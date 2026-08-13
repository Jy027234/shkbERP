<template>
  <div v-permission="['stock:product-batch']">
    <page-wrapper content-full-height fixed-height>
      <!-- 数据列表 -->
      <vxe-grid
        id="ProductStockBatch"
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
              <j-form-item label="仓库">
                <store-center-selector v-model:value="searchFormData.scId" />
              </j-form-item>
              <j-form-item label="航材件号">
                <a-input v-model:value="searchFormData.productCode" allow-clear />
              </j-form-item>
              <j-form-item label="航材名称">
                <a-input v-model:value="searchFormData.productName" allow-clear />
              </j-form-item>
              <j-form-item label="航材分类">
                <product-category-selector
                  v-model:value="searchFormData.categoryId"
                  :only-final="false"
                />
              </j-form-item>
              <j-form-item label="航材制造商">
                <product-brand-selector v-model:value="searchFormData.brandId" />
              </j-form-item>
              <j-form-item label="批次号">
                <a-input v-model:value="searchFormData.batchNumber" allow-clear />
              </j-form-item>
            </j-form>
          </j-border>
        </template>
        <!-- 工具栏 -->
        <template #toolbar_buttons>
          <a-space>
            <a-button type="primary" :icon="h(SearchOutlined)" @click="search">查询</a-button>
            <a-button
              v-permission="['stock:product-batch']"
              type="primary"
              :icon="h(DownloadOutlined)"
              @click="exportList"
              >导出库存</a-button
            >
            <a-button
              v-permission="['stock:init-import']"
              :icon="h(UploadOutlined)"
              @click="$refs.importer.openDialog()"
            >导入库存</a-button>
            <a-button
              v-show="false"
              v-permission="['stock:init-import']"
              :disabled="!lastImportBatchId"
              @click="retryFailed"
            >仅失败重试</a-button>
          </a-space>
        </template>
        <!-- 操作列 -->
        <template #col_action="{ row }">
          <a-space>
            <a
              v-permission="['stock:product-batch']"
              href="javascript:void(0);"
              @click="modify(row)"
            >
              修改
            </a>
            <a
              v-permission="['stock:product-batch']"
              href="javascript:void(0);"
              @click="manageAttachment(row)"
            >
              附件管理
            </a>
          </a-space>
        </template>
      </vxe-grid>
    </page-wrapper>
    
    <!-- 修改批次库存 -->
    <update-product-stock-batch-dialog
      :id="id"
      ref="updateDialog"
      @success="search"
    />
    
    <!-- 批次库存附件管理 -->
    <product-batch-attachment
      ref="attachmentDialog"
    />

    <!-- 库存初始化导入 -->
    <excel-importer
      ref="importer"
      :tip-msg="'流程：预检 -> 执行（仅处理未成功项）。\n建议先清洗模板，确保件号/仓库启用有效。\n若批次号或序列号架位已存在则不更新。\n勾选了仅库存初始化，仅做库存初始化，若已存在的库存记录则不会增加库存。\n没有勾选仅库存初始化，若已存在非0库存航材，则会导入增加库存。'"
      :download-template-url="downloadTemplate"
      :upload-url="upload"
      :execute-url="executeImport"
      :form-data="{ initOnly }"
      :close-after-finish="false"
      @confirm="onImportConfirm"
      @cleared="onImporterCleared"
    >
      <template #form>
        <a-space direction="vertical" style="width: 100%">
          <a-checkbox v-model:checked="precheckOnly">仅预检（不执行落库）</a-checkbox>
          <a-checkbox v-model:checked="initOnly">仅库存初始化</a-checkbox>
          <!-- 预检结果提示 -->
          <div v-if="!isExecuting && precheck.importBatchId">
            <a-alert
              type="info"
              show-icon
              :message="'预检完成：总计 ' + precheck.total + '，通过 ' + precheck.success + '，失败 ' + precheck.failed + '。批次ID：' + precheck.importBatchId"
            />
          </div>
          <!-- 执行结果提示 -->
          <div v-if="!isExecuting && executeRes.importBatchId">
            <a-alert
              type="success"
              show-icon
              :message="'正式导入完成：总计 ' + executeRes.total + '，成功 ' + executeRes.success + '，失败 ' + executeRes.failed + '。批次ID：' + executeRes.importBatchId"
            />
          </div>
          <a-button
            v-if="precheck.importBatchId && precheck.failed === 0"
            type="primary"
            block
            @click="doExecuteImport"
          >正式导入</a-button>
        </a-space>
      </template>
    </excel-importer>

    <!-- 仅失败重试 进度 -->
    <a-modal
      v-model:open="retryVisible"
      :mask-closable="false"
      width="40%"
      title="仅失败重试"
      :style="{ top: '20px' }"
      :footer="null"
      @cancel="retryClearTimer"
    >
      <div v-loading="retryLoading">
        <a-tooltip title="处理进度" placement="bottom">
          <a-progress
            :percent="retryProcess"
            :success-percent="retrySuccessProcess"
            title="处理进度"
            :status="retryStatus"
            style="margin-bottom: 5px"
          />
        </a-tooltip>
        <a-list v-if="retryTipMsgs && retryTipMsgs.length" size="small" bordered :data-source="retryTipMsgs">
          <template #renderItem="{ item }">
            <a-list-item>
              <span style="color: #ff4d4f">{{ item }}</span>
            </a-list-item>
          </template>
          <template #header>
            <div> 失败原因 </div>
          </template>
        </a-list>
      </div>
    </a-modal>
  </div>
</template>

<script>
  import { h, defineComponent } from 'vue';
  import { SearchOutlined, DownloadOutlined,UploadOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/sc/stock/product-stock-batch';
  import UpdateProductStockBatchDialog from './components/update-product-stock-batch-dialog.vue';
  import ProductBatchAttachment from './components/product-batch-attachment.vue';
  import ExcelImporter from '@/components/ExcelImporter';
  import * as importApi from '@/api/sc/stock/init-import';
  import * as compApi from '@/api/components';

  export default defineComponent({
    name: 'ProductStockBatch',
    components: { UpdateProductStockBatchDialog, ProductBatchAttachment, ExcelImporter },
    setup() {
      return {
        h,
        SearchOutlined,
        DownloadOutlined,
        UploadOutlined,
      };
    },
    data() {
      return {
        loading: false,
        // 当前行数据
        id: '',
        // 导入批次ID（用于仅失败重试）
        lastImportBatchId: '',
        // 仅预检开关
        precheckOnly: true,
        // 仅库存初始化开关
        initOnly: true,
        // 预检结果（用于展示统计和批次号、控制“正式导入”按钮）
        precheck: {},
        // 是否正在执行正式导入阶段
        isExecuting: false,
        // 正式导入结果
        executeRes: {},

        // 仅失败重试弹窗与进度
        retryVisible: false,
        retryLoading: false,
        retryProcess: 0,
        retrySuccessProcess: 0,
        retryStatus: 'active',
        retryTipMsgs: [],
        retryTaskId: '',
        retryTimer: null,
        retryReqId: '',

        // 查询列表的查询条件
        searchFormData: {
          scId: '',
          productCode: '',
          productName: '',
          categoryId: '',
          brandId: '',
          batchNumber: '',
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
          { type: 'seq', width: 50 },
          { field: 'scCode', title: '仓库编号', width: 100 },
          { field: 'scName', title: '仓库名称', minWidth: 120 },
          { field: 'productCode', title: '航材件号', width: 120 },
          { field: 'productName', title: '航材名称', minWidth: 180 },
          { field: 'categoryName', title: '航材分类', width: 120 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          { field: 'supplierName', title: '供应商', width: 150 },
          { field: 'quantity', title: '库存数量', align: 'right', width: 100, sortable: true },
          { field: 'batchNumber', title: '批次号', width: 120 },
          { field: 'shelfLocation', title: '架位', minWidth: 120 },
          { field: 'productionDate', title: '生产日期', width: 130 },
          { field: 'expiryDate', title: '失效日期', width: 130 },
          { title: '操作', width: 160, fixed: 'right', slots: { default: 'col_action' } },
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
      // 清空导入面板状态（不清 lastImportBatchId，便于失败重试）
      onImporterCleared() {
        this.precheck = {};
        this.executeRes = {};
        this.isExecuting = false;
        this.lastImportBatchId = '';
      },
      // 列表发生查询时的事件
      search() {
        this.$refs.grid.commitProxy('reload');
      },
      // 下载导入模板
      downloadTemplate() {
        return importApi.downloadTemplate();
      },
      // 上传并执行：先预检，再执行（均使用同一个 taskId，ExcelImporter 已传入）
      upload(params) {
        // params: { file, id }
        return importApi.precheck({ id: params.id, file: params.file, initOnly: this.initOnly }).then((pre) => {
          const payload = pre && pre.data ? pre.data : pre;
          const batchId = payload && payload.importBatchId ? payload.importBatchId : null;
          if (batchId) {
            this.lastImportBatchId = batchId;
          }
          this.precheck = payload || {};
          // 仅进行预检；执行由“正式导入”按钮触发
          return pre;
        });
      },
      // 显式执行导入（只处理未成功项）
      doExecuteImport() {
        const batchId = this.precheck && this.precheck.importBatchId;
        if (!batchId) {
          this.$message.warning('请先完成预检并获取批次ID');
          return;
        }
        this.isExecuting = true;
        this.executeRes = {};
        this.$refs.importer.execute(batchId);
      },
      // 提供给 ExcelImporter 的执行接口
      executeImport(params) {
        // params: { batchId, id }
        return importApi.execute(params);
      },
      // 导入完成
      onImportConfirm(res) {
        // 记录最近一次的批次ID，供“仅失败重试”使用
        try {
          const level1 = res && res.data ? res.data : res;
          const level2 = level1 && level1.data ? level1.data : level1;
          const payload = level2 || {};
          if (payload.importBatchId) {
            this.lastImportBatchId = payload.importBatchId;
          }
          if (this.isExecuting) {
            this.executeRes = payload;
            this.isExecuting = false;
          } else {
            // 预检完成
            this.precheck = Object.assign({}, this.precheck, payload);
          }
        } catch (e) {}
        this.search();
      },
      // 仅失败重试
      retryFailed() {
        if (!this.lastImportBatchId) {
          this.$message.warning('暂无可重试的批次');
          return;
        }
        this.retryVisible = true;
        this.retryLoading = true;
        this.retryProcess = 0;
        this.retrySuccessProcess = 0;
        this.retryStatus = 'active';
        this.retryTipMsgs = [];
        this.retryTaskId = this.$utils.uuid();
        importApi
          .execute({ batchId: this.lastImportBatchId, id: this.retryTaskId, initOnly: this.initOnly })
          .then(() => {
            this.retryTimer = setInterval(this.retryDoTimer, 500);
          })
          .catch(() => {
            this.retryLoading = false;
          });
      },
      retryDoTimer() {
        if (this.retryReqId) return;
        this.retryReqId = this.$utils.uuid();
        compApi
          .getExcelImportTask(this.retryTaskId)
          .then((res) => {
            this.retryProcess = Math.max(this.retryProcess, res.process || 0);
            this.retrySuccessProcess = Math.max(this.retrySuccessProcess, res.successProcess || 0);
            this.retryTipMsgs = res.tipMsgs || [];
            this.retryStatus = res.hasError ? 'exception' : 'active';
            if (res.finished) {
              this.retryClearTimer();
              this.retryLoading = false;
              if (!res.hasError) {
                this.$message.success('仅失败重试完成');
                this.search();
                this.retryVisible = false;
              }
            }
            this.retryReqId = '';
          })
          .catch(() => {
            this.retryClearTimer();
            this.retryLoading = false;
          });
      },
      retryClearTimer() {
        if (this.retryTimer) {
          clearInterval(this.retryTimer);
          this.retryTimer = null;
        }
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
          categoryId: this.searchFormData.categoryId,
          brandId: this.searchFormData.brandId,
          batchNumber: this.searchFormData.batchNumber,
        });

        return params;
      },
      // 导出数据
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
      // 修改批次库存
      modify(row) {
        this.id = row.id;
        this.$refs.updateDialog.openDialog();
      },
      // 管理批次库存附件
      manageAttachment(row) {
        this.$refs.attachmentDialog.openDialog(row.id);
      },
    },
  });
</script>
<style scoped></style>
