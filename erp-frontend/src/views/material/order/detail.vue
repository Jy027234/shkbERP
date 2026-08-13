<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="80%"
    title="查看发料单"
    :style="{ top: '20px' }"
    :footer="null"
  >
    <div v-if="visible" v-permission="['material:order']" v-loading="loading">
      <j-border>
        <j-form>
          <j-form-item label="发料单号">
            {{ formData.code }}
          </j-form-item>
          <j-form-item label="仓库">
            {{ formData.scName }}
          </j-form-item>
          <j-form-item label="发料申请单号">
            {{ formData.materialApplyCode }}
          </j-form-item>
          <j-form-item label="出库完成">
            {{ formData.isOutFinish ? '是' : '否' }}
          </j-form-item>
          <j-form-item label="操作人">
            <span>{{ formData.createBy }}</span>
          </j-form-item>
          <j-form-item label="操作时间">
            <span>{{ formData.createTime }}</span>
          </j-form-item>
          <j-form-item 
            label="备注"
            :span="24"
          >
            <span>{{ formData.description }}</span>
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
        <template #action_default="{ row }">
          <a-space :size="4">
            <a-button
              type="link"
              size="small"
              :disabled="row.outNum > 0"
              @click="openProductSelector(row)"
            >
              替换
            </a-button>
            <a-button
              type="link"
              size="small"
              :disabled="row.outNum > 0"
              @click="openUpdateNumModal(row)"
            >
              修改数量
            </a-button>
            <a-button
              type="link"
              size="small"
              danger
              :disabled="row.outNum > 0"
              @click="handleDeleteDetail(row)"
            >
              删除
            </a-button>
          </a-space>
        </template>
      </vxe-grid>

      <order-time-line :id="id" />

      <!-- 航材选择器（替换） -->
      <product-selector
        v-show="false"
        ref="productSelectorRef"
        v-model:value="selectedProductIds"
        :request-params="{ available: true }"
        multiple
        @input-row="handleProductSelected"
      />
      <!-- 航材选择器（添加） -->
      <product-selector
        v-show="false"
        ref="addProductSelectorRef"
        v-model:value="selectedAddProductIds"
        :request-params="{ available: true }"
        multiple
        @input-row="handleAddProductSelected"
      />

      <!-- 替换原因确认弹窗 -->
      <a-modal
        v-model:open="replaceModalVisible"
        title="替换原因"
        @ok="confirmReplace"
        ok-text="确认替换"
        cancel-text="取消"
      >
        <a-textarea
          v-model:value="replaceReason"
          :rows="4"
          placeholder="请输入替换原因"
          maxlength="200"
          show-count
        />
      </a-modal>

      <!-- 修改数量确认弹窗 -->
      <a-modal
        v-model:open="updateNumModalVisible"
        title="修改发料数量"
        @ok="confirmUpdateNum"
        ok-text="确认修改"
        cancel-text="取消"
      >
        <a-form-item label="发料数量">
          <a-input-number
            v-model:value="updateNum"
            :min="1"
            :max="99999"
            placeholder="请输入发料数量"
            style="width: 100%"
          />
        </a-form-item>
      </a-modal>

      <!-- 添加航材数量确认弹窗 -->
      <a-modal
        v-model:open="addProductModalVisible"
        title="添加航材"
        @ok="confirmAddProduct"
        ok-text="确认添加"
        cancel-text="取消"
      >
        <a-form-item label="航材件号">
          <span>{{ pendingAddProduct ? pendingAddProduct.code : '' }}</span>
        </a-form-item>
        <a-form-item label="航材名称">
          <span>{{ pendingAddProduct ? pendingAddProduct.name : '' }}</span>
        </a-form-item>
        <a-form-item label="发料数量">
          <a-input-number
            v-model:value="addProductNum"
            :min="1"
            :max="99999"
            placeholder="请输入发料数量"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="addProductDescription"
            :rows="3"
            placeholder="请输入备注"
            maxlength="200"
            show-count
          />
        </a-form-item>
      </a-modal>

      <div class="form-modal-footer">
        <a-space>
          <a-button type="primary" @click="openAddProductSelector">添加航材</a-button>
          <a-button v-show="false" @click="print">打印</a-button>
          <a-button @click="closeDialog">关闭</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>
<script>
  import { defineComponent, nextTick } from 'vue';
  import { Modal } from 'ant-design-vue';
  import * as api from '@/api/material/order';
  import ProductSelector from '@/components/Selector/src/ProductSelector.vue';

  export default defineComponent({
    components: { ProductSelector },
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
        // 当前正在替换的明细行
        currentReplaceRow: null,
        // 选中的航材ID（替换）
        selectedProductIds: [],
        // 选中的航材ID（添加）
        selectedAddProductIds: [],
        // 替换原因弹窗
        replaceModalVisible: false,
        replaceReason: '',
        // 待替换的航材信息
        pendingNewProduct: null,
        // 修改数量弹窗
        updateNumModalVisible: false,
        // 当前正在修改数量的明细行
        currentUpdateNumRow: null,
        // 修改后的数量
        updateNum: 0,
        // 添加航材弹窗
        addProductModalVisible: false,
        // 待添加的航材信息
        pendingAddProduct: null,
        // 添加的数量
        addProductNum: 0,
        // 添加的备注
        addProductDescription: '',
        // 列配置
        tableColumn: [
          { type: 'seq', width: 50, fixed: 'left' },
          { field: 'productCode', title: '件号', width: 220,align:'center', fixed: 'left' },
          { field: 'productName', title: '航材名称', width: 260,align:'center', fixed: 'left' },
          { field: 'machineTypeName', title: '机型', width: 180,align:'center' },
          { field: 'categoryName', title: '航材分类', width: 200,align:'center' },
          { field: 'brandName', title: '航材制造商', width: 120,align:'center' },
          { field: 'spec', title: '规格', width: 120,align:'center' },
          { field: 'unit', title: '单位', width: 80,align:'center' },
          { field: 'orderNum', title: '发料数量', width: 100,align:'center' },
          { field: 'outNum', title: '已出库数量', width: 100,align:'center' },
          { field: 'taxPrice', title: '含税价（元）', width: 120, visible: false,align:'center' },
          { field: 'taxAmount', title: '含税金额（元）', width: 120,align:'center', visible: false },
          { field: 'replaceReason', title: '替换原因', minWidth: 200,align:'center' },
          { field: 'description', title: '备注', minWidth: 200,align:'center' },
          { title: '操作', width: 280, fixed: 'right', slots: { default: 'action_default' } },
        ],
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
          materialApplyCode: '',
          status: '',
          description: '',
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
      // 打印
      print() {
        api.print(this.id).then((res) => {
          this.$utils.excel.print(res);
        });
      },
      // 打开航材选择器
      openProductSelector(row) {
        this.currentReplaceRow = row;
        this.selectedProductIds = [];
        nextTick(() => {
          const selector = this.$refs.productSelectorRef;
          if (selector && selector.$refs && selector.$refs.selector) {
            selector.$refs.selector.dialogVisible = true;
          }
        });
      },
      // 选择航材后回调
      handleProductSelected(selectedRows) {
        if (!selectedRows || selectedRows.length === 0) {
          return;
        }
        const newProduct = selectedRows[0];
        if (!this.currentReplaceRow) {
          return;
        }
        Modal.confirm({
          title: '确认替换',
          content: `确定将当前件号【${this.currentReplaceRow.productCode}】替换为【${newProduct.code}】吗？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            this.pendingNewProduct = newProduct;
            this.replaceReason = '';
            this.replaceModalVisible = true;
          },
        });
      },
      // 确认替换
      confirmReplace() {
        if (!this.currentReplaceRow || !this.pendingNewProduct) {
          return;
        }
        api
          .replaceDetailProduct(
            this.currentReplaceRow.id,
            this.pendingNewProduct.id,
            this.replaceReason,
          )
          .then(() => {
            this.$message.success('替换成功');
            this.replaceModalVisible = false;
            this.loadFormData();
          });
      },
      // 打开修改数量弹窗
      openUpdateNumModal(row) {
        this.currentUpdateNumRow = row;
        this.updateNum = row.orderNum;
        this.updateNumModalVisible = true;
      },
      // 确认修改数量
      confirmUpdateNum() {
        if (!this.currentUpdateNumRow || this.updateNum <= 0) {
          return;
        }
        api
          .updateDetailNum(this.currentUpdateNumRow.id, this.updateNum)
          .then(() => {
            this.$message.success('修改成功');
            this.updateNumModalVisible = false;
            this.loadFormData();
          });
      },
      // 打开添加航材选择器
      openAddProductSelector() {
        this.selectedAddProductIds = [];
        nextTick(() => {
          const selector = this.$refs.addProductSelectorRef;
          if (selector && selector.$refs && selector.$refs.selector) {
            selector.$refs.selector.dialogVisible = true;
          }
        });
      },
      // 选择航材后回调（添加航材）
      handleAddProductSelected(selectedRows) {
        if (!selectedRows || selectedRows.length === 0) {
          return;
        }
        const newProduct = selectedRows[0];
        this.pendingAddProduct = newProduct;
        this.addProductNum = 1;
        this.addProductDescription = '';
        this.addProductModalVisible = true;
      },
      // 确认添加航材
      confirmAddProduct() {
        if (!this.pendingAddProduct || this.addProductNum <= 0) {
          return;
        }
        api
          .addDetail(this.id, this.pendingAddProduct.id, this.addProductNum, this.addProductDescription)
          .then(() => {
            this.$message.success('添加成功');
            this.addProductModalVisible = false;
            this.loadFormData();
          });
      },
      // 删除明细项
      handleDeleteDetail(row) {
        Modal.confirm({
          title: '确认删除',
          content: `确定删除航材【${row.productCode}】的发料明细吗？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            api
              .deleteDetail(row.id)
              .then(() => {
                this.$message.success('删除成功');
                this.loadFormData();
              });
          },
        });
      },
    },
  });
</script>
