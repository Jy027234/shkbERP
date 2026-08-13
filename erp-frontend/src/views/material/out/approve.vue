<template>
  <div class="simple-app-container">
    <div v-permission="['material:out']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="单据号">
            {{ formData.code }}
          </j-form-item>
          <j-form-item label="仓库">
            {{ formData.scName }}
          </j-form-item>
          <j-form-item label="发料单号">
            <span v-if="$utils.isEmpty(formData.materialOrderCode)">-</span>
            <span v-else>{{ formData.materialOrderCode }}</span>
          </j-form-item>
          <j-form-item label="状态">
            {{ $enums.MATERIAL_OUT_SHEET_STATUS.getDesc(formData.status) }}
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
      >
        <!-- 批次号 列自定义内容 -->
        <template #lots_cell="{ row }">
          <template v-if="row.lots && row.lots.length">
            <a-tooltip :title="(row.lots || []).map(l => l.lotCode).filter(Boolean).join('，')">
              <span class="cell-ellipsis">
                {{ (row.lots || []).map(l => l.lotCode).filter(Boolean).join('，') || '-' }}
              </span>
            </a-tooltip>
          </template>
          <span v-else>-</span>
        </template>

        <!-- 序列号 列自定义内容 -->
        <template #serials_cell="{ row }">
          <template v-if="row.serials && row.serials.length">
            <a-tooltip :title="(row.serials || []).map(s => s.serialNumber || s.id).filter(Boolean).join('，')">
              <span class="cell-ellipsis">
                {{ (row.serials || []).map(s => s.serialNumber || s.id).filter(Boolean).join('，') || '-' }}
              </span>
            </a-tooltip>
          </template>
          <span v-else>-</span>
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
            <a-input v-model:value="formData.description" readonly />
          </j-form-item>
        </j-form>
      </j-border>

      <!-- 批次号弹窗 -->
      <batch-detail ref="batchDetailDialog" :details="curLotDetails" />

      <!-- 序列号弹窗 -->
      <serial-detail ref="serialDetailDialog" :details="curSerialDetails" />

      <j-border>
        <j-form label-width="140px">
          <j-form-item label="发料备注" :span="24" :content-nest="false">
            <a-textarea v-model:value.trim="formData.approveRemark" maxlength="200" />
          </j-form-item>
        </j-form>
      </j-border>

      <div style="text-align: center; background-color: #ffffff; padding: 8px 0">
        <a-space>
          <a-button
            v-permission="['material:out']"
            danger
            :loading="loading"
            @click="approveRefuse"
            >可领料</a-button
          >
          <a-button
            v-permission="['material:out']"
            type="primary"
            :loading="loading"
            @click="approvePass"
            >发料出库</a-button
          >
          <a-button :loading="loading" @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </div>
</template>
<script>
  import { defineComponent } from 'vue';
  import BatchDetail from '@/components/BatchDetail.vue';
  import SerialDetail from '@/components/SerialDetail.vue';
  import * as api from '@/api/material/out';
  import { useRoute } from 'vue-router';

  export default defineComponent({
    name: 'ApproveMaterialOutSheet',
    components: {
      BatchDetail,
      SerialDetail,
    },
    setup() {
      const route = useRoute();
      const id = route.params.id;

      return {
        id,
      };
    },
    data() {
      return {
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        // 列表数据配置
        tableColumn: [
          { type: 'seq', width: 50 },
          { field: 'productCode', title: '航材件号', width: 120 },
          { field: 'productName', title: '航材名称', width: 260 },
          { field: 'skuCode', title: '航材SKU编号', width: 120 , visible: false},
          { field: 'externalCode', title: '航材简码', width: 120 , visible: false},
          { field: 'unit', title: '单位', width: 80 },
          { field: 'spec', title: '规格', width: 80 },
          { field: 'categoryName', title: '航材分类', width: 120 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          {
            field: 'stockNum',
            title: '库存数量',
            align: 'right',
            width: 100,
            formatter: ({ cellValue }) => {
              return this.$utils.isEmpty(cellValue) ? '-' : cellValue;
            },
          },
          {
            field: 'orderNum',
            title: '发料数量',
            align: 'right',
            width: 100,
            formatter: ({ cellValue }) => {
              return this.$utils.isEmpty(cellValue) ? '-' : cellValue;
            },
          },
          {
            field: 'orderOutNum',
            title: '已发数量',
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
            formatter: ({ row }) => {
              return this.calcRemainNum(row);
            },
          },
          {
            field: 'outNum',
            title: '出库数量',
            align: 'right',
            width: 100,
          },
          { field: 'lots', title: '批次号', width: 160, slots: { default: 'lots_cell' } },
          { field: 'serials', title: '序列号', width: 160, slots: { default: 'serials_cell' } },
          { field: 'description', title: '备注', width: 200 },
        ],
        tableData: [],
        // 当前批次明细
        curLotDetails: [],
        // 当前序列号明细
        curSerialDetails: [],
        // 发料单ID
        materialOrderId: '',
      };
    },
    computed: {},
    created() {
      this.openDialog();
    },
    methods: {
      // 计算剩余出库数量：发料数量 - 本次出库数量，最小为0
      calcRemainNum(row) {
        const orderNum = Number(row.orderNum || 0);
        const outNum = Number(row.outNum || 0);
        const remain = this.$utils.sub(orderNum, outNum);
        return remain < 0 ? 0 : remain;
      },
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
          code: '',
          scId: '',
          scName: '',
          materialOrderId: '',
          materialOrderCode: '',
          totalNum: 0,
          description: '',
          approveRemark: '',
          createBy: '',
          createTime: '',
          status: '',
        };

        this.tableData = [];
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
            this.formData = Object.assign({}, res, {
              approveRemark: '',
            });

            // 加载表格数据
            if (res.details && res.details.length > 0) {
              this.tableData = res.details;

              // 检查每个航材的库存是否足够
              this.checkProductStock();
            }
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 检查航材库存是否足够
      checkProductStock() {
        // 按商品ID分组汇总出库数量
        const productMap = new Map();
        this.tableData.forEach((item) => {
          if (productMap.has(item.productId)) {
            const existItem = productMap.get(item.productId);
            existItem.outNum = this.$utils.add(existItem.outNum, item.orderNum);
          } else {
            productMap.set(item.productId, {
              productId: item.productId,
              productCode: item.productCode,
              productName: item.productName,
              stockNum: item.stockNum || 0,
              outNum: item.outNum,
            });
          }
        });

        // 检查每个商品的库存是否足够
        let hasInsufficient = false;
        productMap.forEach((item) => {
          if (item.outNum > item.stockNum) {
            hasInsufficient = true;
            this.$msg.createWarning(
              `${item.productName} 的库存数量(${item.stockNum})不足，出库数量为(${item.outNum})`,
            );
          }
        });

        if (hasInsufficient) {
          this.$msg.createWarning('存在库存不足的航材，请谨慎发料！');
        }
      },
      // 查看批次详情
      viewBatchDetail(row) {
        if (this.$utils.isEmpty(row.lotDetails)) {
          // 查询批次详情
          api.getBatchStock(this.formData.scId, row.productId).then((res) => {
            row.lotDetails = res;
            this.curLotDetails = res;
            this.$nextTick(() => this.$refs.batchDetailDialog.openDialog());
          });
        } else {
          this.curLotDetails = row.lotDetails;
          this.$nextTick(() => this.$refs.batchDetailDialog.openDialog());
        }
      },
      // 查看序列号详情
      viewSerialDetail(row) {
        if (this.$utils.isEmpty(row.serialDetails)) {
          // 查询序列号详情
          api.getSerialStock(this.formData.scId, row.productId).then((res) => {
            row.serialDetails = res;
            this.curSerialDetails = res;
            this.$nextTick(() => this.$refs.serialDetailDialog.openDialog());
          });
        } else {
          this.curSerialDetails = row.serialDetails;
          this.$nextTick(() => this.$refs.serialDetailDialog.openDialog());
        }
      },
      // 审核通过
      approvePass() {
        // 验证数据
        const checkStockNumArr = [];
        this.tableData.forEach((item) => {
          if (checkStockNumArr.map((v) => v.productId).includes(item.productId)) {
            checkStockNumArr
              .filter((v) => v.productId === item.productId)
              .forEach((v) => {
                v.outNum = this.$utils.add(v.outNum, item.orderNum);
              });
          } else {
            checkStockNumArr.push({
              productId: item.productId,
              productCode: item.productCode,
              productName: item.productName,
              stockNum: item.stockNum || 0,
              outNum: item.outNum,
            });
          }
        });

        const unValidStockNumArr = checkStockNumArr.filter((item) => item.stockNum < item.outNum);
        if (!this.$utils.isEmpty(unValidStockNumArr)) {
          this.$msg
            .createConfirm(
              '航材（' +
                unValidStockNumArr[0].productName +
                '）' +
                '当前库存为' +
                unValidStockNumArr[0].stockNum +
                '，需出库数量为' +
                unValidStockNumArr[0].outNum +
                '，库存不足，确定要发料吗？',
            )
            .then(() => {
              this.doApprovePass();
            })
            .catch(() => {});
        } else {
          this.$msg.createConfirm('确定要操作发料吗？').then(() => {
            this.doApprovePass();
          });
        }
      },
      // 执行审核通过
      doApprovePass() {
        this.loading = true;
        api
          .approvePass({
            id: this.formData.id,
            description: this.formData.approveRemark,
          })
          .then((res) => {
            this.$msg.createSuccess('发料成功！');
            this.$router.replace('/material/out');
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 审核拒绝
      approveRefuse() {
        if (this.$utils.isEmpty(this.formData.approveRemark)) {
          this.$msg.createError('请填写发料备注！');
          return;
        }

        this.$msg.createConfirm('确定改为可领料？').then(() => {
          this.loading = true;
          api
            .approveRefuse({
              id: this.formData.id,
              refuseReason: this.formData.approveRemark,
            })
            .then((res) => {
              this.$msg.createSuccess('修改成功！');
              this.$router.replace('/material/out');
            })
            .finally(() => {
              this.loading = false;
            });
        });
      },
    },
  });
</script>
<style>
 .cell-ellipsis {
   display: inline-block;
   max-width: 100%;
   overflow: hidden;
   text-overflow: ellipsis;
   white-space: nowrap;
   vertical-align: bottom;
 }
</style>
