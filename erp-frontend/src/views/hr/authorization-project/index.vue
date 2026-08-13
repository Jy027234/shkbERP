<template>
  <div>
    <div v-permission="['hr:authorization:query']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="AuthorizationProject"
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
          :sort-config="{ remote: true }"
          :loading="loading"
          height="530px"
          @checkbox-change="handleCheckboxChange"
          @checkbox-all="handleCheckboxAll"
        >
          <template #form>
            <j-border>
          <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
            <j-form-item label="岗位">
              <a-input v-model:value="searchFormData.projectName" allow-clear />
            </j-form-item>
            <j-form-item label="状态">
              <a-select
                v-model:value="searchFormData.status"
                placeholder="请选择"
                allow-clear
                style="width: 120px"
              >
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">禁用</a-select-option>
              </a-select>
            </j-form-item>
          </j-form>
        </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button :icon="h(SyncOutlined)" @click="reset">重置</a-button>
              <a-button
                v-permission="['hr:authorization:create']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="handleAdd"
                >新增项目</a-button
              >
              <a-button
                v-permission="['hr:authorization:query']"
                :icon="h(DownloadOutlined)"
                @click="handleExport"
                >导出Excel</a-button
              >
              <a-button
                v-permission="['hr:authorization:delete']"
                danger
                :disabled="!selectedRowKeys.length"
                :icon="h(DeleteOutlined)"
                @click="handleBatchDelete"
                >批量删除</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #status_default="{ row }">
            <a-tag :color="row.status === 1 ? 'success' : 'default'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </a-tag>
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

    <!-- 必修课程设置弹窗 -->
    <a-modal
      v-model:open="courseModalVisible"
      title="必修课程设置"
      @ok="handleCourseModalSubmit"
      @cancel="handleCourseModalCancel"
      width="800px"
    >
      <div v-if="courseModalVisible" class="course-modal-content">
        <div class="modal-tip">设置该授权项目的必修课程，参与授权的人员需完成以下课程才能获得有效授权</div>
        <a-transfer
          v-model:target-keys="selectedCourseIds"
          :data-source="courseDataSource"
          :titles="['可选课程', '必修课程']"
          :render="item => item.title"
          show-search
          :filter-option="(inputValue, item) => item.title.indexOf(inputValue) !== -1"
        />
      </div>
    </a-modal>
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import * as api from '@/api/hr/authorization-project';
  import {
    SearchOutlined,
    SyncOutlined,
    PlusOutlined,
    DeleteOutlined,
    DownloadOutlined
  } from '@ant-design/icons-vue';
  
  const courseList = [
    { id: '1', courseName: '安全生产培训', courseType: '安全培训' },
    { id: '2', courseName: '维修技能培训', courseType: '技能培训' },
    { id: '3', courseName: '设备操作规程', courseType: '操作规程' },
    { id: '4', courseName: '高空作业培训', courseType: '作业培训' },
    { id: '5', courseName: '应急救援培训', courseType: '安全培训' },
    { id: '6', courseName: '质量管理体系培训', courseType: '管理培训' }
  ];

  export default defineComponent({
    name: 'HrAuthorizationProject',
    components: {
      Add,
      Modify,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        SyncOutlined,
        PlusOutlined,
        DeleteOutlined,
        DownloadOutlined,
        courseList,
      };
    },
    data() {
      return {
        loading: false,
        id: '',
        selectedRows: [],
        selectedRowKeys: [],
        courseModalVisible: false,
        currentProjectId: '',
        selectedCourseIds: [],
        courseList: courseList,
        searchFormData: {
          projectName: '',
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        },
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        tableColumn: [
          { type: 'checkbox', width: 50 },
          { type: 'seq', width: 50 },
          { field: 'projectName', title: '岗位', width: 200 },
          { field: 'authorizationItem', title: '授权项目/限制', width: 250 },
          { field: 'qualificationRequirement', title: '资质要求', width: 250 },
          { field: 'trainingRequirement', title: '培训要求', width: 250 },
          {
            field: 'validityPeriod',
            title: '有效期',
            width: 120,
            formatter: function({ row }) {
              const period = row.validityPeriod || 0;
              const unit = row.validityUnit === 'month' ? '个月' : '年';
              return `${period}${unit}`;
            }
          },
          { field: 'description', title: '备注', minWidth: 150 },
          { field: 'status', title: '状态', width: 100, slots: { default: 'status_default' } },
          { field: 'createTime', title: '创建时间', width: 180 },
          { title: '操作', width: 200, fixed: 'right', slots: { default: 'action_default' } },
        ],
        proxyConfig: {
          props: {
            result: 'datas',
            total: 'total',
          },
          ajax: {
            query: ({ page, sorts }) => {
              const params = {
                ...this.$utils.buildSortPageVo(page, sorts),
                ...this.buildSearchFormData(),
              };
              return api.query(params).then((res) => {
                return res;
              });
            },
          },
        },
      };
    },
    created() {
      this.loadCourseList();
    },
    computed: {
      courseDataSource() {
        return this.courseList.map(course => ({
          key: course.id,
          title: course.courseName,
          description: course.courseType
        }));
      }
    },
    methods: {
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      
      buildQueryParams(page, sorts) {
        const params = {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };
        return params;
      },
      
      buildSearchFormData() {
        return {
          projectName: this.searchFormData.projectName,
          status: this.searchFormData.status,
        };
      },
      
      reset() {
        this.searchFormData = {
          projectName: '',
          status: undefined,
          pageIndex: 1,
          pageSize: 10
        };
        if (this.$refs.grid && this.$refs.grid.clearSort) {
          this.$refs.grid.clearSort();
        }
        this.search();
      },
      
      createActions(row) {
        return [
          {
            permission: ['hr:authorization:update'],
            label: '编辑',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
          {
            permission: ['hr:authorization:update'],
            label: '必修课程',
            onClick: () => {
              this.handleCourses(row);
            },
          },
          {
            permission: ['hr:authorization:delete'],
            label: '删除',
            type: 'danger',
            onClick: () => {
              this.handleDelete(row);
            },
          },
        ];
      },
      
      handleCheckboxChange({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },

      handleCheckboxAll({ records }) {
        this.selectedRows = records;
        this.selectedRowKeys = records.map(row => row.id);
      },
      
      handleAdd() {
        this.$nextTick(() => this.$refs.addDialog.openDialog());
      },
      
      async handleCourses(row) {
        this.currentProjectId = row.id;
        try {
          const courseIds = await api.getRequiredCourses(row.id);
          this.selectedCourseIds = courseIds || [];
        } catch (error) {
          // 错误已在拦截器处理
          this.selectedCourseIds = [];
        }
        this.courseModalVisible = true;
      },
      
      loadCourseList() {
      },
      
      async handleCourseModalSubmit() {
        if (!this.currentProjectId) return;
        
        try {
          await api.saveRequiredCourses(this.currentProjectId, this.selectedCourseIds);
          this.$message.success('必修课程设置成功');
          this.courseModalVisible = false;
          this.selectedCourseIds = [];
          this.currentProjectId = '';
          this.search();
        } catch (error) {
          // 错误已在拦截器处理
        }
      },
      
      handleCourseModalCancel() {
        this.courseModalVisible = false;
        this.selectedCourseIds = [];
        this.currentProjectId = '';
      },
      
      handleDelete(record) {
        this.$confirm({
          title: '确定要删除该项目吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.del(record.id);
              this.$message.success('删除成功');
              this.search();
            } catch (error) {
              // 错误已在拦截器处理
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      handleBatchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择要删除的项目');
          return;
        }
        
        this.$confirm({
          title: '确定要批量删除所选项目吗？',
          onOk: async () => {
            this.loading = true;
            try {
              await api.batchDelete(this.selectedRowKeys);
              this.$message.success(`成功删除 ${this.selectedRowKeys.length} 个项目`);
              this.selectedRowKeys = [];
              this.search();
            } catch (error) {
              // 错误已在拦截器处理
            } finally {
              this.loading = false;
            }
          }
        });
      },
      
      handleExport() {
        const params = this.buildSearchFormData();
        api.exportList(params).then((res) => {
          if (res) {
            const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `授权项目_${new Date().toLocaleDateString()}.xlsx`);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
            this.$message.success('导出成功');
          }
        });
      },
    },
  });
</script>

<style scoped>
  .course-modal-content {
    .modal-tip {
      margin-bottom: 16px;
      color: #666;
      font-size: 14px;
    }
  }
</style>
