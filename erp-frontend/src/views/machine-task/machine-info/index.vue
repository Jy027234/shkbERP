<template>
  <div v-permission="['machine-task:machine-info']">
    <page-wrapper content-full-height fixed-height>
      <!-- 数据列表 -->
      <vxe-grid
        id="MachineInfo"
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        :proxy-config="proxyConfig"
        :columns="tableColumn"
        :toolbar-config="toolbarConfig"
        :custom-config="{}"
        :pager-config="{}"
        :loading="loading"
        height="auto"
      >
        <template #form>
          <j-border>
            <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
              <j-form-item label="设备ID">
                <a-input v-model:value="searchFormData.machineId" allow-clear />
              </j-form-item>
              <j-form-item label="设备名称">
                <a-input v-model:value="searchFormData.machineName" allow-clear />
              </j-form-item>
              <j-form-item label="设备类型">
                <a-select v-model:value="searchFormData.machineType" placeholder="全部" allow-clear>
                  <a-select-option :value="1">线束检测机</a-select-option>
                  <a-select-option :value="2">磁粉机</a-select-option>
                </a-select>
              </j-form-item>
            </j-form>
          </j-border>
        </template>

        <!-- 设备类型渲染 -->
        <template #machineType_default="{ row }">
          <a-tag v-if="row.machineType === 1" color="blue">线束检测机</a-tag>
          <a-tag v-else-if="row.machineType === 2" color="purple">磁粉机</a-tag>
          <span v-else>-</span>
        </template>

        <!-- 设备状态渲染：5分钟内有访问为在线 -->
        <template #status_default="{ row }">
          <a-tag v-if="isOnline(row)" color="green">在线</a-tag>
          <a-tag v-else color="default">离线</a-tag>
        </template>

        <!-- 操作 列自定义内容 -->
        <template #action_default="{ row }">
          <table-action outside :actions="createActions(row)" />
        </template>

        <!-- 工具栏 自定义按钮槽位 -->
        <template #toolbar_buttons>
          <a-space>
            <a-button type="primary" @click="search">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
          </a-space>
        </template>
      </vxe-grid>
    </page-wrapper>
    <!-- 编辑设备弹窗 -->
    <a-modal
      v-model:open="showEdit"
      title="编辑设备"
      :confirm-loading="saving"
      @ok="submitEdit"
      @cancel="showEdit = false"
    >
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="设备名称" required>
          <a-input v-model:value="editForm.machineName" />
        </a-form-item>
        <a-form-item label="IP地址">
          <a-input v-model:value="editForm.ipAddress" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import {
    SearchOutlined,
    PlusOutlined,
    ThunderboltOutlined,
    SettingOutlined,
    CheckOutlined,
    StopOutlined,
    DownOutlined,
    CloudUploadOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/machine-task/machine-info';

  export default defineComponent({
    name: 'MachineTaskMachineInfo',
    components: {
      DownOutlined,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        PlusOutlined,
        ThunderboltOutlined,
        SettingOutlined,
        CheckOutlined,
        StopOutlined,
        CloudUploadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        saving: false,
        // 当前行数据
        id: '',
        ids: [],
        showEdit: false,
        editForm: {
          id: '',
          machineName: '',
          ipAddress: '',
        },
        // 查询列表的查询条件
        searchFormData: {
          machineId: '',
          machineName: '',
          machineType: undefined,
        },
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 45 },
          { field: 'machineId', title: '设备ID', width: 200 },
          { field: 'machineName', title: '设备名称', minWidth: 150 },
          { field: 'machineType', title: '设备类型', width: 120, slots: { default: 'machineType_default' } },
          { field: 'status', title: '设备状态', width: 120, slots: { default: 'status_default' } },
          { field: 'ipAddress', title: 'IP地址', width: 180 },
          { field: 'visitTime', title: '最近心跳时间', width: 200 },
          { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
        ],
        // 请求接口配置
        proxyConfig: {
          props: {
            // 响应结果列表字段
            result: 'datas',
            // 响应结果总条数字段
            total: 'totalCount',
          },
          ajax: {
            // 查询接口
            query: ({ page, sorts }) => {
              return api.query(this.buildQueryParams(page, sorts));
            },
          },
        },
        batchHandleDatas: [],
      };
    },
    created() {},
    methods: {
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        return {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
      },
      // 查询前构建具体的查询参数
      buildSearchFormData() {
        return {
          ...this.searchFormData,
        };
      },
      handleCommand() {},
      isOnline(row) {
        if (!row || !row.visitTime) return false;
        const t = new Date(row.visitTime).getTime();
        if (Number.isNaN(t)) return false;
        return Date.now() - t <= 5 * 60 * 1000;
      },
      createActions(row) {
        return [
          {
            label: '编辑',
            onClick: () => {
              this.editForm = {
                id: row.id,
                machineName: row.machineName,
                ipAddress: row.ipAddress,
              };
              this.showEdit = true;
            },
          },
        ];
      },
      submitEdit() {
        if (!this.editForm.machineName) {
          this.$msg.createWarning('设备名称不能为空');
          return;
        }
        this.saving = true;
        api
          .update({
            id: this.editForm.id,
            machineName: this.editForm.machineName,
            ipAddress: this.editForm.ipAddress,
          })
          .then(() => {
            this.$msg.createSuccess('保存成功');
            this.showEdit = false;
            this.$refs.grid.commitProxy('reload');
          })
          .finally(() => {
            this.saving = false;
          });
      },
    },
  });
</script>
<style scoped></style>
