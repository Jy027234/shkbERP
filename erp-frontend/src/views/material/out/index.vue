<template>
  <div>
    <div v-permission="['material:out']">
      <page-wrapper content-full-height fixed-height>
        <!-- 数据列表 -->
        <vxe-grid
          id="MaterialOutSheet"
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
              <j-form @collapse="$refs.grid.refreshColumn()">
                <j-form-item label="出库单号">
                  <a-input v-model:value="searchFormData.code" allow-clear />
                </j-form-item>

                <j-form-item label="仓库">
                  <store-center-selector v-model:value="searchFormData.scId" />
                </j-form-item>

                <j-form-item v-show="false" label="操作人">
                  <user-selector v-model:value="searchFormData.createBy" />
                </j-form-item>

                <j-form-item v-show="false" label="操作日期" :content-nest="false">
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

                <j-form-item label="审核人">
                  <user-selector v-model:value="searchFormData.approveBy" />
                </j-form-item>

                <j-form-item label="发料单号">
                    <a-input v-model:value="searchFormData.materialOrderCode" allow-clear />
                  </j-form-item>

                <j-form-item label="合同编号">
                    <a-input v-model:value="searchFormData.contractCode" allow-clear />
                  </j-form-item>

                <j-form-item label="状态">
                  <a-select v-model:value="searchFormData.status" placeholder="全部" allow-clear>
                    <a-select-option
                      v-for="item in $enums.MATERIAL_OUT_SHEET_STATUS.values()"
                      :key="item.code"
                      :value="item.code"
                      >{{ item.desc }}</a-select-option
                    >
                  </a-select>
                </j-form-item>
                <j-form-item label="发料日期" :content-nest="false">
                  <div class="date-range-container">
                    <a-date-picker
                      v-model:value="searchFormData.approveTimeStart"
                      placeholder=""
                      value-format="YYYY-MM-DD 00:00:00"
                    />
                    <span class="date-split">至</span>
                    <a-date-picker
                      v-model:value="searchFormData.approveTimeEnd"
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
                v-permission="['material:out']"
                type="primary"
                :icon="h(PlusOutlined)"
                @click="openAddDialog"
                >新增</a-button
              >
              <a-button
                v-permission="['material:out']"
                @click="batchApproveRefuse"
                >可领料</a-button
              >
              <a-button
                v-permission="['material:out']"
                @click="batchApprovePass"
                >发料出库</a-button
              >
              <a-button
                v-show="false"
                v-permission="['material:out']"
                danger
                :icon="h(DeleteOutlined)"
                @click="batchDelete"
                >批量删除</a-button
              >
              <a-button
                v-show="false"
                v-permission="['material:out']"
                :icon="h(DownloadOutlined)"
                @click="exportList"
                >导出</a-button
              >
            </a-space>
          </template>

          <!-- 发料单号 列自定义内容 -->
          <template #materialOrderCode_default="{ row }">
            <span v-if="$utils.isEmpty(row.materialOrderCode)">-</span>
            <span v-else>
              <a
                v-permission="['material:order:query']"
                @click="viewMaterialOrderDetail(row.materialOrderId)"
                >{{ row.materialOrderCode }}</a
              >
              <span v-no-permission="['material:order:query']">{{ row.materialOrderCode }}</span>
            </span>
          </template>

          <!-- 状态 列自定义内容：用不同颜色标签区分状态 -->
          <template #status_default="{ row }">
            <!-- 0：备料中 -->
            <a-tag v-if="$enums.MATERIAL_OUT_SHEET_STATUS.PREPARING.equalsCode(row.status)" color="blue">
              {{ $enums.MATERIAL_OUT_SHEET_STATUS.getDesc(row.status) }}
            </a-tag>
            <!-- 1：已发料 -->
            <a-tag v-else-if="$enums.MATERIAL_OUT_SHEET_STATUS.ISSUED.equalsCode(row.status)" color="green">
              {{ $enums.MATERIAL_OUT_SHEET_STATUS.getDesc(row.status) }}
            </a-tag>
            <!-- 2：可领料 -->
            <a-tag v-else-if="$enums.MATERIAL_OUT_SHEET_STATUS.PICKABLE.equalsCode(row.status)" color="orange">
              {{ $enums.MATERIAL_OUT_SHEET_STATUS.getDesc(row.status) }}
            </a-tag>
            <span v-else>{{ $enums.MATERIAL_OUT_SHEET_STATUS.getDesc(row.status) }}</span>
          </template>

          <!-- 操作 列自定义内容 -->
          <template #action_default="{ row }">
            <table-action outside :actions="createActions(row)" />
          </template>
        </vxe-grid>
      </page-wrapper>

      <!-- 查看窗口 -->
      <detail :id="id" ref="viewDialog" />

      <approve-refuse ref="approveRefuseDialog" @confirm="doApproveRefuse" />

      <!-- 发料单查看窗口 -->
      <material-order-detail :id="materialOrderId" ref="viewMaterialOrderDetailDialog" />

      <!-- 批量操作 -->
      <batch-handler
        ref="batchApprovePassHandlerDialog"
        :table-column="[
          { field: 'code', title: '出库单号', width: 180 },
          { field: 'scName', title: '仓库名称', width: 120 },
        ]"
        title="发料出库"
        :tableData="batchHandleDatas"
        :handle-fn="doBatchApprovePass"
        @confirm="search"
      />
      <batch-handler
        ref="batchApproveRefuseHandlerDialog"
        :table-column="[
          { field: 'code', title: '出库单号', width: 180 },
          { field: 'scName', title: '仓库名称', width: 120 },
        ]"
        title="可领料操作"
        :tableData="batchHandleDatas"
        :handle-fn="doBatchApproveRefuse"
        @confirm="search"
      />
      <batch-handler
        ref="batchDeleteHandlerDialog"
        :table-column="[
          { field: 'code', title: '出库单号', width: 180 },
          { field: 'scCode', title: '仓库编号', width: 100 },
          { field: 'scName', title: '仓库名称', width: 120 },
        ]"
        title="批量删除"
        :tableData="batchHandleDatas"
        :handle-fn="doBatchDelete"
        @confirm="search"
      />
    </div>
  </div>
</template>

<script>
  import { h, defineComponent } from 'vue';
  import Detail from './detail.vue';
  import ApproveRefuse from '@/components/ApproveRefuse';
  import MaterialOrderDetail from '@/views/material/order/detail.vue';
  import moment from 'moment';
  import {
    SearchOutlined,
    PlusOutlined,
    CheckOutlined,
    CloseOutlined,
    DeleteOutlined,
    DownloadOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/material/out';

  export default defineComponent({
    name: 'MaterialOutSheet',
    components: {
      Detail,
      ApproveRefuse,
      MaterialOrderDetail,
    },
    setup() {
      return {
        h,
        SearchOutlined,
        PlusOutlined,
        CheckOutlined,
        CloseOutlined,
        DeleteOutlined,
        DownloadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        materialOrderId: '',
        // 查询列表的查询条件
        searchFormData: {
          code: '',
          scId: '',
          createBy: '',
          // 默认不按操作时间过滤，留空即可
          createTimeStart: '',
          createTimeEnd: '',
          approveBy: '',
          approveTimeStart: '',
          approveTimeEnd: '',
          status: undefined,
          materialOrderCode: '',
          contractCode: '',
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
          { field: 'code', title: '出库单号', width: 180, sortable: true },
          {
            field: 'materialOrderCode',
            title: '发料单号',
            width: 180,
            slots: { default: 'materialOrderCode_default' },
          },
          { field: 'contractCode', title: '合同编号', width: 180 },
          { field: 'scCode', title: '仓库编号', width: 100, visible: false },
          { field: 'scName', title: '仓库名称', width: 120 },
          { field: 'totalNum', title: '航材数量', align: 'right', width: 100 },
          { field: 'createTime', title: '创建时间', width: 170, sortable: true },
          { field: 'createBy', title: '操作人', width: 100, visible: false },
          {
            field: 'status',
            title: '状态',
            width: 100,
            slots: { default: 'status_default' },
          },
          { field: 'approveTime', title: '发料时间', width: 170, sortable: true },
          { field: 'approveBy', title: '发料人', width: 100 },
          { field: 'description', title: '备注', width: 200 },
          { title: '操作', width: 200, fixed: 'right', slots: { default: 'action_default' } },
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
        batchRefuseReason: '',
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
        const params = Object.assign({}, this.searchFormData, {
          scId: this.searchFormData.scId,
          createBy: this.searchFormData.createBy,
          approveBy: this.searchFormData.approveBy,
        });

        return params;
      },
      openAddDialog() {
        this.$router.push('/material/out/add');
      },
      openModifyDialog(row) {
        this.$router.push('/material/out/modify/' + row.id);
      },
      // 删除订单
      deleteOrder(row) {
        this.$msg.createConfirm('对选中的发料出库单执行删除操作？').then(() => {
          this.loading = true;
          api
            .deleteById(row.id)
            .then(() => {
              this.$msg.createSuccess('删除成功！');
              this.search();
            })
            .finally(() => {
              this.loading = false;
            });
        });
      },
      doBatchDelete(row) {
        return api.batchDelete(row.id);
      },
      // 批量删除
      batchDelete() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择要执行操作的发料出库单！');
          return;
        }

        for (let i = 0; i < records.length; i++) {
          if (this.$enums.MATERIAL_OUT_SHEET_STATUS.ISSUED.equalsCode(records[i].status)) {
            this.$msg.createError('第' + (i + 1) + '个发料出库单已发料，不允许执行删除操作！');
            return;
          }
        }

        this.batchHandleDatas = records;

        this.$refs.batchDeleteHandlerDialog.openDialog();
      },
      doBatchApprovePass(row) {
        // 逐条执行单据的审核通过
        return api.approvePass({ id: row.id });
      },
      // 批量审核通过
      batchApprovePass() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择要执行操作的发料出库单！');
          return;
        }

        for (let i = 0; i < records.length; i++) {
          if (this.$enums.MATERIAL_OUT_SHEET_STATUS.ISSUED.equalsCode(records[i].status)) {
            this.$msg.createError('第' + (i + 1) + '个发料出库单已发料，不允许继续执行发料！');
            return;
          }
        }
        // 打开批量处理对话框，展示待执行清单并逐条执行
        this.batchHandleDatas = records;
        this.$refs.batchApprovePassHandlerDialog.openDialog();
      },
      // 批量审核拒绝
      batchApproveRefuse() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择要执行操作的发料出库单！');
          return;
        }

        for (let i = 0; i < records.length; i++) {
          if (this.$enums.MATERIAL_OUT_SHEET_STATUS.ISSUED.equalsCode(records[i].status)) {
            this.$msg.createError('第' + (i + 1) + '个发料出库单已发料，不允许继续标记为可领料！');
            return;
          }

          if (this.$enums.MATERIAL_OUT_SHEET_STATUS.PICKABLE.equalsCode(records[i].status)) {
            this.$msg.createError('第' + (i + 1) + '个发料出库单已标记为可领料，不允许继续标记为可领料！');
            return;
          }
        }

        this.$refs.approveRefuseDialog.openDialog();
      },
      doBatchApproveRefuse(row) {
        return api.batchApproveRefuse({
          id: row.id,
          refuseReason: this.batchRefuseReason,
        });
      },
      doApproveRefuse(reason) {
        this.batchHandleDatas = this.$refs.grid.getCheckboxRecords();
        this.batchRefuseReason = reason;

        this.$refs.batchApproveRefuseHandlerDialog.openDialog();
      },
      exportList() {
        this.loading = true;
        api
          .exportList(this.buildQueryParams({}))
          .then(() => {
            this.$msg.createSuccessTip('导出成功！');
          })
          .finally(() => {
            this.loading = false;
          });
      },
      viewMaterialOrderDetail(id) {
        this.materialOrderId = id;
        this.$nextTick(() => this.$refs.viewMaterialOrderDetailDialog.openDialog());
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
            permission: ['material:out'],
            label: '发料',
            ifShow: () => {
              // 备料中或可领料状态均可执行发料
              return (
                this.$enums.MATERIAL_OUT_SHEET_STATUS.PREPARING.equalsCode(row.status) ||
                this.$enums.MATERIAL_OUT_SHEET_STATUS.PICKABLE.equalsCode(row.status)
              );
            },
            onClick: () => {
              this.$router.push('/material/out/approve/' + row.id);
            },
          },
          {
            permission: ['material:out'],
            label: '修改',
            ifShow: () => {
              // 备料中或可领料状态允许修改
              return (
                this.$enums.MATERIAL_OUT_SHEET_STATUS.PREPARING.equalsCode(row.status) ||
                this.$enums.MATERIAL_OUT_SHEET_STATUS.PICKABLE.equalsCode(row.status)
              );
            },
            onClick: () => {
              this.openModifyDialog(row);
            },
          },
          {
            permission: ['material:out'],
            label: '删除',
            danger: true,
            ifShow: () => {
              // 备料中或可领料状态允许删除
              return (
                this.$enums.MATERIAL_OUT_SHEET_STATUS.PREPARING.equalsCode(row.status) ||
                this.$enums.MATERIAL_OUT_SHEET_STATUS.PICKABLE.equalsCode(row.status)
              );
            },
            onClick: () => {
              this.deleteOrder(row);
            },
          },
        ];
      },
    },
  });
</script>
<style scoped></style>
