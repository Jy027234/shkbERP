<template>
  <div class="simple-app-container">
    <div v-permission="['stock:adjust:modify']" v-loading="loading">
      <j-border>
        <j-form
          ref="form"
          :model="formData"
          :rules="{
            scId: [{ required: true, message: '请选择仓库' }],
            bizType: [{ required: true, message: '请选择业务类型' }],
            reasonId: [{ required: true, message: '请选择调整原因' }],
          }"
        >
          <j-form-item label="仓库" required>
            <store-center-selector
              v-model:value="formData.scId"
              :before-open="beforeSelectSc"
              @update:value="afterSelectSc"
            />
          </j-form-item>
          <j-form-item label="业务类型" required>
            <a-select v-model:value="formData.bizType">
              <a-select-option
                v-for="item in $enums.STOCK_ADJUST_SHEET_BIZ_TYPE.values()"
                :key="item.code"
                :value="item.code"
                >{{ item.desc }}</a-select-option
              >
            </a-select>
          </j-form-item>
          <j-form-item label="调整原因" required>
            <stock-adjust-reason-selector v-model:value="formData.reasonId" />
          </j-form-item>
          <j-form-item :span="16" />
          <j-form-item label="备注" :span="24">
            <a-textarea v-model:value.trim="formData.description" maxlength="200" />
          </j-form-item>
          <j-form-item label="状态">
            <span
              v-if="$enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_PASS.equalsCode(formData.status)"
              style="color: #52c41a"
              >{{ $enums.STOCK_ADJUST_SHEET_STATUS.getDesc(formData.status) }}</span
            >
            <span
              v-else-if="
                $enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)
              "
              style="color: #f5222d"
              >{{ $enums.STOCK_ADJUST_SHEET_STATUS.getDesc(formData.status) }}</span
            >
            <span v-else style="color: #303133">{{
              $enums.STOCK_ADJUST_SHEET_STATUS.getDesc(formData.status)
            }}</span>
          </j-form-item>
          <j-form-item label="拒绝理由" :span="16" :content-nest="false">
            <a-input
              v-if="$enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)"
              v-model:value="formData.refuseReason"
              readonly
            />
          </j-form-item>
          <j-form-item label="操作人">
            <span>{{ formData.updateBy }}</span>
          </j-form-item>
          <j-form-item label="操作时间" :span="16">
            <span>{{ formData.updateTime }}</span>
          </j-form-item>
          <j-form-item
            v-if="
              $enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_PASS.equalsCode(formData.status) ||
              $enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)
            "
            label="审核人"
          >
            <span>{{ formData.approveBy }}</span>
          </j-form-item>
          <j-form-item
            v-if="
              $enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_PASS.equalsCode(formData.status) ||
              $enums.STOCK_ADJUST_SHEET_STATUS.APPROVE_REFUSE.equalsCode(formData.status)
            "
            label="审核时间"
            :span="16"
          >
            <span>{{ formData.approveTime }}</span>
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

        <!-- 调整库存数量 列自定义内容 -->
        <template #stockNum_default="{ row }">
          <a-input
            v-model:value="row.stockNum"
            class="number-input"
            @input="(e) => stockNumInput(e.target.value)"
          />
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

      <order-time-line :id="id" />

      <j-border title="合计">
        <j-form label-width="140px">
          <j-form-item label="调整品种数" :span="6">
            <a-input v-model:value="formData.productNum" class="number-input" readonly />
          </j-form-item>
          <j-form-item label="库存调整数量" :span="6">
            <a-input v-model:value="formData.diffStockNum" class="number-input" readonly />
          </j-form-item>
        </j-form>
      </j-border>

      <batch-add-product
        ref="batchAddProductDialog"
        :sc-id="formData.scId || ''"
        @confirm="batchAddProduct"
      />

      <!-- 批次/序列号追溯明细录入 -->
      <a-modal
        v-model:open="traceDialog.visible"
        :title="traceDialog.mode === 'batch' ? '批次调整明细（逐批次指定）' : '序列号调整明细（一条序列号一条明细）'"
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
              <template v-else-if="column.key === 'stockNum'">
                <a-input v-model:value="record.stockNum" class="number-input" placeholder="调整数量" />
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
            v-permission="['stock:adjust:modify']"
            type="primary"
            :loading="loading"
            @click="submit"
            >保存</a-button
          >
          <a-button :loading="loading" @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </div>
</template>
<script>
  import { h, defineComponent } from 'vue';
  import BatchAddProduct from '@/views/sc/stock/adjust/stock/batch-add-product.vue';
  import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/sc/stock/adjust/stock';

  export default defineComponent({
    name: 'ModifyStockAdjustSheet',
    components: {
      BatchAddProduct,
    },
    setup() {
      return { h, PlusOutlined, DeleteOutlined };
    },
    data() {
      return {
        id: this.$route.params.id,
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
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
          { type: 'checkbox', width: 45, fixed: 'left' },
          { field: 'productCode', title: '航材件号', width: 120, fixed: 'left' },
          {
            field: 'productName',
            title: '航材名称',
            width: 260,
            fixed: 'left',
            slots: { default: 'productName_default' },
          },
          { field: 'skuCode', title: '航材SKU编号', width: 120, visible: false },
          { field: 'externalCode', title: '航材简码', width: 120, visible: false },
          { field: 'unit', title: '单位', width: 80 },
          { field: 'spec', title: '规格', width: 80 },
          { field: 'categoryName', title: '航材分类', width: 120 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          { field: 'curStockNum', title: '库存数量', width: 120, align: 'right' },
          {
            field: 'stockNum',
            title: '调整库存数量',
            width: 120,
            align: 'right',
            slots: { default: 'stockNum_default' },
          },
          {
            field: 'description',
            title: '备注',
            width: 200,
            slots: { default: 'description_default' },
          },
          {
            title: '追溯明细',
            slots: { default: 'trace_default' },
            width: 110,
            fixed: 'right',
          },
        ],
        tableData: [],
        // 追溯明细弹窗
        traceDialog: {
          visible: false,
          mode: 'batch',
          rowIndex: -1,
          batchRows: [],
          serialRows: [],
        },
        batchColumns: [
          { title: '批次号', key: 'batchNumber' },
          { title: '调整数量', key: 'stockNum', width: 120 },
          { title: '备注', key: 'description', width: 220 },
          { title: '操作', key: 'action', width: 70 },
        ],
        serialColumns: [
          { title: '序列号', key: 'serialNumber' },
          { title: '批次号', key: 'batchNumber', width: 160 },
          { title: '备注', key: 'description', width: 220 },
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
        this.loadData();
      },
      // 关闭对话框
      closeDialog() {
        this.$utils.closeCurrentPage();
      },
      // 初始化表单数据
      initFormData() {
        this.formData = {
          scId: '',
          bizType: '',
          reasonId: '',
          description: '',
          updateBy: '',
          updateTime: '',
          approveBy: '',
          approveTime: '',
          status: '',
          refuseReason: '',
          productNum: 0,
          diffStockNum: 0,
        };

        this.tableData = [];
      },
      // 提交表单事件
      submit() {
        this.$refs.form
          .validate()
          .then()
          .then((valid) => {
            if (valid) {
              if (this.$utils.isEmpty(this.tableData)) {
                this.$msg.createError('请录入航材！');
                return;
              }

              for (let i = 0; i < this.tableData.length; i++) {
                const data = this.tableData[i];
                if (this.$utils.isEmpty(data.productId)) {
                  this.$msg.createError('第' + (i + 1) + '行航材不允许为空！');
                  return;
                }
                if (this.$utils.isEmpty(data.stockNum)) {
                  this.$msg.createError('第' + (i + 1) + '行调整库存数量不允许为空！');
                  return;
                }
                if (!this.$utils.isInteger(data.stockNum)) {
                  this.$msg.createError('第' + (i + 1) + '行调整库存数量必须是整数！');
                  return;
                }
                if (!this.$utils.isIntegerGtZero(data.stockNum)) {
                  this.$msg.createError('第' + (i + 1) + '行调整库存数量必须大于0！');
                  return;
                }
              }

              const params = {
                id: this.id,
                scId: this.formData.scId,
                bizType: this.formData.bizType,
                reasonId: this.formData.reasonId,
                description: this.formData.description,
                products: this.tableData.map((item) => {
                  return {
                    productId: item.productId,
                    stockNum: item.stockNum,
                    description: item.description,
                    batchDetails: item.batchDetails || [],
                    serialDetails: item.serialDetails || [],
                  };
                }),
              };
              this.loading = true;
              api
                .update(params)
                .then(() => {
                  this.$msg.createSuccess('修改成功！');
                  this.$emit('confirm');

                  this.closeDialog();
                })
                .finally(() => {
                  this.loading = false;
                });
            }
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
          curStockNum: '',
          description: '',
          isBatch: false,
          isSerial: false,
          batchNumber: '',
          serialNumberList: '',
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
            stockNum: '',
            description: '',
          });
        } else {
          this.traceDialog.serialRows.push({
            key: this.$utils.uuid(),
            serialNumber: '',
            batchNumber: '',
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
          const batches = [];
          for (const item of this.traceDialog.batchRows) {
            if (this.$utils.isEmpty(item.batchNumber)) {
              this.$msg.createError('批次号不允许为空！');
              return;
            }
            if (batches.indexOf(item.batchNumber) >= 0) {
              this.$msg.createError('批次[' + item.batchNumber + ']重复提交！');
              return;
            }
            batches.push(item.batchNumber);
            if (!this.$utils.isIntegerGtZero(item.stockNum)) {
              this.$msg.createError('批次[' + item.batchNumber + ']调整数量必须大于0！');
              return;
            }
            sum += Number(item.stockNum);
          }
          if (sum !== Number(row.stockNum)) {
            this.$msg.createError('批次调整数量合计必须等于调整库存数量！');
            return;
          }
          row.batchDetails = this.traceDialog.batchRows.map((item) => {
            return {
              batchNumber: item.batchNumber,
              stockNum: Number(item.stockNum),
              description: item.description || '',
            };
          });
        } else {
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
          }
          if (serials.length !== Number(row.stockNum)) {
            this.$msg.createError('序列号明细数量必须等于调整库存数量！');
            return;
          }
          row.serialDetails = this.traceDialog.serialRows.map((item) => {
            return {
              serialNumber: item.serialNumber,
              batchNumber: item.batchNumber || '',
              description: item.description || '',
            };
          });
        }
        this.traceDialog.visible = false;
      },
      // 新增航材
      addProduct() {
        if (this.$utils.isEmpty(this.formData.scId)) {
          this.$msg.createError('请先选择仓库！');
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

        api.searchProducts(this.formData.scId, queryString).then((res) => {
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
        this.calcSum();
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

          this.calcSum();
        });
      },
      openBatchAddProductDialog() {
        if (this.$utils.isEmpty(this.formData.scId)) {
          this.$msg.createError('请先选择仓库！');
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
      beforeSelectSc() {
        let flag = false;
        if (!this.$utils.isEmpty(this.formData.scId)) {
          return this.$msg.createConfirm('更改仓库，会清空航材数据，是否确认更改？');
        } else {
          flag = true;
        }

        return flag;
      },
      afterSelectSc(e) {
        if (!this.$utils.isEmpty(e)) {
          this.tableData = [];
          this.calcSum();
        }
      },
      priceInput(e) {
        this.calcSum();
      },
      calcSum() {
        let productNum = 0;
        let diffStockNum = 0;
        this.tableData.forEach((item) => {
          if (!this.$utils.isEmpty(item.productId)) {
            productNum += 1;

            if (this.$utils.isIntegerGeZero(item.stockNum)) {
              diffStockNum = this.$utils.add(item.stockNum, diffStockNum);
            }
          }
        });

        this.formData.productNum = productNum;
        this.formData.diffStockNum = diffStockNum;
      },
      stockNumInput(e) {
        this.calcSum();
      },
      async loadData() {
        this.loading = true;
        api
          .getDetail(this.id)
          .then((res) => {
            Object.assign(this.formData, {
              scId: res.scId,
              bizType: res.bizType,
              reasonId: res.reasonId,
              description: res.description,
              updateBy: res.updateBy,
              updateTime: res.updateTime,
              approveBy: res.approveBy,
              approveTime: res.approveTime,
              status: res.status,
              refuseReason: res.refuseReason,
            });

            this.tableData = res.details;
            this.calcSum();
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  });
</script>
