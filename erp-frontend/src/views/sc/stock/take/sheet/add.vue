<template>
  <div class="simple-app-container">
    <div v-permission="['stock:take:sheet:add']" v-loading="loading">
      <j-border>
        <j-form label-width="120px">
          <j-form-item label="关联盘点任务" required>
            <take-stock-plan-selector
              v-model:value="formData.takeStockPlanId"
              :request-params="{
                taking: true,
              }"
              :before-open="beforeSelectTakeStockPlan"
              @update:value="afterSelectTakeStockPlan"
            />
          </j-form-item>
          <j-form-item label="预先盘点单">
            <pre-take-stock-sheet-selector
              v-model:value="formData.preTakeStockSheetId"
              :request-params="{
                scId: formData.scId,
              }"
              :before-open="beforeSelectPreTakeStockSheet"
              @update:value="afterSelectPreTakeStockSheet"
            />
          </j-form-item>
          <j-form-item label="仓库">
            {{ formData.scName }}
          </j-form-item>
          <j-form-item label="盘点类别">
            {{ $enums.TAKE_STOCK_PLAN_TYPE.getDesc(formData.takeType) }}
          </j-form-item>
          <j-form-item label="盘点状态">
            {{ $enums.TAKE_STOCK_PLAN_STATUS.getDesc(formData.takeStatus) }}
          </j-form-item>
          <j-form-item label="分类/制造商">
            {{ formData.bizName }}
          </j-form-item>
          <j-form-item label="备注" :span="24">
            <a-textarea v-model:value.trim="formData.description" maxlength="200" />
          </j-form-item>
        </j-form>
      </j-border>

      <!-- 数据列表 -->
      <vxe-grid
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        keep-source
        row-id="id"
        height="500"
        :data="tableData"
        :columns="tableColumn"
        :toolbar-config="toolbarConfig"
      >
        <!-- 工具栏 -->
        <template #toolbar_buttons>
          <a-space>
            <a-button type="primary" :icon="h(PlusOutlined)" @click="addProduct">新增</a-button>
            <a-button danger :icon="h(DeleteOutlined)" @click="delProduct">删除</a-button>
            <a-button :icon="h(PlusOutlined)" @click="openBatchAddProductDialog"
              >批量添加航材</a-button
            >
          </a-space>
        </template>

        <!-- 航材名称 列自定义内容 -->
        <template #productName_default="{ row, rowIndex }">
          <a-auto-complete
            v-if="!row.isFixed"
            v-model:value="row.productName"
            style="width: 100%"
            placeholder=""
            value-key="productName"
            :options="row.productOptions"
            @search="(e) => queryProduct(e, row)"
            @select="(e) => handleSelectProduct(rowIndex, e, row)"
          />
          <span v-else>{{ row.productName }}</span>
        </template>

        <!-- 盘点数量 列自定义内容 -->
        <template #takeNum_default="{ row }">
          <a-input v-model:value="row.takeNum" class="number-input" />
        </template>

        <!-- 备注 列自定义内容 -->
        <template #description_default="{ row }">
          <a-input v-model:value="row.description" />
        </template>

        <!-- 追溯明细 列自定义内容 -->
        <template #trace_default="{ row, rowIndex }">
          <a-button
            v-if="row.isBatch || row.isSerial"
            type="link"
            @click="openTraceDialog(row, rowIndex)"
            >{{ row.isBatch ? '批次明细' : '序列号明细' }}</a-button
          >
        </template>
      </vxe-grid>

      <batch-add-product
        ref="batchAddProductDialog"
        :plan-id="formData.takeStockPlanId || ''"
        @confirm="batchAddProduct"
      />

      <!-- 批次/序列号追溯明细录入 -->
      <a-modal
        v-model:open="traceDialog.visible"
        :title="traceDialog.mode === 'batch' ? '批次盘点明细（逐批次录入）' : '序列号盘点明细（一条序列号一条明细）'"
        width="860px"
        :footer="null"
        :mask-closable="false"
      >
        <div v-if="traceDialog.mode === 'batch'">
          <a-table
            :data-source="traceDialog.batchRows"
            :columns="batchColumns"
            row-key="key"
            size="small"
            :pagination="false"
          >
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.key === 'batchNumber'">
                <a-input v-model:value="record.batchNumber" placeholder="批次号" />
              </template>
              <template v-else-if="column.key === 'takeNum'">
                <a-input v-model:value="record.takeNum" class="number-input" placeholder="实盘数量" />
              </template>
              <template v-else-if="column.key === 'description'">
                <a-input v-model:value="record.description" placeholder="备注" />
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" danger @click="traceDialog.batchRows.splice(index, 1)"
                  >删除</a-button
                >
              </template>
            </template>
          </a-table>
        </div>
        <div v-else>
          <a-table
            :data-source="traceDialog.serialRows"
            :columns="serialColumns"
            row-key="key"
            size="small"
            :pagination="false"
          >
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.key === 'serialNumber'">
                <a-input v-model:value="record.serialNumber" placeholder="序列号" />
              </template>
              <template v-else-if="column.key === 'batchNumber'">
                <a-input v-model:value="record.batchNumber" placeholder="批次号" />
              </template>
              <template v-else-if="column.key === 'takeStatus'">
                <a-select v-model:value="record.takeStatus" style="width: 100%">
                  <a-select-option :value="1">实盘在库</a-select-option>
                  <a-select-option :value="0">实盘缺失</a-select-option>
                </a-select>
              </template>
              <template v-else-if="column.key === 'description'">
                <a-input v-model:value="record.description" placeholder="备注" />
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" danger @click="traceDialog.serialRows.splice(index, 1)"
                  >删除</a-button
                >
              </template>
            </template>
          </a-table>
        </div>
        <div style="text-align: center; margin-top: 12px">
          <a-space>
            <a-button
              type="primary"
              ghost
              :icon="h(PlusOutlined)"
              @click="addTraceRow"
              >新增一行</a-button
            >
            <a-button type="primary" @click="saveTraceDialog">确定</a-button>
          </a-space>
        </div>
      </a-modal>

      <div style="text-align: center; background-color: #ffffff; padding: 8px 0">
        <a-space>
          <a-button
            v-permission="['stock:take:sheet:add']"
            type="primary"
            :loading="loading"
            @click="submit"
            >保存</a-button
          >
          <a-button
            v-permission="['stock:take:sheet:approve']"
            type="primary"
            :loading="loading"
            @click="directApprovePass"
            >审核通过</a-button
          >
          <a-button :loading="loading" @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </div>
</template>
<script>
  import { h, defineComponent } from 'vue';
  import BatchAddProduct from '@/views/sc/stock/take/sheet/batch-add-product.vue';
  import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
  import * as planApi from '@/api/sc/stock/take/plan';
  import * as preApi from '@/api/sc/stock/take/pre';
  import * as api from '@/api/sc/stock/take/sheet';

  export default defineComponent({
    name: 'AddStockTakeSheet',
    components: {
      BatchAddProduct,
    },
    setup() {
      return {
        h,
        PlusOutlined,
        DeleteOutlined,
      };
    },
    data() {
      return {
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        // 设置信息
        config: {},
        // 工具栏配置
        toolbarConfig: {
          // 缩放
          zoom: false,
          // 自定义表头
          custom: false,
          // 右侧是否显示刷新按钮
          refresh: false,
          // 自定义左侧工具栏
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        // 列表数据配置
        tableColumn: [
          { type: 'checkbox', width: 45 },
          { field: 'productCode', title: '航材编号', width: 120 },
          {
            field: 'productName',
            title: '航材名称',
            width: 260,
            slots: { default: 'productName_default' },
          },
          { field: 'skuCode', title: '航材SKU编号', width: 120 },
          { field: 'externalCode', title: '航材简码', width: 120 },
          { field: 'unit', title: '单位', width: 80 },
          { field: 'spec', title: '规格', width: 80 },
          { field: 'categoryName', title: '航材分类', width: 120 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          { field: 'stockNum', title: '系统库存数量', width: 120, align: 'right' },
          {
            field: 'takeNum',
            title: '盘点数量',
            width: 120,
            slots: { default: 'takeNum_default' },
            align: 'right',
          },
          {
            field: 'description',
            title: '备注',
            slots: { default: 'description_default' },
            width: 200,
          },
          {
            title: '追溯明细',
            slots: { default: 'trace_default' },
            width: 110,
            fixed: 'right',
          },
        ],
        tableData: [],
        // 批次明细弹窗
        traceDialog: {
          visible: false,
          mode: 'batch',
          rowIndex: -1,
          batchRows: [],
          serialRows: [],
        },
        batchColumns: [
          { title: '批次号', key: 'batchNumber' },
          { title: '实盘数量', key: 'takeNum', width: 120 },
          { title: '备注', key: 'description', width: 220 },
          { title: '操作', key: 'action', width: 70 },
        ],
        serialColumns: [
          { title: '序列号', key: 'serialNumber' },
          { title: '批次号', key: 'batchNumber', width: 160 },
          { title: '实盘状态', key: 'takeStatus', width: 120 },
          { title: '备注', key: 'description', width: 200 },
          { title: '操作', key: 'action', width: 70 },
        ],
      };
    },
    computed: {},
    created() {
      this.openDialog();
    },
    methods: {
      // 打开对话框 由父页面触发
      openDialog() {
        // 初始化表单数据
        this.initFormData();
      },
      // 关闭对话框
      closeDialog() {
        this.$utils.closeCurrentPage();
      },
      // 初始化表单数据
      initFormData() {
        this.formData = {
          takeStockPlanId: '',
          preTakeStockSheetId: '',
          description: '',
          scId: '',
          scName: '',
          takeType: '',
          takeStatus: '',
          bizName: '',
        };

        this.tableData = [];
      },
      validParams() {
        if (this.$utils.isEmpty(this.formData.takeStockPlanId)) {
          this.$msg.createError('请选择关联盘点任务！');
          return false;
        }
        if (this.$utils.isEmpty(this.tableData)) {
          this.$msg.createError('请录入航材！');
          return false;
        }

        for (let i = 0; i < this.tableData.length; i++) {
          const column = this.tableData[i];
          if (this.$utils.isEmpty(column.productId)) {
            this.$msg.createError('第' + (i + 1) + '行航材不允许为空！');
            return false;
          }
          if (this.$utils.isEmpty(column.takeNum)) {
            this.$msg.createError('第' + (i + 1) + '行航材的盘点数量不允许为空！');
            return false;
          }

          if (!this.$utils.isIntegerGeZero(column.takeNum)) {
            this.$msg.createError('第' + (i + 1) + '行航材的盘点数量不允许小于0！');
            return false;
          }
        }

        return true;
      },
      // 提交表单事件
      submit() {
        if (!this.validParams()) {
          return;
        }
        const params = {
          planId: this.formData.takeStockPlanId,
          preSheetId: this.formData.preTakeStockSheetId || '',
          description: this.formData.description,
          products: this.tableData.map((item) => {
            return {
              productId: item.productId,
              takeNum: item.takeNum,
              description: item.description,
              batchDetails: item.batchDetails || [],
              serialDetails: item.serialDetails || [],
            };
          }),
        };

        this.loading = true;
        api
          .create(params)
          .then(() => {
            this.$msg.createSuccess('保存成功！');
            this.$emit('confirm');

            this.closeDialog();
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 直接审核通过
      directApprovePass() {
        if (!this.validParams()) {
          return;
        }
        const params = {
          planId: this.formData.takeStockPlanId,
          preSheetId: this.formData.preTakeStockSheetId || '',
          description: this.formData.description,
          products: this.tableData.map((item) => {
            return {
              productId: item.productId,
              takeNum: item.takeNum,
              description: item.description,
              batchDetails: item.batchDetails || [],
              serialDetails: item.serialDetails || [],
            };
          }),
        };

        this.loading = true;
        api
          .directApprovePass(params)
          .then(() => {
            this.$msg.createSuccess('审核通过！');
            this.$emit('confirm');

            this.closeDialog();
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 页面显示时触发
      open() {
        // 初始化表单数据
        this.initFormData();
      },
      emptyProduct() {
        return {
          id: this.$utils.uuid(),
          productId: '',
          productCode: '',
          productName: '',
          skuCode: '',
          externalCode: '',
          unit: '',
          spec: '',
          categoryName: '',
          brandName: '',
          stockNum: '',
          takeNum: '',
          description: '',
          products: [],
          isBatch: false,
          isSerial: false,
          batchDetails: [],
          serialDetails: [],
        };
      },
      openTraceDialog(row, rowIndex) {
        this.traceDialog.visible = true;
        this.traceDialog.rowIndex = rowIndex;
        this.traceDialog.mode = row.isBatch ? 'batch' : 'serial';
        this.traceDialog.batchRows = (row.batchDetails || []).map((item) => {
          return Object.assign({ key: this.$utils.uuid() }, item);
        });
        this.traceDialog.serialRows = (row.serialDetails || []).map((item) => {
          return Object.assign({ key: this.$utils.uuid() }, item);
        });
      },
      addTraceRow() {
        if (this.traceDialog.mode === 'batch') {
          this.traceDialog.batchRows.push({
            key: this.$utils.uuid(),
            batchNumber: '',
            takeNum: '',
            description: '',
          });
        } else {
          this.traceDialog.serialRows.push({
            key: this.$utils.uuid(),
            serialNumber: '',
            batchNumber: '',
            takeStatus: 1,
            description: '',
          });
        }
      },
      saveTraceDialog() {
        const row = this.tableData[this.traceDialog.rowIndex];
        if (!row) {
          return;
        }
        if (this.traceDialog.mode === 'batch') {
          let sum = 0;
          for (const item of this.traceDialog.batchRows) {
            if (this.$utils.isEmpty(item.batchNumber)) {
              this.$msg.createError('批次号不允许为空！');
              return;
            }
            if (!this.$utils.isIntegerGeZero(item.takeNum)) {
              this.$msg.createError('批次[' + item.batchNumber + ']实盘数量不允许小于0！');
              return;
            }
            sum += Number(item.takeNum);
          }
          if (sum !== Number(row.takeNum)) {
            this.$msg.createError('批次实盘数量合计必须等于盘点数量！');
            return;
          }
          row.batchDetails = this.traceDialog.batchRows.map((item) => {
            return {
              batchNumber: item.batchNumber,
              takeNum: Number(item.takeNum),
              description: item.description || '',
            };
          });
        } else {
          let present = 0;
          const serials = [];
          for (const item of this.traceDialog.serialRows) {
            if (this.$utils.isEmpty(item.serialNumber)) {
              this.$msg.createError('序列号不允许为空！');
              return;
            }
            if (serials.indexOf(item.serialNumber) >= 0) {
              this.$msg.createError('序列号[' + item.serialNumber + ']重复提交！');
              return;
            }
            serials.push(item.serialNumber);
            if (Number(item.takeStatus) === 1) {
              present++;
            }
          }
          if (present !== Number(row.takeNum)) {
            this.$msg.createError('实盘在库序列号数量必须等于盘点数量！');
            return;
          }
          row.serialDetails = this.traceDialog.serialRows.map((item) => {
            return {
              serialNumber: item.serialNumber,
              batchNumber: item.batchNumber || '',
              takeStatus: Number(item.takeStatus),
              description: item.description || '',
            };
          });
        }
        this.traceDialog.visible = false;
      },
      // 新增航材
      addProduct() {
        if (this.$utils.isEmpty(this.formData.takeStockPlanId)) {
          this.$msg.createError('请先选择关联盘点任务！');
          return;
        }

        this.tableData.push(this.emptyProduct());
      },
      // 搜索航材
      queryProduct(queryString, row) {
        if (this.$utils.isEmpty(queryString)) {
          row.products = [];
          row.productOptions = [];
          return;
        }

        api.searchProducts(this.formData.takeStockPlanId, queryString).then((res) => {
          row.products = res;
          row.productOptions = res.map((item) => {
            return {
              value: item.productId,
              label: item.productCode + ' ' + item.productName,
            };
          });
        });
      },
      // 选择航材
      handleSelectProduct(index, value, row) {
        value = row ? row.products.filter((item) => item.productId === value)[0] : value;
        for (let i = 0; i < this.tableData.length; i++) {
          const data = this.tableData[i];
          if (data.productId === value.productId) {
            if (i === index) {
              this.tableData[index] = Object.assign(this.tableData[index], value);
              return;
            }
            this.$msg.createError('新增航材与第' + (i + 1) + '行航材相同，请勿重复添加');
            this.tableData = this.tableData.filter((t) => {
              return t.id !== row.id;
            });
            return;
          }
        }
        this.tableData[index] = Object.assign(this.tableData[index], value);
      },
      // 删除航材
      delProduct() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择要删除的航材数据！');
          return;
        }

        this.$msg.createConfirm('是否确定删除选中的航材？').then(() => {
          const tableData = this.tableData.filter((t) => {
            const tmp = records.filter((item) => item.id === t.id);
            return this.$utils.isEmpty(tmp);
          });

          this.tableData = tableData;
        });
      },
      openBatchAddProductDialog() {
        if (this.$utils.isEmpty(this.formData.takeStockPlanId)) {
          this.$msg.createError('请先选择关联盘点任务！');
          return;
        }
        this.$refs.batchAddProductDialog.openDialog();
      },
      // 批量新增航材
      batchAddProduct(productList) {
        const filterProductList = [];
        productList.forEach((item) => {
          if (
            this.$utils.isEmpty(this.tableData.filter((data) => item.productId === data.productId))
          ) {
            filterProductList.push(item);
          }
        });

        filterProductList.forEach((item) => {
          this.tableData.push(this.emptyProduct());
          this.handleSelectProduct(this.tableData.length - 1, item);
        });
      },
      beforeSelectPreTakeStockSheet() {
        if (this.$utils.isEmpty(this.formData.takeStockPlanId)) {
          this.$msg.createError('请先选择关联盘点任务');
          return false;
        }

        if (!this.$utils.isEmpty(this.formData.preTakeStockSheetId)) {
          return this.$msg.createConfirm(
            '更改关联盘点任务，不会清除已加载的预先盘点单的航材数据，是否确认更改？',
          );
        }

        return true;
      },
      beforeSelectTakeStockPlan() {
        if (!this.$utils.isEmpty(this.formData.takeStockPlanId)) {
          return this.$msg.createConfirm('更改关联盘点任务，会清空航材数据，是否确认更改？');
        } else {
          return true;
        }
      },
      afterSelectTakeStockPlan(e) {
        this.formData.preTakeStockSheetId = '';

        this.formData.scId = '';
        this.formData.scName = '';
        this.formData.takeType = '';
        this.formData.takeStatus = '';
        this.formData.bizName = '';

        if (!this.$utils.isEmpty(e)) {
          this.loading = true;
          planApi.get(e).then((res) => {
            this.formData.scId = res.scId;
            this.formData.scName = res.scName;
            this.formData.takeType = res.takeType;
            this.formData.takeStatus = res.takeStatus;
            this.formData.bizName = res.bizName;

            planApi
              .getProducts(e)
              .then((products) => {
                this.tableData = products.map((item) => {
                  return Object.assign(this.emptyProduct(), { isFixed: true }, item);
                });
              })
              .finally(() => {
                this.loading = false;
              });
          });
        } else {
          this.tableData = [];
        }
      },
      afterSelectPreTakeStockSheet(e) {
        this.loading = true;
        preApi
          .getProducts(e, this.formData.takeStockPlanId)
          .then((products) => {
            products.forEach((item) => {
              const tableData = this.tableData.filter((obj) => obj.productId === item.productId);
              if (!this.$utils.isEmpty(tableData)) {
                tableData.forEach((obj) => {
                  obj.takeNum = item.takeNum;
                });
              } else {
                this.tableData.push(Object.assign(this.emptyProduct(), item));
              }
            });
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  });
</script>
