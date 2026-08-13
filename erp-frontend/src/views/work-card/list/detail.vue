<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="40%"
    title="查看"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['work-card']" v-loading="loading">
      <a-descriptions :column="4" bordered>
        <a-descriptions-item label="工卡号" :span="2">
          {{ formData.code }}
        </a-descriptions-item>
        <a-descriptions-item label="工卡名称" :span="2">
          {{ formData.name }}
        </a-descriptions-item>
        <a-descriptions-item label="机型" :span="2">
          {{ formData.machineTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="件号" :span="2">
          {{ formData.partNumber }}
        </a-descriptions-item>
        <a-descriptions-item label="维修类型" :span="2">
          {{ formData.repairTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="客户" :span="2">
          {{ formData.customerName }}
        </a-descriptions-item>
        <a-descriptions-item label="批准日期" :span="2">
          {{ formData.approvalDate }}
        </a-descriptions-item>
        <a-descriptions-item label="状态" :span="2">
          <available-tag :available="formData.available" />
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
          {{ formData.description }}
        </a-descriptions-item>
      </a-descriptions>
      
      <!-- 必换件列表 -->
      <div class="product-list" style="margin-top: 16px;">
        <a-divider>必换件列表</a-divider>
        <a-table
          :columns="columns"
          :data-source="productList"
          :loading="productLoading"
          :pagination="false"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="false && column.dataIndex === 'productCode'">
              {{ record.productCode }}
            </template>
            <template v-else-if="column.dataIndex === 'productName'">
              {{ record.productName }}
            </template>
            <template v-else-if="column.dataIndex === 'productSpec'">
              {{ record.productSpec }}
            </template>
            <template v-else-if="column.dataIndex === 'productUnit'">
              {{ record.productUnit }}
            </template>
            <template v-else-if="column.dataIndex === 'quantity'">
              {{ record.quantity }}
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent } from 'vue';
  import { workCardApi } from '@/api/work-card/index';

  export default defineComponent({
    // 使用组件
    components: {},
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
        // 必换件列表
        productList: [],
        // 必换件加载状态
        productLoading: false,
        // 必换件表格列定义
        columns: [
          {
            title: '件号',
            dataIndex: 'productCode',
            width: 250,
          },
          {
            title: '航材名称',
            dataIndex: 'productName',
            width: 250,
          },
          {
            title: '规格',
            dataIndex: 'productSpec',
            width: 120,
          },
          {
            title: '单位',
            dataIndex: 'productUnit',
            width: 80,
          },
          {
            title: '数量',
            dataIndex: 'quantity',
            width: 100,
          },
        ],
      };
    },
    created() {
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
      // 初始化表单数据
      initFormData() {
        this.formData = {
          id: '',
          code: '',
          name: '',
          machineTypeId: '',
          machineTypeName: '',
          partNumberId: '',
          partNumberName: '',
          partNumber: '',
          repairTypeId: '',
          repairTypeName: '',
          customerId: '',
          customerName: '',
          approvalDate: '',
          available: '',
          description: '',
        };
      },
      // 页面显示时触发
      open() {
        // 初始化数据
        this.initFormData();

        // 查询数据
        this.loadFormData();
      },
      // 查询数据
      loadFormData() {
        this.loading = true;
        workCardApi
          .get(this.id)
          .then((data) => {
            this.formData = data;
            // 格式化日期显示
            if (this.formData.approvalDate) {
              this.formData.approvalDate = this.formData.approvalDate.substring(0, 10);
            }
            // 加载必换件列表
            this.loadProductList();
          })
          .finally(() => {
            this.loading = false;
          });
      },
      
      // 加载必换件列表
      loadProductList() {
        this.productLoading = true;
        workCardApi
          .getProducts(this.id)
          .then((data) => {
            this.productList = data || [];
          })
          .finally(() => {
            this.productLoading = false;
          });
      },
    },
  });
</script>
