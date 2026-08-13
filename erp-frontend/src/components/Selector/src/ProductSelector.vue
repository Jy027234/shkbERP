<template>
  <div>
    <dialog-table
      ref="selector"
      :request="getList"
      :load="getLoad"
      :table-column="[
        { field: 'code', title: '件号', width: 120 },
        { field: 'name', title: '航材名称', minWidth: 260 },
        { field: 'skuCode', title: '航材SKU编号', width: 120, visible: false },
        { field: 'externalCode', title: '航材简码', width: 120, visible: false },
        { field: 'unit', title: '单位', width: 80 },
        { field: 'spec', title: '规格', width: 80 },
        { field: 'categoryName', title: '航材分类', width: 120 },
        { field: 'brandName', title: '航材制造商', width: 120 },
        { field: 'machineTypeName', title: '机型', width: 120 },
        { field: 'partNumberName', title: '件号', width: 120, visible: false },
        { field: 'available', title: '状态', width: 80, slots: { default: 'available_default' } },
      ]"
      :request-params="_requestParams"
      v-bind="$attrs"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.code)" label="件号">
              <a-input v-model:value="searchParams.code" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.name)" label="名称">
              <a-input v-model:value="searchParams.name" />
            </j-form-item>
            <j-form-item v-if="false && $utils.isEmpty(requestParams.shortName)" label="简称">
              <a-input v-model:value="searchParams.shortName" />
            </j-form-item>
            <j-form-item v-if="false && $utils.isEmpty(requestParams.skuCode)" label="SKU编号">
              <a-input v-model:value="searchParams.skuCode" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.categoryId)" label="分类">
              <product-category-selector v-model:value="searchParams.categoryId" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.brandId)" label="制造商">
              <product-brand-selector v-model:value="searchParams.brandId" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.machineTypeId)" label="机型">
              <a-select 
                v-model:value="searchParams.machineTypeId" 
                allow-clear
                placeholder="请选择机型"
                show-search
                :filter-option="filterMachineTypeOption"
                @change="handleMachineTypeChange"
                @popupScroll="handleMachineTypeScroll"
                :loading="machineTypeLoading"
              >
                <a-select-option
                  v-for="item in machineTypeList"
                  :key="item.id"
                  :value="item.id"
                  >{{ item.name }}</a-select-option
                >
              </a-select>
            </j-form-item>
            <j-form-item v-if="false && $utils.isEmpty(requestParams.partNumberId)" label="件号">
              <a-select 
                v-model:value="searchParams.partNumberId" 
                allow-clear
                placeholder="请选择件号"
                show-search
                :filter-option="filterPartNumberOption"
                :disabled="!searchParams.machineTypeId"
                @popupScroll="handlePartNumberScroll"
                :loading="partNumberLoading"
              >
                <a-select-option
                  v-for="item in partNumberList"
                  :key="item.id"
                  :value="item.id"
                  >{{ item.code }}</a-select-option
                >
              </a-select>
            </j-form-item>
            <j-form-item label="创建日期" :content-nest="false">
              <div class="date-range-container">
                <a-date-picker
                  v-model:value="searchParams.startTime"
                  placeholder=""
                  value-format="YYYY-MM-DD 00:00:00"
                />
                <span class="date-split">至</span>
                <a-date-picker
                  v-model:value="searchParams.endTime"
                  placeholder=""
                  value-format="YYYY-MM-DD 23:59:59"
                />
              </div>
            </j-form-item>
            <j-form-item v-if="false && $utils.isEmpty(requestParams.productType)" label="航材类型">
              <a-select v-model:value="searchParams.productType" placeholder="全部" allow-clear>
                <a-select-option
                  v-for="item in $enums.PRODUCT_TYPE.values()"
                  :key="item.code"
                  :value="item.code"
                  >{{ item.desc }}</a-select-option
                >
              </a-select>
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.available)" label="状态">
              <a-select v-model:value="searchParams.available" placeholder="全部" allow-clear>
                <a-select-option
                  v-for="item in $enums.AVAILABLE.values()"
                  :key="item.code"
                  :value="item.code"
                  >{{ item.desc }}</a-select-option
                >
              </a-select>
            </j-form-item>
          </j-form>
        </j-border>
      </template>
      <!-- 工具栏 -->
      <template #toolbar_buttons>
        <a-space class="operator">
          <a-button type="primary" @click="$refs.selector.search()">
            <template #icon>
              <SearchOutlined />
            </template>
            查询</a-button
          >
        </a-space>
      </template>
    </dialog-table>
  </div>
</template>

<script>
  import { defineComponent, onMounted } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/base-data/product/info';
  import * as machineTypeApi from '@/api/base-data/machine-type';
  import * as partNumberApi from '@/api/base-data/part-number';

  export default defineComponent({
    name: 'ProductSelector',
    components: { SearchOutlined },
    props: {
      requestParams: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    mounted() {
      // 在组件挂载时加载机型列表
      this.loadMachineTypeList();
    },
    data() {
      return {
        searchParams: { code: '', name: '', available: this.$enums.AVAILABLE.ENABLE.code },
        // 机型相关
        machineTypeList: [],
        machineTypeLoading: false,
        machineTypePagination: {
          pageIndex: 1,
          pageSize: 20,
          totalCount: 0
        },
        // 件号相关
        partNumberList: [],
        partNumberLoading: false,
        partNumberPagination: {
          pageIndex: 1,
          pageSize: 20,
          totalCount: 0
        },
      };
    },
    computed: {
      _requestParams() {
        return { available: true, ...this.searchParams, ...this.requestParams };
      },
    },
    methods: {
      getList(params) {
        return api.selector({
          ...params,
          available: true,
          ...this.searchParams,
          ...this.requestParams,
        });
      },
      getLoad(ids) {
        return api.loadProduct(ids);
      },
      // 加载机型列表
      loadMachineTypeList(isAppend = false) {
        if (!isAppend) {
          // 重置分页信息
          this.machineTypePagination.pageIndex = 1;
          this.machineTypeList = [];
        }
        
        if (this.machineTypeLoading) {
          return;
        }
        
        // 如果已经加载完所有数据，则不再加载
        if (isAppend && this.machineTypeList.length >= this.machineTypePagination.totalCount) {
          return;
        }
        
        this.machineTypeLoading = true;
        machineTypeApi.selector({
          pageIndex: this.machineTypePagination.pageIndex,
          pageSize: this.machineTypePagination.pageSize,
          available: true
        }).then((res) => {
          if (isAppend) {
            // 追加数据
            this.machineTypeList = [...this.machineTypeList, ...(res.datas || [])];
          } else {
            this.machineTypeList = res.datas || [];
          }
          this.machineTypePagination.totalCount = res.totalCount || 0;
        }).finally(() => {
          this.machineTypeLoading = false;
        });
      },
      
      // 加载件号列表
      loadPartNumberList(machineTypeId, isAppend = false) {
        if (!machineTypeId) {
          this.partNumberList = [];
          return;
        }
        
        if (!isAppend) {
          // 重置分页信息
          this.partNumberPagination.pageIndex = 1;
          this.partNumberList = [];
        }
        
        if (this.partNumberLoading) {
          return;
        }
        
        // 如果已经加载完所有数据，则不再加载
        if (isAppend && this.partNumberList.length >= this.partNumberPagination.totalCount) {
          return;
        }
        
        this.partNumberLoading = true;
        partNumberApi.selector({
          pageIndex: this.partNumberPagination.pageIndex,
          pageSize: this.partNumberPagination.pageSize,
          machineTypeId: machineTypeId,
          available: true
        }).then((res) => {
          if (isAppend) {
            // 追加数据
            this.partNumberList = [...this.partNumberList, ...(res.datas || [])];
          } else {
            this.partNumberList = res.datas || [];
          }
          this.partNumberPagination.totalCount = res.totalCount || 0;
        }).finally(() => {
          this.partNumberLoading = false;
        });
      },
      
      // 处理机型变更
      handleMachineTypeChange(value) {
        // 清空件号
        this.searchParams.partNumberId = '';
        // 重新加载件号列表
        this.loadPartNumberList(value);
      },
      
      // 过滤机型选项
      filterMachineTypeOption(input, option) {
        if (!input) return true;
        
        // 获取选项的原始数据
        const item = this.machineTypeList.find(item => item.id === option.value);
        if (item) {
          // 直接使用原始数据进行搜索
          const searchText = `${item.name} ${item.code}`.toLowerCase();
          return searchText.indexOf(input.toLowerCase()) >= 0;
        }
        
        // 如果没有原始数据，则使用选项的显示文本
        const label = option.label || '';
        const value = option.value ? String(option.value) : '';
        const children = option.children ? String(option.children) : '';
        
        return label.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
               value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
               children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
      },
      
      // 过滤件号选项
      filterPartNumberOption(input, option) {
        if (!input) return true;
        
        // 获取选项的原始数据
        const item = this.partNumberList.find(item => item.id === option.value);
        if (item) {
          // 直接使用原始数据进行搜索
          const searchText = `${item.name} ${item.code}`.toLowerCase();
          return searchText.indexOf(input.toLowerCase()) >= 0;
        }
        
        // 如果没有原始数据，则使用选项的显示文本
        const label = option.label || '';
        const value = option.value ? String(option.value) : '';
        const children = option.children ? String(option.children) : '';
        
        return label.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
               value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
               children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
      },
      
      // 处理机型选择器滚动事件
      handleMachineTypeScroll(e) {
        // 判断是否滚动到底部
        const { target } = e;
        if (target.scrollTop + target.offsetHeight >= target.scrollHeight - 20) {
          // 增加页码，加载下一页数据
          this.machineTypePagination.pageIndex++;
          this.loadMachineTypeList(true);
        }
      },
      
      // 处理件号选择器滚动事件
      handlePartNumberScroll(e) {
        // 判断是否滚动到底部
        const { target } = e;
        if (target.scrollTop + target.offsetHeight >= target.scrollHeight - 20) {
          // 增加页码，加载下一页数据
          this.partNumberPagination.pageIndex++;
          this.loadPartNumberList(this.searchParams.machineTypeId, true);
        }
      },
    },
  });
</script>

<style lang="less">
.date-range-container {
  display: flex;
  align-items: center;
}

.date-split {
  margin: 0 8px;
}
</style>
