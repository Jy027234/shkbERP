<template>
  <div>
    <div v-permission="['equipment:device']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="Device"
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
                <j-form-item label="管理区域">
                  <a-input v-model:value="searchFormData.managementArea" allow-clear />
                </j-form-item>
                <j-form-item label="设备编号">
                  <a-input v-model:value="searchFormData.code" allow-clear />
                </j-form-item>
                <j-form-item label="设备名称">
                  <a-input v-model:value="searchFormData.name" allow-clear />
                </j-form-item>
                <j-form-item label="维保项目">
                  <a-input v-model:value="searchFormData.maintenanceProject" allow-clear />
                </j-form-item>
                <j-form-item label="维保间隔（天）">
                  <a-input v-model:value="searchFormData.maintenanceInterval" allow-clear />
                </j-form-item>
                <j-form-item label="上次维保时间" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.lastMaintenanceTimeStart"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.lastMaintenanceTimeEnd"
                      placeholder=""
                      value-format="YYYY-MM-DD 23:59:59"
                    />
                  </div>
                </j-form-item>
                <j-form-item label="下次维保时间" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.nextMaintenanceTimeStart"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.nextMaintenanceTimeEnd"
                      placeholder=""
                      value-format="YYYY-MM-DD 23:59:59"
                    />
                  </div>
                </j-form-item>
                <j-form-item label="维保工卡">
                  <a-input v-model:value="searchFormData.maintenanceCard" allow-clear />
                </j-form-item>
                <j-form-item label="设备状态">
                  <a-select v-model:value="searchFormData.available" allow-clear>
                    <a-select-option
                      v-for="item in $enums.AVAILABLE.values()"
                      :key="item.code"
                      :value="item.code"
                      >{{ item.desc }}</a-select-option
                    >
                  </a-select>
                </j-form-item>
                <j-form-item label="创建时间" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.createTimeStart"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.createTimeEnd"
                      placeholder=""
                      value-format="YYYY-MM-DD 23:59:59"
                    />
                  </div>
                </j-form-item>
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button
                v-permission="['equipment:device']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="$refs.addDialog.openDialog()"
                >新增</a-button
              >
              <a-button
                v-permission="['equipment:device']"
                danger
                :icon="h(DeleteOutlined)"
                @click="batchDelete"
                >批量删除</a-button
              >
              <a-button
                v-permission="['equipment:device']"
                type="primary"
                :icon="h(ToolOutlined)"
                @click="openMaintenanceRecords"
                >维保记录管理</a-button
              >
              <a-button
                v-permission="['equipment:device']"
                :icon="h(PaperClipOutlined)"
                @click="openDeviceAttachment"
                >附件管理</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #available_default="{ row }">
            <available-tag :available="row.available" />
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>
    </div>
    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="search" />

    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="search" />

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />

    <device-importer ref="importer" @confirm="search" />
    
    <!-- 维保记录管理窗口 -->
    <maintenance-records ref="maintenanceRecords" :device-ids="selectedDeviceIds" @confirm="search" />

    <!-- 附件管理窗口 -->
    <device-attachment ref="deviceAttachment" />
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import MaintenanceRecords from './maintenance-records.vue';
  import DeviceAttachment from './device-attachment.vue';
  import {
    CheckOutlined,
    CloudUploadOutlined,
    DownOutlined,
    PlusOutlined,
    SearchOutlined,
    SettingOutlined,
    StopOutlined,
    ThunderboltOutlined,
    DeleteOutlined,
    ToolOutlined,
    PaperClipOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/equipment';

  export default defineComponent({
    name: 'EquipmentDevice',
    components: {
      Add,
      Modify,
      Detail,
      MaintenanceRecords,
      DownOutlined,
      DeleteOutlined,
      DeviceAttachment,
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
        DeleteOutlined,
        PaperClipOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        // 选中的设备ID
        selectedDeviceIds: [],
        // 查询列表的查询条件
        searchFormData: {
          managementArea: '',
          code: '',
          name: '',
          maintenanceProject: '',
          maintenanceInterval: '',
          lastMaintenanceTimeStart: '',
          lastMaintenanceTimeEnd: '',
          nextMaintenanceTimeStart: '',
          nextMaintenanceTimeEnd: '',
          maintenanceCard: '',
          createTimeStart: '',
          createTimeEnd: '',
          available: this.$enums.AVAILABLE.ENABLE.code,
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
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'managementArea', title: '管理区域', width: 100 },
          { field: 'code', title: '设备编号', width: 100 },
          { field: 'name', title: '设备名称', width: 180 },
          { field: 'maintenanceProject', title: '维保项目', width: 180 },
          { field: 'maintenanceInterval', title: '维保间隔', width: 180, sortable: true },
          { field: 'lastMaintenanceTime', title: '上次维保时间', width: 180, sortable: true },
          { field: 'nextMaintenanceTime', title: '下次维保时间', width: 180, sortable: true },
          { field: 'maintenanceCard', title: '维保工卡', width: 180, sortable: true },
          {
            field: 'available',
            title: '设备状态',
            width: 100,
            slots: { default: 'available_default' },
          },
          { field: 'description', title: '备注', minWidth: 200 },
          { field: 'createBy', title: '创建人', width: 100 },
          { field: 'createTime', title: '创建时间', width: 170, sortable: true },
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
          managementArea: this.searchFormData.managementArea,
          code: this.searchFormData.code,
          name: this.searchFormData.name,
          maintenanceProject: this.searchFormData.maintenanceProject,
          maintenanceInterval: this.searchFormData.maintenanceInterval,
          lastMaintenanceTimeStart: this.searchFormData.lastMaintenanceTimeStart,
          lastMaintenanceTimeEnd: this.searchFormData.lastMaintenanceTimeEnd,
          nextMaintenanceTimeStart: this.searchFormData.nextMaintenanceTimeStart,
          nextMaintenanceTimeEnd: this.searchFormData.nextMaintenanceTimeEnd,
          maintenanceCard: this.searchFormData.maintenanceCard,
          createTimeStart: this.searchFormData.createTimeStart,
          createTimeEnd: this.searchFormData.createTimeEnd,
          available: this.searchFormData.available,
        };
      },
      createActions(row) {
        return [
          {
            label: '查看',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.viewDialog.openDialog());
            },
          },
          {
            permission: ['equipment:device'],
            label: '修改',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          }
        ];
      },
      
      // 删除设备
      deleteDevice(id) {
        this.$dialog.confirm({
          title: '提示',
          content: '确认删除该设备？',
          onOk: async () => {
            await api.deleteById(id);
            this.$message.success('删除成功');
            this.search();
          },
        });
      },
      
      // 批量删除设备
      batchDelete() {
        const selectedRows = this.$refs.grid.getCheckboxRecords();
        if (selectedRows.length === 0) {
          this.$message.warning('请选择要删除的设备');
          return;
        }
        
        this.$dialog.confirm({
          title: '提示',
          content: `确认删除选中的 ${selectedRows.length} 个设备？`,
          onOk: async () => {
            const ids = selectedRows.map(row => row.id);
            await api.batchDelete(ids);
            this.$message.success('删除成功');
            this.search();
          },
        });
      },
      
      // 打开维保记录管理窗口
      openMaintenanceRecords() {
        const selectedRows = this.$refs.grid.getCheckboxRecords();
        if (selectedRows.length === 0) {
          this.$message.warning('请选择要管理维保记录的设备');
          return;
        }
        
        this.selectedDeviceIds = selectedRows.map(row => row.id);
        this.$nextTick(() => this.$refs.maintenanceRecords.openDialog());
      },
      // 打开附件管理窗口
      openDeviceAttachment() {
        const selectedRows = this.$refs.grid.getCheckboxRecords();
        if (selectedRows.length === 0) {
          this.$message.warning('请选择要管理附件的设备');
          return;
        }

        if (selectedRows.length > 1) {
          this.$message.warning('附件管理只能选择一个设备');
          return;
        }

        const deviceId = selectedRows[0].id;
        this.$nextTick(() => this.$refs.deviceAttachment.openDialog(deviceId));
      },
    },
  });
</script>
<style scoped></style>
