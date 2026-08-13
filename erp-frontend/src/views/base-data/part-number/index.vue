<template>
  <div v-permission="['base-data:part-number:query']">
    <page-wrapper content-full-height fixed-height>
      <!-- 数据列表 -->
      <vxe-grid
        id="PartNumber"
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        :columns="tableColumn"
        :toolbar-config="toolbarConfig"
        :custom-config="{}"
        :pager-config="{}"
        :loading="loading"
        height="auto"
        :proxy-config="proxyConfig"
      >
        <template #form>
          <j-border>
            <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
              <j-form-item label="件号">
                <a-input v-model:value="searchFormData.code" allow-clear />
              </j-form-item>
              <j-form-item label="名称">
                <a-input v-model:value="searchFormData.name" allow-clear />
              </j-form-item>
              <j-form-item label="适用机型">
                <a-select v-model:value="searchFormData.machineTypeId" placeholder="全部" allow-clear>
                  <a-select-option
                    v-for="item in machineTypes"
                    :key="item.id"
                    :value="item.id"
                    >{{ item.name }}</a-select-option
                  >
                </a-select>
              </j-form-item>
              <j-form-item label="状态">
                <a-select v-model:value="searchFormData.available" placeholder="全部" allow-clear>
                  <a-select-option
                    v-for="item in $enums.AVAILABLE.values()"
                    :key="item.code"
                    :value="item.code"
                    >{{ item.desc }}</a-select-option
                  >
                </a-select>
              </j-form-item>
            </j-form>
          </j-border>
        </template>
        <!-- 工具栏 -->
        <template #toolbar_buttons>
          <a-space>
            <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
            <a-button
              v-permission="['base-data:part-number:add']"
              type="primary"
              :icon="h(PlusOutlined)"
              @click="$refs.addDialog.openDialog()"
              >新增</a-button
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

    <!-- 新增窗口 -->
    <add ref="addDialog" @confirm="search" />

    <!-- 修改窗口 -->
    <modify :id="id" ref="updateDialog" @confirm="search" />

    <!-- 查看窗口 -->
    <detail :id="id" ref="viewDialog" />
  </div>
</template>

<script>
  import { h, defineComponent } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import * as api from '@/api/base-data/part-number';
  import * as machineTypeApi from '@/api/base-data/machine-type';
  import { PlusOutlined, SearchOutlined } from '@ant-design/icons-vue';

  export default defineComponent({
    name: 'PartNumber',
    components: {
      Add,
      Modify,
      Detail,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        PlusOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        ids: [],
        // 查询列表的查询条件
        searchFormData: {
          available: this.$enums.AVAILABLE.ENABLE.code,
          machineTypeId: '',
        },
        // 机型列表
        machineTypes: [],
        // 工具栏配置
        toolbarConfig: {
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
        tableColumn: [
          { type: 'seq', width: 50 },
          { field: 'code', title: '件号', width: 120, sortable: true },
          { field: 'name', title: '名称', minWidth: 160, sortable: true },
          { field: 'machineTypeName', title: '机型', width: 120, sortable: true },
          { field: 'available', title: '状态', width: 80, slots: { default: 'available_default' } },
          { field: 'description', title: '备注', minWidth: 160 },
          { title: '操作', width: 120, fixed: 'right', slots: { default: 'action_default' } },
        ],
        datas: [
          {
            id: '1',
            code: '001',
            name: 'CFM56-7B-123',
            available: true,
            description: 'LEAP发动机叶片',
            createBy: '系统管理员',
            createTime: '2023-03-21 10:09:44',
            updateBy: '系统管理员',
            updateTime: '2023-03-21 10:12:30',
          },
          {
            id: '2',
            code: '002',
            name: 'PW4000-ABC-456',
            available: true,
            description: '普惠发动机燃油喷嘴',
            createBy: '系统管理员',
            createTime: '2023-03-21 10:09:44',
            updateBy: '系统管理员',
            updateTime: '2023-03-21 10:12:30',
          },
          {
            id: '3',
            code: '003',
            name: 'HONEYWELL-789XYZ',
            available: true,
            description: '气象雷达模块',
            createBy: '系统管理员',
            createTime: '2023-03-21 10:09:44',
            updateBy: '系统管理员',
            updateTime: '2023-03-21 10:12:30',
          },
          {
            id: '4',
            code: '004',
            name: 'BACN10-123',
            available: true,
            description: '机身蒙皮紧固件',
            createBy: '系统管理员',
            createTime: '2023-03-21 10:09:44',
            updateBy: '系统管理员',
            updateTime: '2023-03-21 10:12:30',
          },
          {
            id: '5',
            code: '005',
            name: 'A320-LG-789',
            available: true,
            description: '起落架液压阀',
            createBy: '系统管理员',
            createTime: '2023-03-21 10:09:44',
            updateBy: '系统管理员',
            updateTime: '2023-03-21 10:12:30',
          },
          {
            id: '6',
            code: '006',
            name: 'THALES-EFIS-001',
            available: true,
            description: '电子飞行仪表系统组件',
            createBy: '系统管理员',
            createTime: '2023-03-21 10:09:44',
            updateBy: '系统管理员',
            updateTime: '2023-03-21 10:12:30',
          },
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
    created() {
      // 加载机型列表
      this.loadMachineTypes();
    },
    methods: {
      // 加载机型列表
      loadMachineTypes() {
        machineTypeApi.selector({}).then(res => {
          this.machineTypes = res.datas || [];
        });
      },
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
            permission: ['base-data:part-number:modify'],
            label: '修改',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
        ];
      },
    },
  });
</script>
<style scoped></style>
