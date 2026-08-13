<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="80%"
    title="查看发料出库单"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['material:out']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="发料出库单号">
            {{ formData.code }}
          </j-form-item>
          <j-form-item label="仓库">
            {{ formData.scName }}
          </j-form-item>
          <j-form-item label="发料单号">
            <span v-if="$utils.isEmpty(formData.materialOrderCode)">-</span>
            <span v-else>
              <a
                v-permission="['material:order']"
                @click="viewMaterialOrderDetail(formData.materialOrderId)"
                >{{ formData.materialOrderCode }}</a
              >
              <span v-no-permission="['material:order']">{{ formData.materialOrderCode }}</span>
            </span>
          </j-form-item>
          <j-form-item label="合同编号">
            <span v-if="$utils.isEmpty(formData.contractCode)">-</span>
            <span>{{ formData.contractCode }}</span>
          </j-form-item>
          <j-form-item label="状态">
            {{ $enums.MATERIAL_OUT_SHEET_STATUS.getDesc(formData.status) }}
          </j-form-item>
          <j-form-item v-show="false" label="操作人">
            {{ formData.createBy }}
          </j-form-item>
          <j-form-item v-show="false" label="操作时间">
            {{ formData.createTime }}
          </j-form-item>
          <j-form-item label="发料人">
            {{ formData.approveBy }}
          </j-form-item>
          <j-form-item label="发料时间">
            {{ formData.approveTime }}
          </j-form-item>
          <j-form-item
            v-if="!$utils.isEmpty(formData.description)"
            label="备注"
            :content-nest="false"
            :span="24"
          >
            <a-input v-model:value="formData.description" readonly />
          </j-form-item>
          <j-form-item
            v-if="!$utils.isEmpty(formData.refuseReason)"
            label="可领料备注"
            :content-nest="false"
            :span="24"
          >
            <a-input v-model:value="formData.refuseReason" readonly />
          </j-form-item>
        </j-form>
      </j-border>
      <!-- 数据列表 -->
      <vxe-grid
        id="material-out-detail-grid"
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
        <!-- 批次 列自定义内容 -->
        <template #batchDetail_default="{ row }">
          <a-tooltip v-if="hasBatch(row)" :title="batchTooltip(row)">
            <span>{{ batchDisplay(row) }}</span>
          </a-tooltip>
          <span v-else>-</span>
        </template>

        <!-- 序列号 列自定义内容 -->
        <template #serialDetail_default="{ row }">
          <a-tooltip v-if="hasSerial(row)" :title="serialTooltip(row)">
            <span>{{ serialDisplay(row) }}</span>
          </a-tooltip>
          <span v-else>-</span>
        </template>
      </vxe-grid>

      <!-- 批次号弹窗 -->
      <batch-detail ref="batchDetailDialog" :details="curLotDetails" />

      <!-- 序列号弹窗 -->
      <serial-detail ref="serialDetailDialog" :details="curSerialDetails" />

      <!-- 发料单查看窗口 -->
      <material-order-detail :id="materialOrderId" ref="viewMaterialOrderDetailDialog" />

      <div class="form-modal-footer">
        <a-space>
          <!-- <a-button type="primary" @click="print">打印</a-button> -->
          <a-button type="primary" @click="exportWord">打印</a-button>
          <a-button @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import * as api from '@/api/material/out';
  import MaterialOrderDetail from '@/views/material/order/detail.vue';
  import BatchDetail from '@/components/BatchDetail.vue';
  import SerialDetail from '@/components/SerialDetail.vue';

  export default defineComponent({
    components: {
      MaterialOrderDetail,
      BatchDetail,
      SerialDetail,
    },
    props: {
      id: {
        type: String,
        required: true,
      },
    },
    data() {
      return {
        // 是否可见
        visible: false,
        // 是否显示加载框
        loading: false,
        // 表单数据
        formData: {},
        // 列表数据
        tableData: [],
        // 列配置
        tableColumn: [
          { type: 'seq', width: 50 },
          { field: 'productCode', title: '件号', width: 120 },
          { field: 'productName', title: '航材名称', width: 260 },
          { field: 'categoryName', title: '航材分类', width: 200 },
          { field: 'brandName', title: '航材制造商', width: 120 },
          { field: 'spec', title: '规格', width: 120 },
          { field: 'unit', title: '单位', width: 80 },
          { field: 'orderNum', title: '发料数量', width: 100, align: 'right' },
          { field: 'outNum', title: '出库数量', width: 100, align: 'right' },
          {
            field: 'batchNumber',
            title: '批次号',
            width: 100,
            showOverflow: false,
            slots: { default: 'batchDetail_default' },
          },
          {
            field: 'serialNumbers',
            title: '序列号',
            width: 100,
            showOverflow: false,
            slots: { default: 'serialDetail_default' },
          },
          { field: 'description', title: '备注', minWidth: 200 },
        ],
        // 发料单ID
        materialOrderId: '',
        // 当前批次明细
        curLotDetails: [],
        // 当前序列号明细
        curSerialDetails: [],
      };
    },
    computed: {},
    created() {
      // 初始化表单数据
      this.initFormData();
    },
    methods: {
      // 打开对话框 由父页面触发
      openDialog() {
        this.visible = true;

        this.$nextTick(() => this.open());
      },
      // 关闭对话框
      closeDialog() {
        this.visible = false;
        this.$emit('close');
      },
      // 初始化
      initFormData() {
        this.formData = {
          code: '',
          scName: '',
          materialOrderCode: '',
          materialOrderId: '',
          contractCode: '',
          status: '',
          description: '',
          createBy: '',
          createTime: '',
          approveBy: '',
          approveTime: '',
          refuseReason: '',
        };

        this.tableData = [];
      },
      // 页面显示时触发
      open() {
        // 初始化数据
        this.initFormData();

        // 查询数据
        this.loadFormData();
      },
      // 查询数据
      async loadFormData() {
        this.loading = true;
        await api
          .get(this.id)
          .then((data) => {
            this.formData = data;
            this.tableData = data.details || [];
          })
          .finally(() => {
            this.loading = false;
          });
      },
      // 是否有批次
      hasBatch(row) {
        return (
          (row.lots && row.lots.length > 0) ||
          (!!row.batchNumber && String(row.batchNumber).trim() !== '')
        );
      },
      // 是否有序列号
      hasSerial(row) {
        return (
          (row.serials && row.serials.length > 0) ||
          (!!row.serialNumbers && String(row.serialNumbers).trim() !== '')
        );
      },
      // 批次完整文本
      batchFullText(row) {
        if (row.lots && row.lots.length > 0) {
          return row.lots
            .map((x) => x.lotCode || x.batchNumber || '')
            .filter(Boolean)
            .join('、');
        }
        return row.batchNumber || '';
      },
      // 序列完整文本
      serialFullText(row) {
        if (row.serials && row.serials.length > 0) {
          return row.serials
            .map((x) => x.serialNum || x.serialNumber || '')
            .filter(Boolean)
            .join('、');
        }
        return row.serialNumbers || '';
      },
      // 批次显示（前几个）
      batchDisplay(row) {
        if (row.lots && row.lots.length > 0) {
          const list = row.lots
            .map((x) => x.lotCode || x.batchNumber || '')
            .filter(Boolean);
          if (list.length <= 3) return list.join('、');
          return `${list.slice(0, 3).join('、')} …`;
        }
        return this.truncate(String(row.batchNumber || ''), 30);
      },
      // 序列显示（前几个）
      serialDisplay(row) {
        if (row.serials && row.serials.length > 0) {
          const list = row.serials
            .map((x) => x.serialNum || x.serialNumber || '')
            .filter(Boolean);
          if (list.length <= 3) return list.join('、');
          return `${list.slice(0, 3).join('、')} …`;
        }
        return this.truncate(String(row.serialNumbers || ''), 30);
      },
      // Tooltip 文本
      batchTooltip(row) {
        return this.batchFullText(row);
      },
      serialTooltip(row) {
        return this.serialFullText(row);
      },
      // 通用截断
      truncate(text, maxLen) {
        if (!text) return '';
        return text.length <= maxLen ? text : `${text.slice(0, maxLen)} …`;
      },
      // 打印
      print() {
        api.print(this.id).then((res) => {
          this.$utils.excel.print(res);
        });
      },
      // 打印 Word
      exportWord() {
        api.exportWord(this.id)
          .then(res => {
            if (res) {
              const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
              const url = window.URL.createObjectURL(blob);
              const link = document.createElement('a');
              link.href = url;
              const fileName = `发料出库单_${this.formData.code || ''}_${new Date().toLocaleDateString().replace(/\//g, '-')}.docx`;
              link.setAttribute('download', fileName);
              document.body.appendChild(link);
              link.click();
              document.body.removeChild(link);
              window.URL.revokeObjectURL(url);
              this.$message.success('打印文件下载成功');
            }
          })
          .catch(err => {
            console.error('打印失败:', err);
            this.$message.error(err.message || '打印失败，请重试');
          });
      },
      // 查看发料单详情
      viewMaterialOrderDetail(id) {
        this.materialOrderId = id;
        this.$nextTick(() => this.$refs.viewMaterialOrderDetailDialog.openDialog());
      },
      // 查看批次详情
      viewBatchDetail(row) {
        this.curLotDetails = row.lots || [];
        this.$nextTick(() => this.$refs.batchDetailDialog.openDialog());
      },
      // 查看序列号详情
      viewSerialDetail(row) {
        this.curSerialDetails = row.serials || [];
        this.$nextTick(() => this.$refs.serialDetailDialog.openDialog());
      },
    },
  });
</script>
