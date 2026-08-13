<template>
  <div class="simple-app-container">
    <div v-permission="['material:out']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="仓库" required>
            <store-center-selector v-model:value="formData.scId" :before-open="beforeSelectSc" />
          </j-form-item>
          <j-form-item label="发料单">
            <material-order-selector-for-out-sheet
              v-model:value="formData.materialOrderId"
              @select="materialOrderChange"
              @change="materialOrderChange"
              :sc-id="formData.scId"
              :disabled="$utils.isEmpty(formData.scId)"
            />
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
            <a-button v-show="false" :icon="h(PlusOutlined)" @click="openBatchAddProductDialog">批量添加航材</a-button>
            <a-button :icon="h(NumberOutlined)" @click="batchInputOutNum">批量录入数量</a-button>
            <a-tooltip title="将出库数量设置为剩余出库数量">
              <a-button :icon="h(EditOutlined)" @click="quickSettingOutNum">快捷设置数量</a-button>
            </a-tooltip>
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

        <!-- 库存数量 列自定义内容 -->
        <template #stockNum_default="{ row }">
          <span v-if="checkStockNum(row)">{{ row.stockNum }}</span>
          <span v-else style="color: #f5222d">{{ row.stockNum }}</span>
        </template>

        <!-- 剩余出库数量 列自定义内容 -->
        <template #remainNum_default="{ row }">
          <span v-if="$utils.isEmpty(row.remainNum)">-</span>
          <span v-else-if="$utils.isIntegerGeZero(row.outNum)">{{
            Math.max(0, $utils.sub(row.remainNum, row.outNum))
          }}</span>
          <span v-else>{{ row.remainNum }}</span>
        </template>

        <!-- 出库数量 列自定义内容 -->
        <template #outNum_default="{ row }">
          <a-input
            v-model:value="row.outNum"
            class="number-input"
            @input="(e) => outNumInput(e.target.value)"
          />
        </template>

        <!-- 批次 列自定义内容（单选） -->
        <template #hasLot_default="{ row }">
          <template v-if="(row.isBatch !== undefined ? row.isBatch : row.hasLot) === true">
            <template v-if="row.selectedBatches && row.selectedBatches.length > 0">
              <a-button type="link" size="small" @click="openBatchSelector(row)">
                {{ row.selectedBatches[0].batchNumber || '重选批次' }}
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
          <a-input v-model:value="row.description" />
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
            <a-textarea v-model:value.trim="formData.description" maxlength="200" />
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
            v-permission="['material:out']"
            type="primary"
            :loading="loading"
            @click="createOrder"
            >保存</a-button
          >
          <a-button
            v-permission="['material:out']"
            type="primary"
            :loading="loading"
            @click="directApprovePassOrder"
            >发料出库</a-button
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

  export default defineComponent({
    name: 'AddMaterialOutSheet',
    components: {
      BatchAddProduct,
      MaterialOrderSelectorForOutSheet,
      BatchDetail,
      SerialDetail,
    },
    setup() {
      return {
        h,
        PlusOutlined,
        DeleteOutlined,
        NumberOutlined,
        EditOutlined,
      };
    },
    data() {
      return {
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        materialOrderId: '',
        // 当前批次选择目标
        currentBatchRowId: '',
        currentBatchProductId: '',
        currentSelectedBatchId: '',
        // 当前序列号选择目标
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
      };
    },
    computed: {},
    created() {
      this.openDialog();
    },
    methods: {
      // 提取 id（选择器可能返回对象或直接返回 id）
      getId(val) {
        if (!val) return '';
        if (typeof val === 'object') return val.id || val.value || val.key || '';
        return String(val);
      },
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
      async initFormData() {
        this.formData = {
          scId: '',
          materialOrderId: '',
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
      outNumInput(value) {
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

              this.outNumInput(value);
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
          }
        }

        this.calcSum();
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
              if (product.outNum > product.remainNum) {
                this.$msg.createError(
                  '第' +
                    (i + 1) +
                    '行航材剩余出库数量为' +
                    (product.remainNum) +
                    '，本次出库数量不允许大于' +
                    product.remainNum +
                    '！',
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
      // 创建订单
      createOrder() {
        if (!this.validData()) {
          return;
        }
        // 归一化 materialOrderId，避免为对象或空
        const materialOrderId = this.materialOrderId;
        const params = {
          scId: this.formData.scId,
          materialOrderId,
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

              product.materialOrderDetailId = t.isFixed ? t.id : '';
              return product;
            }),
        };

        this.loading = true;
        api
          .create(params)
          .then((res) => {
            this.$msg.createSuccess('保存成功！');

            // 延迟关闭页面，确保成功提示有足够展示时间
            setTimeout(() => {
              this.$emit('confirm');
              // 显式跳转到列表页，避免关闭页签后回到错误路由
              this.$router.push('/material/out');
            }, 1500);
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 直接审核通过订单
      directApprovePassOrder() {
        if (!this.validData()) {
          return;
        }

        const checkStockNumArr = [];
        this.tableData
          .filter((item) => this.$utils.isIntegerGtZero(item.outNum))
          .forEach((item) => {
            if (checkStockNumArr.map((v) => item.productId).includes(item.productId)) {
              checkStockNumArr
                .filter((v) => v.productId === item.productId)
                .forEach((v) => {
                  v.outNum = this.$utils.add(v.outNum, item.outNum);
                });
            } else {
              checkStockNumArr.push({
                productId: item.productId,
                productCode: item.productCode,
                productName: item.productName,
                stockNum: item.stockNum,
                outNum: item.outNum,
              });
            }
          });

        const unValidStockNumArr = checkStockNumArr.filter((item) => item.stockNum < item.outNum);
        if (!this.$utils.isEmpty(unValidStockNumArr)) {
          this.$msg.createError(
            '航材（' +
              unValidStockNumArr[0].productCode +
              '）' +
              unValidStockNumArr[0].productName +
              '当前库存为' +
              unValidStockNumArr[0].stockNum +
              '，总出库数量为' +
              unValidStockNumArr[0].outNum +
              '，无法完成发料出库！',
          );
          return false;
        }

        // 归一化 materialOrderId，避免为对象或空
        const materialOrderId = this.materialOrderId
        const params = {
          scId: this.formData.scId,
          materialOrderId,
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

              product.materialOrderDetailId = t.isFixed ? t.id : '';
              return product;
            }),
        };

        this.$msg.createConfirm('对发料出库单执行发料操作？').then(() => {
          this.loading = true;
          api
            .directApprovePass(params)
            .then((res) => {
              this.$msg.createSuccess('发料出库成功！');

              // 延迟关闭页面，确保成功提示有足够展示时间
              setTimeout(() => {
                this.$emit('confirm');
                // 显式跳转到列表页，避免关闭页签后回到错误路由
                this.$router.push('/material/out');
              }, 1500);
            })
            .finally(() => {
              this.loading = false;
            });
        });
      },
      // 选择发料单
      materialOrderChange(e) {
        // 只要选择了发料单，清空已固定的航材，然后将发料单中所有的明细列出来
        if (!this.$utils.isEmpty(e)) {
          this.loading = true;
          
          // 先清除已固定的行
          const tableData = this.tableData.filter((item) => !item.isFixed);
          console.log('materialOrderChange', e)
          // 从选择器获取的数据中提取发料单ID
          const materialOrderId = (e && typeof e === 'object') ? (e.id || e.value || e.key) : e;
          // 显式记录发料单ID，确保请求参数携带正确ID
          this.formData.materialOrderId = materialOrderId;
          this.materialOrderId = materialOrderId
          materialOrderApi
            .get(materialOrderId)
            .then((res) => {
              if (!res || !res.details) {
                this.$message.warning('获取发料单详情失败或发料单无明细数据');
                return;
              }
              
              let orderDetails = res.details;
              // 处理明细数据，添加固定标记和剩余数量
              orderDetails = orderDetails.map((item) => {
                const newItem = Object.assign(this.emptyProduct(), item);
                newItem.isFixed = true;
                newItem.remainNum = newItem.orderNum - (newItem.outNum || 0);
                newItem.orderOutNum = newItem.outNum;
                newItem.outNum = 0;
                // 兼容服务端返回 isBatch / isSerial 与旧字段
                if (newItem.isBatch === undefined && newItem.hasLot !== undefined) {
                  newItem.isBatch = newItem.hasLot;
                }
                if (newItem.isSerial === undefined && newItem.hasSerial !== undefined) {
                  newItem.isSerial = newItem.hasSerial;
                }
                return newItem;
              })
              // 仅保留剩余出库量 > 0 的明细
              .filter((it) => (Number(it.remainNum) || 0) > 0);

              // 更新表格数据，将发料单明细放在前面
              this.tableData = [...orderDetails, ...tableData];
              
              // 更新仓库信息
              if (res.scId) {
                this.formData.scId = res.scId;
              }
              
              if (orderDetails.length > 0) {
                this.$message.success(`成功加载${orderDetails.length}条发料单明细数据`);
              } else {
                this.$message.info('该发料单没有明细数据');
              }
            })
            .catch((error) => {
              console.error('加载发料单详情失败:', error);
              this.$message.error('加载发料单详情失败，请重试');
            })
            .finally(() => {
              this.loading = false;
            });
        } else {
          // 清空选择
          this.formData.materialOrderId = '';
          // 如果清空了发料单选择，也清空固定行
          this.tableData = this.tableData.filter((item) => !item.isFixed);
        }
      },
      beforeSelectSc() {
        if (this.$utils.isEmpty(this.formData.materialOrderId)) {
          return true;
        } else {
          // 如果已选择了发料单，提示先清除发料单
          this.$message.warning('修改仓库会清除已选择的发料单，请确认');
          // 清除已选择的发料单
          this.formData.materialOrderId = '';
          // 清空表格数据
          this.tableData = [];
          return true;
        }
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
        // 回显当前已选批次
        this.currentSelectedBatchId = row.selectedBatches && row.selectedBatches.length > 0 ? row.selectedBatches[0].id : '';
        // 等待父传子 props 完成更新后再打开弹窗，避免首次打开时 props 仍为空导致不请求
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
        this.currentSelectedSerialIds = Array.isArray(row.selectedSerialIds) ? row.selectedSerialIds : [];
        // 加载序列号库存
        this.loading = true;
        api
          .getSerialStock(this.formData.scId, row.productId)
          .then((list) => {
            this.serialDetails = list || [];
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
          // 保存所选序列号，用于显示和提交
          this.$set ? this.$set(this.tableData[idx], 'selectedSerialIds', ids) : (this.tableData[idx].selectedSerialIds = ids);
          this.$set ? this.$set(this.tableData[idx], 'selectedSerials', rows) : (this.tableData[idx].selectedSerials = rows);
        }
      },
    },
    watch: {
      'formData.materialOrderId': {
        handler(val) {
          // 无论是对象还是原始值，统一转为 ID 字符串
          const normalized = this.getId(val);
          if (normalized !== val) this.formData.materialOrderId = normalized;
        },
        deep: false,
      },
    },
  });
</script>
<style></style>
