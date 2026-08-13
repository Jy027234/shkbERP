<template>
  <div class="simple-app-container">
    <div v-permission="['material:out']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="仓库" required>
            <store-center-selector
              v-model:value="formData.scId"
              :before-open="beforeSelectSc"
              :disabled="isDisabled"
            />
          </j-form-item>
          <j-form-item label="发料单">
            <div style="display: flex; align-items: center; gap: 8px;">
              <material-order-selector-for-out-sheet
                v-model:value="formData.materialOrderId"
                :sc-id="formData.scId"
                :disabled="$utils.isEmpty(formData.scId) || isDisabled"
                @select="materialOrderChange"
                @change="materialOrderChange"
              />
              <a-tag v-if="!$utils.isEmpty(formData.materialOrderId)" color="blue">
                {{ formData.materialOrderCode || '-' }}
              </a-tag>
            </div>
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
            <a-button
              type="primary"
              :icon="h(PlusOutlined)"
              @click="addProduct"
              :disabled="isDisabled"
              >新增</a-button
            >
            <a-button
              danger
              :icon="h(DeleteOutlined)"
              @click="delProduct"
              :disabled="isDisabled"
              >删除</a-button
            >
            <a-button
              v-show="false"
              :icon="h(PlusOutlined)"
              @click="openBatchAddProductDialog"
              :disabled="isDisabled"
              >批量添加航材</a-button
            >
            <a-button
              :icon="h(NumberOutlined)"
              @click="batchInputOutNum"
              :disabled="isDisabled"
              >批量录入数量</a-button
            >
            <a-tooltip title="将出库数量设置为剩余出库数量">
              <a-button
                :icon="h(EditOutlined)"
                @click="quickSettingOutNum"
                :disabled="isDisabled"
                >快捷设置数量</a-button
              >
            </a-tooltip>
          </a-space>
        </template>

        <!-- 航材名称 列自定义内容 -->
        <template #productName_default="{ row, rowIndex }">
          <a-auto-complete
            v-if="!row.isFixed && !isDisabled"
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

        <!-- 库存数量 列自定义内容 -->
        <template #stockNum_default="{ row }">
          <span v-if="checkStockNum(row)">{{ row.stockNum }}</span>
          <span v-else style="color: #f5222d">{{ row.stockNum }}</span>
        </template>

        <!-- 剩余出库数量 列自定义内容（= 发料数量 - 出库数量） -->
        <template #remainNum_default="{ row }">
          <span v-if="$utils.isEmpty(row.remainNum)">-</span>
          <span v-else>{{ row.remainNum }}</span>
        </template>

        <!-- 出库数量 列自定义内容 -->
        <template #outNum_default="{ row }">
          <a-input
            v-model:value="row.outNum"
            class="number-input"
            @input="(e) => outNumInput(row, e.target.value)"
            :disabled="isDisabled"
          />
        </template>

        <!-- 批次 列自定义内容（单选） -->
        <template #hasLot_default="{ row }">
          <template v-if="(row.isBatch !== undefined ? row.isBatch : row.hasLot) === true">
            <template v-if="row.selectedBatches && row.selectedBatches.length > 0">
              <a-button type="link" size="small" @click="openBatchSelector(row)">
                {{ row.selectedBatches[0].batchNumber || '选择批次' }}
              </a-button>
            </template>
            <template v-else>
              <a-button size="small" @click="openBatchSelector(row)">选择批次</a-button>
            </template>
          </template>
          <template v-else>
            <a-tag>默认批次</a-tag>
          </template>
        </template>

        <!-- 序列号 列自定义内容（多选） -->
        <template #hasSerial_default="{ row }">
          <template v-if="(row.isSerial !== undefined ? row.isSerial : row.hasSerial) === true">
            <template v-if="row.selectedSerials && row.selectedSerials.length > 0">
              <a-tooltip :title="(row.selectedSerials || []).map(s => s.serialNumber).join('、')">
                <a-button type="link" size="small" @click="openSerialSelector(row)">
                  已选{{ row.selectedSerials.length }}个序列号
                </a-button>
              </a-tooltip>
            </template>
            <template v-else>
              <a-button size="small" @click="openSerialSelector(row)">选择序列号</a-button>
            </template>
          </template>
          <template v-else>
            <a-tag>无需序列号</a-tag>
          </template>
        </template>

        <!-- 备注 列自定义内容 -->
        <template #description_default="{ row }">
          <a-input v-model:value="row.description" :disabled="isDisabled" />
        </template>
      </vxe-grid>

      <j-border title="合计">
        <j-form label-width="140px">
          <j-form-item label="出库数量" :span="6">
            <a-input v-model:value="formData.totalNum" class="number-input" readonly />
          </j-form-item>
        </j-form>
      </j-border>

      <j-border>
        <j-form label-width="140px">
          <j-form-item label="备注" :span="24" :content-nest="false">
            <a-textarea
              v-model:value.trim="formData.description"
              maxlength="200"
              :disabled="isDisabled"
            />
          </j-form-item>
        </j-form>
      </j-border>

      <batch-add-product
        ref="batchAddProductDialog"
        :sc-id="formData.scId"
        @confirm="batchAddProduct"
      />
      <batch-detail
        ref="batchDetailModal"
        :sc-id="formData.scId"
        :product-id="currentBatchProductId"
        :selected-id="currentSelectedBatchId"
        @confirm="onBatchSelected"
      />
      <serial-detail
        ref="serialDetailModal"
        :details="serialDetails"
        :selected-ids="currentSelectedSerialIds"
        :is-serial="true"
        @confirm="onSerialSelected"
      />
      <div style="text-align: center; background-color: #ffffff; padding: 8px 0">
        <a-space>
          <a-button
            v-if="!isDisabled"
            v-permission="['material:out:modify']"
            type="primary"
            :loading="loading"
            @click="updateOrder"
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
  import BatchDetail from '@/components/BatchDetail.vue';
  import SerialDetail from '@/components/SerialDetail.vue';
  import MaterialOrderSelectorForOutSheet from '@/components/Selector/src/MaterialOrderSelectorForOutSheet.vue';
  import Moment from 'moment';
  import {
    PlusOutlined,
    DeleteOutlined,
    NumberOutlined,
    EditOutlined,
  } from '@ant-design/icons-vue';
  import * as api from '@/api/material/out';
  import * as materialOrderApi from '@/api/material/order';
  import { useRoute } from 'vue-router';

  export default defineComponent({
    name: 'ModifyMaterialOutSheet',
    components: {
      BatchAddProduct,
      BatchDetail,
      SerialDetail,
      MaterialOrderSelectorForOutSheet,
    },
    setup() {
      const route = useRoute();
      const id = route.params.id || route.query.id;

      return {
        h,
        PlusOutlined,
        DeleteOutlined,
        NumberOutlined,
        EditOutlined,
        id,
      };
    },
    data() {
      return {
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        // 发料单ID（用于规整和请求）
        materialOrderId: '',
        // 批次选择相关
        currentBatchRowId: '',
        currentBatchProductId: '',
        currentSelectedBatchId: '',
        // 序列号选择相关
        currentSerialRowId: '',
        currentSerialProductId: '',
        serialDetails: [],
        currentSelectedSerialIds: [],
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
            slots: { default: 'productName_default' },
            fixed: 'left',
          },
          { field: 'skuCode', title: '航材SKU编号', width: 120, visible: false },
          { field: 'externalCode', title: '航材简码', width: 120, visible: false },
          { field: 'unit', title: '单位', width: 80 },
          { field: 'spec', title: '规格', width: 80 },
          { field: 'categoryName', title: '航材分类', width: 120 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          {
            field: 'stockNum',
            title: '库存数量',
            align: 'right',
            width: 100,
            slots: { default: 'stockNum_default' },
          },
          {
            field: 'orderNum',
            title: '需发料数量',
            align: 'right',
            width: 100,
            formatter: ({ cellValue }) => {
              return this.$utils.isEmpty(cellValue) ? '-' : cellValue;
            },
          },
          {
            field: 'orderOutNum',
            title: '已发料数量',
            align: 'right',
            width: 100,
            formatter: ({ cellValue }) => {
              return this.$utils.isEmpty(cellValue) ? '-' : cellValue;
            },
          },
          {
            field: 'remainNum',
            title: '剩余出库数量',
            align: 'right',
            width: 120,
            slots: { default: 'remainNum_default' },
          },
          {
            field: 'outNum',
            title: '出库数量',
            align: 'right',
            width: 100,
            slots: { default: 'outNum_default' },
          },
          {
            field: 'isBatch',
            title: '选择批次号',
            width: 120,
            slots: { default: 'hasLot_default' },
          },
          {
            field: 'isSerial',
            title: '选择序列号',
            width: 120,
            slots: { default: 'hasSerial_default' },
          },
          {
            field: 'description',
            title: '备注',
            width: 200,
            slots: { default: 'description_default' },
          },
        ],
        tableData: [],
        // 是否禁用编辑
        isDisabled: false,
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
      async initFormData() {
        this.formData = {
          id: '',
          scId: '',
          materialOrderId: '',
          materialOrderCode: '',
          totalNum: 0,
          description: '',
        };

        this.tableData = [];
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
          orderNum: '',
          remainNum: '',
          outNum: '',
          hasLot: false,
          hasSerial: false,
          description: '',
          isFixed: false,
          products: [],
        };
      },
      // 加载数据
      loadData() {
        if (this.$utils.isEmpty(this.id)) {
          return;
        }

        this.loading = true;
        api
          .get(this.id)
          .then((res) => {
            // 设置表单数据
            this.formData = {
              id: res.id,
              scId: res.scId,
              materialOrderId: res.materialOrderId || '',
              totalNum: res.totalNum,
              description: res.description,
              createBy: res.createBy,
              createTime: res.createTime,
              updateBy: res.updateBy,
              updateTime: res.updateTime,
              status: res.status,
            };

            // 记录 materialOrderId 归一化
            this.materialOrderId = this.formData.materialOrderId || '';

            // 设置禁用状态：仅“已发料”禁用，其余（备料中、可领料）允许编辑
            this.isDisabled = this.$enums.MATERIAL_OUT_SHEET_STATUS.ISSUED.equalsCode(res.status);

            // 加载表格数据
            if (res.details && res.details.length > 0) {
              res.details.forEach((detail) => {
                // 转换数据格式
                const product = Object.assign(this.emptyProduct(), {
                  id: detail.id,
                  materialOrderDetailId: detail.materialOrderDetailId || '',
                  productId: detail.productId,
                  productCode: detail.productCode,
                  productName: detail.productName,
                  skuCode: detail.skuCode,
                  externalCode: detail.externalCode,
                  unit: detail.unit,
                  spec: detail.spec,
                  isBatch: detail.isBatch || false,
                  isSerial: detail.isSerial || false,
                  categoryName: detail.categoryName,
                  brandName: detail.brandName,
                  stockNum: detail.stockNum || 0,
                  orderNum: detail.orderNum || 0,
                  remainNum: detail.remainNum || 0,
                  outNum: detail.outNum,
                  hasLot: detail.hasLot || false,
                  hasSerial: detail.hasSerial || false,
                  description: detail.description || '',
                });

                if (!this.$utils.isEmpty(detail.materialOrderDetailId)) {
                  product.isFixed = true;
                }

                // 兼容字段命名
                if (product.isBatch === undefined && product.hasLot !== undefined) {
                  product.isBatch = product.hasLot;
                }
                if (product.isSerial === undefined && product.hasSerial !== undefined) {
                  product.isSerial = product.hasSerial;
                }

                // 回显批次选择（取第一批次）
                if (Array.isArray(detail.lots) && detail.lots.length > 0) {
                  const lot = detail.lots[0];
                  product.selectedBatches = [
                    {
                      id: lot.lotId || lot.id || lot.stockBatchId || lot.batchId || '',
                      batchNumber: lot.lotCode || lot.lotNumber || lot.batchNumber || lot.lot || '',
                    },
                  ];
                }
                // 回显序列号选择
                if (Array.isArray(detail.serials) && detail.serials.length > 0) {
                  product.selectedSerials = detail.serials.map((s) => ({
                    id: s.id || s.serialId || '',
                    serialNumber: s.serialNum || s.serialNumber || s.sn || s.code || '',
                  }));
                  // 预存两类键：关系表id 与 序列号，便于首次打开弹窗即可回显
                  const idKeys = product.selectedSerials.map((s) => s.id).filter(Boolean).map((v) => String(v));
                  const snKeys = product.selectedSerials.map((s) => s.serialNumber).filter(Boolean).map((v) => String(v));
                  product.selectedSerialIds = Array.from(new Set([...idKeys, ...snKeys]));
                }

                this.tableData.push(product);
              });

              // 若存在发料单，获取发料单详情并回显固定行的发料数量与剩余出库数量
              if (!this.$utils.isEmpty(this.materialOrderId)) {
                materialOrderApi.get(this.materialOrderId).then((order) => {
                  // 显示发料单号
                  this.formData.materialOrderCode = order.code || order.no || order.sn || '';

                  const orderDetailMap = new Map();
                  (order.details || []).forEach((od) => {
                    // 兼容 id 命名
                    const key = od.id || od.detailId || od.materialOrderDetailId;
                    if (key) orderDetailMap.set(key, od);
                  });

                  this.tableData.forEach((row) => {
                    if (row.isFixed && row.materialOrderDetailId) {
                      const od = orderDetailMap.get(row.materialOrderDetailId);
                      if (od) {
                        const orderNum = od.orderNum || 0;
                        const currentOut = this.$utils.isIntegerGeZero(row.outNum) ? Number(row.outNum) : 0;
                        // 剩余 = 发料数量 - 当前出库数量（不考虑历史累计）
                        row.orderNum = orderNum;
                        row.remainNum = Math.max(0, this.$utils.sub(orderNum, currentOut));
                      }
                    }
                  });

                  this.calcSum();
                });
              } else {
                this.calcSum();
              }
            }
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 新增航材
      addProduct() {
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
        this.tableData[index] = Object.assign(this.tableData[index], value);

        // 获取库存数据
        if (!this.$utils.isEmpty(this.formData.scId) && !this.$utils.isEmpty(value.productId)) {
          this.tableData[index].stockNum = value.stockNum;
          this.tableData[index].hasLot = value.isBatch;
          this.tableData[index].hasSerial = value.isSerial;
        }
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
      outNumInput(row, value) {
        // 固定行：动态计算剩余 = 发料数量 - 当前出库数量
        if (row && row.isFixed) {
          const orderNum = this.$utils.isIntegerGeZero(row.orderNum) ? Number(row.orderNum) : 0;
          const currentOut = this.$utils.isIntegerGeZero(value) ? Number(value) : 0;
          row.remainNum = Math.max(0, this.$utils.sub(orderNum, currentOut));
        }
        this.calcSum();
      },
      // 计算汇总数据
      calcSum() {
        let totalNum = 0;

        this.tableData
          .filter((t) => {
            return this.$utils.isIntegerGeZero(t.outNum);
          })
          .forEach((t) => {
            const num = parseInt(t.outNum);
            totalNum = this.$utils.add(totalNum, num);
          });

        this.formData.totalNum = totalNum;
      },
      // 批量录入数量
      batchInputOutNum() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择航材数据！');
          return;
        }

        this.$msg
          .createPrompt('请输入出库数量', {
            inputPattern: this.$utils.PATTERN_IS_INTEGER_GE_ZERO,
            inputErrorMessage: '出库数量必须为整数并且不小于0',
            title: '批量录入数量',
            required: true,
          })
          .then(({ value }) => {
            records.forEach((t) => {
              t.outNum = value;
              this.outNumInput(t, value);
            });
          });
      },
      // 快捷设置数量
      quickSettingOutNum() {
        const records = this.$refs.grid.getCheckboxRecords();
        if (this.$utils.isEmpty(records)) {
          this.$msg.createError('请选择航材数据！');
          return;
        }

        for (let i = 0; i < records.length; i++) {
          const record = records[i];
          if (record.isFixed) {
            record.outNum = record.remainNum;
            this.outNumInput(record, record.outNum);
          }
        }
      },
      // 批量新增航材
      batchAddProduct(productList) {
        productList.forEach((item) => {
          this.tableData.push(this.emptyProduct());
          this.handleSelectProduct(this.tableData.length - 1, item);
        });
      },
      // 校验数据
      validData() {
        if (this.$utils.isEmpty(this.formData.scId)) {
          this.$msg.createError('仓库不允许为空！');
          return false;
        }

        if (this.$utils.isEmpty(this.tableData)) {
          this.$msg.createError('请录入航材！');
          return false;
        }

        for (let i = 0; i < this.tableData.length; i++) {
          const product = this.tableData[i];

          if (this.$utils.isEmpty(product.productId)) {
            this.$msg.createError('第' + (i + 1) + '行航材不允许为空！');
            return false;
          }

          if (!this.$utils.isEmpty(product.outNum)) {
            if (!this.$utils.isInteger(product.outNum)) {
              this.$msg.createError('第' + (i + 1) + '行航材出库数量必须为整数！');
              return false;
            }

            if (product.isFixed) {
              if (!this.$utils.isIntegerGeZero(product.outNum)) {
                this.$msg.createError('第' + (i + 1) + '行航材出库数量不允许小于0！');
                return false;
              }
            } else {
              if (!this.$utils.isIntegerGtZero(product.outNum)) {
                this.$msg.createError('第' + (i + 1) + '行航材出库数量必须大于0！');
                return false;
              }
            }

            if (product.isFixed) {
              if ((Number(product.outNum) + Number(product.remainNum)) > Number(product.orderNum)) {
                this.$msg.createError(
                  '第' +
                    (i + 1) +
                    '行航材出库数量为' +
                    (product.outNum) +
                    '，本次可出库数量超过发料数量！'
                );
                return false;
              }
            }
          } else {
            if (!product.isFixed) {
              this.$msg.createError('第' + (i + 1) + '行航材出库数量不允许为空！');
              return false;
            }
          }
        }

        if (
          this.tableData.filter((item) => this.$utils.isIntegerGtZero(item.outNum)).length === 0
        ) {
          this.$msg.createError('航材必须全部或部分出库！');
          return false;
        }

        return true;
      },
      // 更新出库单
      updateOrder() {
        if (!this.validData()) {
          return;
        }

        const params = {
          id: this.formData.id,
          scId: this.formData.scId,
          materialOrderId: this.materialOrderId || this.formData.materialOrderId || '',
          description: this.formData.description,
          details: this.tableData
            .filter((t) => this.$utils.isIntegerGtZero(t.outNum))
            .map((t) => {
              const product = {
                productId: t.productId,
                outNum: Number(t.outNum),
                orderNum: t.orderNum,
                description: t.description,
                stockBatchId: t.selectedBatches?.[0]?.id ? t.selectedBatches?.[0]?.id : '',
                serials: t.selectedSerials?.map((s) => s.id) ? t.selectedSerials?.map((s) => s.id) : [],
                serialNumbers: t.selectedSerials?.map((s) => s.serialNumber) ? t.selectedSerials?.map((s) => s.serialNumber).join(',') : '',
              };

              if (!this.$utils.isEmpty(t.id) && t.id.length > 30) {
                product.id = t.id;
              }

              if (t.isFixed) {
                product.materialOrderDetailId = t.materialOrderDetailId;
              }

              return product;
            }),
        };

        this.loading = true;
        api
          .update(params)
          .then((res) => {
            this.$msg.createSuccess('保存成功！');
            // 延时关闭，确保提示可见
            setTimeout(() => {
              this.$emit('confirm');
              this.closeDialog();
            }, 1500);
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 选择发料单
      materialOrderChange(e) {
        // 只要选择了发料单，清空所有航材，然后将发料单中所有的明细列出来
        if (!this.$utils.isEmpty(e)) {
          this.loading = true;
          // 从选择器获取的数据中提取发料单ID
          const materialOrderId = (e && typeof e === 'object') ? (e.id || e.value || e.key) : e;
          this.formData.materialOrderId = materialOrderId;
          this.materialOrderId = materialOrderId;
          materialOrderApi
            .get(materialOrderId)
            .then((res) => {
              const tableData = this.tableData.filter((item) => !item.isFixed);
              let orderDetails = res.details || [];
              orderDetails = orderDetails.map((item) => {
                item.isFixed = true;
                item.remainNum = item.orderNum - (item.outNum || 0);
                const newItem = Object.assign(this.emptyProduct(), item);
                // 兼容服务端返回 isBatch / isSerial 与旧字段
                if (newItem.isBatch === undefined && newItem.hasLot !== undefined) {
                  newItem.isBatch = newItem.hasLot;
                }
                if (newItem.isSerial === undefined && newItem.hasSerial !== undefined) {
                  newItem.isSerial = newItem.hasSerial;
                }
                return newItem;
              });

              this.tableData = [...orderDetails, ...tableData];

              this.formData.scId = res.scId;
            })
            .finally(() => {
              this.loading = false;
            });
        }
      },
      // 打开批次选择弹窗
      openBatchSelector(row) {
        if (this.$utils.isEmpty(this.formData.scId)) {
          this.$msg.createError('请先选择仓库！');
          return;
        }
        if (this.$utils.isEmpty(row.productId)) {
          this.$msg.createError('请先选择航材！');
          return;
        }
        this.currentBatchRowId = row.id;
        this.currentBatchProductId = row.productId;
        this.currentSelectedBatchId = row.selectedBatches && row.selectedBatches.length > 0 ? row.selectedBatches[0].id : '';
        this.$nextTick(() => {
          this.$refs.batchDetailModal.openDialog();
        });
      },
      // 接收批次选择结果
      onBatchSelected(batches) {
        const idx = this.tableData.findIndex((r) => r.id === this.currentBatchRowId);
        if (idx >= 0) {
          this.$set ? this.$set(this.tableData[idx], 'selectedBatches', batches) : (this.tableData[idx].selectedBatches = batches);
        }
      },
      // 打开序列号选择弹窗
      openSerialSelector(row) {
        if (this.$utils.isEmpty(this.formData.scId)) {
          this.$msg.createError('请先选择仓库！');
          return;
        }
        if (this.$utils.isEmpty(row.productId)) {
          this.$msg.createError('请先选择航材！');
          return;
        }
        this.currentSerialRowId = row.id;
        this.currentSerialProductId = row.productId;
        // 统一为字符串标识：同时包含 id 与 serialNumber，避免因关系ID与库存ID不一致导致无法回显
        if (Array.isArray(row.selectedSerials) && row.selectedSerials.length > 0) {
          const keys = [];
          row.selectedSerials.forEach((s) => {
            if (!s) return;
            if (s.id != null && s.id !== '') keys.push(String(s.id));
            if (s.serialNumber != null && s.serialNumber !== '') keys.push(String(s.serialNumber));
          });
          // 若父级还维护了 selectedSerialIds，则一并纳入
          if (Array.isArray(row.selectedSerialIds)) {
            row.selectedSerialIds.forEach((v) => keys.push(String(v)));
          }
          // 去重
          this.currentSelectedSerialIds = Array.from(new Set(keys));
        } else if (Array.isArray(row.selectedSerialIds)) {
          this.currentSelectedSerialIds = row.selectedSerialIds.map((v) => String(v));
        } else {
          this.currentSelectedSerialIds = [];
        }
        // 加载序列号库存
        this.loading = true;
        api
          .getSerialStock(this.formData.scId, row.productId)
          .then((list) => {
            // 规范化字段：确保包含 id、serialNumber、batchNumber，便于选择器识别与回显
            this.serialDetails = (list || []).map((it) => ({
              // id 兼容：id / serialId
              id: it.id || it.serialId || it.stockSerialId || it.snId || '',
              // 序列号 兼容：serialNum / serialNumber / sn / code
              serialNumber: it.serialNum || it.serialNumber || it.sn || it.code || '',
              // 批次号（可选，用于展示）
              batchNumber: it.batchNumber || it.lotCode || it.lotNumber || it.lot || '',
              // 透传其他展示字段
              scName: it.scName,
              productName: it.productName,
              partNumberCode: it.partNumberCode,
              machineType: it.machineType,
              stockStatus: it.stockStatus,
              productionDate: it.productionDate,
              expiryDate: it.expiryDate,
            }));
            this.$nextTick(() => {
              this.$refs.serialDetailModal.openDialog();
            });
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 接收序列号选择结果
      onSerialSelected({ ids, rows }) {
        const idx = this.tableData.findIndex((r) => r.id === this.currentSerialRowId);
        if (idx >= 0) {
          this.$set ? this.$set(this.tableData[idx], 'selectedSerialIds', ids) : (this.tableData[idx].selectedSerialIds = ids);
          this.$set ? this.$set(this.tableData[idx], 'selectedSerials', rows) : (this.tableData[idx].selectedSerials = rows);
        }
      },
      beforeSelectSc() {
        if (this.$utils.isEmpty(this.formData.materialOrderId)) {
          return true;
        }

        this.$msg.createError('由于"发料出库单关联发料单"，不允许修改仓库！');
        return false;
      },
      // 检查库存数量
      checkStockNum(row) {
        const checkArr = this.tableData
          .filter((item) => item.productId === row.productId)
          .map((item) => item.outNum);
        if (this.$utils.isEmpty(checkArr)) {
          checkArr.push(0);
        }
        const totalOutNum = checkArr.reduce((total, item) => {
          const outNum = this.$utils.isIntegerGtZero(item) ? item : 0;
          return this.$utils.add(total, outNum);
        }, 0);

        return totalOutNum <= row.stockNum;
      },
    },
  });
</script>
<style></style>
