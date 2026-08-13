<template>
  <div>
    <div v-permission="['equipment:tool']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="Tool"
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
          height="auto"
          @checkbox-change="handleCheckboxChange"
          @checkbox-all="handleCheckboxAll"
        >
          <template #form>
            <j-border>
              <j-form label-width="80px" @collapse="$refs.grid.refreshColumn()">
                <!-- 常显的核心筛选项 -->
                <j-form-item label="管理区域">
                  <a-input v-model:value="searchFormData.managementArea" allow-clear />
                </j-form-item>
                <j-form-item label="设备名称">
                  <a-input v-model:value="searchFormData.name" allow-clear />
                </j-form-item>
                <j-form-item label="管理编号">
                  <a-input v-model:value="searchFormData.code" allow-clear />
                </j-form-item>
                <j-form-item label="证书编号">
                  <a-input v-model:value="searchFormData.certificateNumber" allow-clear />
                </j-form-item>
                <j-form-item label="规格">
                    <a-input v-model:value="searchFormData.specification" allow-clear />
                  </j-form-item>
                  <j-form-item label="型号">
                    <a-input v-model:value="searchFormData.model" allow-clear />
                  </j-form-item>

                <!-- 更多筛选项折叠区域 -->
                <template #more>
                  <j-form-item label="计量标准">
                    <a-input v-model:value="searchFormData.standard" allow-clear />
                  </j-form-item>
                  <j-form-item label="精度">
                    <a-input v-model:value="searchFormData.precision" allow-clear />
                  </j-form-item>
                  <j-form-item label="存放位置">
                    <a-input v-model:value="searchFormData.storageLocation" allow-clear />
                  </j-form-item>
                  <j-form-item label="上次计量日期" :content-nest="false">
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
                  <j-form-item label="下次计量日期" :content-nest="false">
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
                  <j-form-item label="有效期" :content-nest="false">
                    <div class="date-range-container">
                      <a-date-picker
                        v-model:value="searchFormData.expirationTimeStart"
                        placeholder=""
                        value-format="YYYY-MM-DD 00:00:00"
                      />
                      <span class="date-split">至</span>
                      <a-date-picker
                        v-model:value="searchFormData.expirationTimeEnd"
                        placeholder=""
                        value-format="YYYY-MM-DD 23:59:59"
                      />
                    </div>
                  </j-form-item>
                  <j-form-item label="计量周期">
                    <a-input v-model:value="searchFormData.calibrationPeriod" allow-clear />
                  </j-form-item>
                  <j-form-item label="上次维保单位">
                    <a-input v-model:value="searchFormData.lastMaintenanceUnit" allow-clear />
                  </j-form-item>
                  <j-form-item label="状态">
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
                </template>
              </j-form>
            </j-border>
          </template>
          <!-- 工具栏 -->
          <template #toolbar_buttons>
            <a-space>
              <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
              <a-button :icon="h(SyncOutlined)" @click="reset">重置</a-button>
              <a-button
                v-permission="['equipment:tool']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="$refs.addDialog.openDialog()"
                >新增</a-button
              >
              <a-button
                v-permission="['equipment:tool']"
                type="primary"
                :icon="h(FieldTimeOutlined)"
                @click="openToolRecords"
                >计量记录管理</a-button
              >
              <a-button
                v-permission="['equipment:tool']"
                :icon="h(PaperClipOutlined)"
                @click="openToolAttachment"
                >附件管理</a-button
              >
            </a-space>
          </template>

          <!-- 状态 列自定义内容 -->
          <template #available_default="{ row }">
            <a-tag :color="row.available ? 'green' : 'red'">
              {{ row.available ? '启用' : '停用' }}
            </a-tag>
          </template>

          <!-- 有效期 列自定义内容（仅文字变色） -->
          <template #expiration_default="{ row }">
            <span class="expiration-text" :class="getExpirationClass(row.expirationTime)">
              {{ row.expirationTime || '-' }}
            </span>
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

    <!-- 计量记录管理窗口 -->
    <tool-records ref="toolRecords" :tool-ids="selectedToolIds" @confirm="search" />

    <!-- 附件管理窗口 -->
    <tool-attachment ref="toolAttachment" />
  </div>
</template>

<script>
  import { defineComponent, h } from 'vue';
  import Add from './add.vue';
  import Modify from './modify.vue';
  import Detail from './detail.vue';
  import ToolRecords from './tool-records.vue';
  import ToolAttachment from './tool-attachment.vue';
  import {
    CheckOutlined,
    CloudUploadOutlined,
    DownOutlined,
    FieldTimeOutlined,
    PaperClipOutlined,
    PlusOutlined,
    SearchOutlined,
    SyncOutlined,
    ThunderboltOutlined,
    ToolOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/equipment/tool';

  export default defineComponent({
    name: 'EquipmentTool',
    components: {
      Add,
      Modify,
      Detail,
      ToolRecords,
      ToolAttachment,
      DownOutlined,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        PlusOutlined,
        CloudUploadOutlined,
        CheckOutlined,
        ThunderboltOutlined,
        ToolOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        // 选中的行数据
        selectedRows: [], // 选中的行数据
        // 选中的工具ID列表
        selectedToolIds: [],
        // 查询列表的查询条件
        searchFormData: {
          managementArea: '',
          name: '',
          code: '',
          certificateNumber: '',
          specification: '',
          model: '',
          standard: '',
          precision: '',
          storageLocation: '',
          lastMaintenanceTimeStart: '',
          lastMaintenanceTimeEnd: '',
          nextMaintenanceTimeStart: '',
          nextMaintenanceTimeEnd: '',
          calibrationPeriod: '',
          expirationTimeStart: '',
          expirationTimeEnd: '',
          lastMaintenanceUnit: '',
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
          { field: 'expirationTime', title: '有效期', width: 150, sortable: true, slots: { default: 'expiration_default' } },
          { field: 'managementArea', title: '管理区域', width: 100 },
          { field: 'name', title: '设备名称', width: 100 },
          { field: 'code', title: '管理编号', width: 180 },
          { field: 'certificateNumber', title: '证书编号', width: 180 },
          { field: 'specification', title: '规格', width: 180 },
          { field: 'model', title: '型号', width: 180 },
          { field: 'standard', title: '计量标准', width: 180 },
          { field: 'precision', title: '精度', width: 180 },
          { field: 'storageLocation', title: '存放位置', width: 180 },
          { field: 'lastMaintenanceTime', title: '上次计量日期', width: 180},
          { field: 'nextMaintenanceTime', title: '下次计量日期', width: 180 },
          { field: 'calibrationPeriod', title: '计量周期', width: 180},
          { field: 'lastMaintenanceUnit', title: '上次维保单位', width: 180 },
          {
            field: 'available',
            title: '状态',
            width: 100,
            slots: { default: 'available_default' },
          },
          { field: 'description', title: '备注', minWidth: 200 },
          { field: 'createBy', title: '创建人', width: 100 },
          { field: 'createTime', title: '创建时间', width: 170 },
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
      // 根据有效期返回文字样式类名（仅改变颜色）
      getExpirationClass(v) {
        if (!v) return '';
        const d = new Date(String(v).replace(/-/g, '/'));
        if (isNaN(d.getTime())) return '';
        const today = new Date();
        const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        const diffMs = d.getTime() - startOfToday.getTime();
        const diffDays = Math.floor(diffMs / (24 * 60 * 60 * 1000));
        if (diffDays < 0) {
          return 'text-expired';
        } else if (diffDays <= 10) {
          return 'text-due-10';
        } else if (diffDays <= 30) {
          return 'text-due-30';
        }
        return '';
      },
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('query');
      },
      // 查询前构建查询参数结构
      buildQueryParams(page, sorts) {
        const params = {
          ...this.$utils.buildSortPageVo(page, sorts),
          ...this.buildSearchFormData(),
        };

        // 将 vxe 的排序映射为后端的排序参数
        // vxe 的 sorts 可能为数组或对象，兼容两种结构
        const sortItems = Array.isArray(sorts) ? sorts : (sorts ? [sorts] : []);
        if (sortItems.length > 0) {
          // 仅处理我们关心的两列：expirationTime / createTime
          for (const s of sortItems) {
            const field = s.property || s.field || s.column?.property || s.column?.field;
            const order = (s.order || '').toLowerCase(); // 期望 'asc' 或 'desc'
            if (field === 'expirationTime') {
              params.expirationTimeOrder = order === 'asc' || order === 'desc' ? order : undefined;
            }
            if (field === 'createTime') {
              params.createTimeOrder = order === 'asc' || order === 'desc' ? order : undefined;
            }
          }
        }

        return params;
      },
      // 查询前构建具体的查询参数
      buildSearchFormData() {
        const data = {
          managementArea: this.searchFormData.managementArea,
          name: this.searchFormData.name,
          code: this.searchFormData.code,
          certificateNumber: this.searchFormData.certificateNumber,
          specification: this.searchFormData.specification,
          precision: this.searchFormData.precision,
          standard: this.searchFormData.standard,
          storageLocation: this.searchFormData.storageLocation,
          lastMaintenanceTimeStart: this.searchFormData.lastMaintenanceTimeStart,
          lastMaintenanceTimeEnd: this.searchFormData.lastMaintenanceTimeEnd,
          nextMaintenanceTimeStart: this.searchFormData.nextMaintenanceTimeStart,
          nextMaintenanceTimeEnd: this.searchFormData.nextMaintenanceTimeEnd,
          calibrationPeriod: this.searchFormData.calibrationPeriod,
          expirationTimeStart: this.searchFormData.expirationTimeStart || undefined,
          expirationTimeEnd: this.searchFormData.expirationTimeEnd || undefined,
          lastMaintenanceUnit: this.searchFormData.lastMaintenanceUnit,
          createTimeStart: this.searchFormData.createTimeStart,
          createTimeEnd: this.searchFormData.createTimeEnd,
        };

        // 只有当 available 有值时才传递给后端，否则传递 undefined
        if (this.searchFormData.available !== undefined && this.searchFormData.available !== null) {
          data.available = Boolean(this.searchFormData.available);
        }

        return data;
      },
      // 重置筛选项为默认值
      reset() {
        this.searchFormData = {
          managementArea: '',
          name: '',
          code: '',
          certificateNumber: '',
          specification: '',
          model: '',
          standard: '',
          precision: '',
          storageLocation: '',
          lastMaintenanceTimeStart: '',
          lastMaintenanceTimeEnd: '',
          nextMaintenanceTimeStart: '',
          nextMaintenanceTimeEnd: '',
          calibrationPeriod: '',
          expirationTimeStart: '',
          expirationTimeEnd: '',
          lastMaintenanceUnit: '',
          createTimeStart: '',
          createTimeEnd: '',
          available: this.$enums.AVAILABLE.ENABLE.code,
        };
        // 清除表格排序状态，避免残留排序影响查询
        if (this.$refs.grid && this.$refs.grid.clearSort) {
          this.$refs.grid.clearSort();
        }
        this.search();
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
            permission: ['equipment:tool'],
            label: '修改',
            onClick: () => {
              this.id = row.id;
              this.$nextTick(() => this.$refs.updateDialog.openDialog());
            },
          },
        ];
      },
      
      // 处理表格行选择变化
      handleCheckboxChange({ records }) {
        this.selectedRows = records;
        this.selectedToolIds = records.map(row => row.id);
      },

      // 处理表格全选/取消全选
      handleCheckboxAll({ records }) {
        this.selectedRows = records;
        this.selectedToolIds = records.map(row => row.id);
      },

      // 打开工具计量记录管理窗口
      openToolRecords() {
        if (this.selectedRows.length === 0) {
          this.$message.warning('请选择要管理计量记录的工具');
          return;
        }
        
        this.$nextTick(() => this.$refs.toolRecords.openDialog());
      },

      // 打开工具附件管理窗口
      openToolAttachment() {
        if (this.selectedRows.length === 0) {
          this.$message.warning('请选择要管理附件的工具');
          return;
        }
        
        if (this.selectedRows.length > 1) {
          this.$message.warning('附件管理只能选择一个工具');
          return;
        }
        
        const toolId = this.selectedRows[0].id;
        this.$nextTick(() => this.$refs.toolAttachment.openDialog(toolId));
      },
    },
  });
</script>
<style scoped>
/* 仅针对“有效期”列的文字着色，不再渲染整行背景色 */
.expiration-text.text-expired {
  color: #f41b07; /* 已过期：红色 */
  font-weight: 600;
}

.expiration-text.text-due-10 {
  color: #c85700; /* 10天内到期：橙色偏深 */
  font-weight: 600;
}

.expiration-text.text-due-30 {
  color: #deb35d; /* 30天内到期：橙黄色 */
  font-weight: 600;
}
</style>
