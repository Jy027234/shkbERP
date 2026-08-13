<template>
  <div>
    <dialog-table
      ref="selector"
      :request="getList"
      :load="getLoad"
      :dialog-width="'80%'"
      :request-params="_requestParams"
      :table-column="[
        { field: 'code', title: '发料单号', minWidth: 180 },
        { field: 'scCode', title: '仓库编号', minWidth: 100 },
        { field: 'scName', title: '仓库名称', minWidth: 120 },
        { field: 'createTime', title: '操作时间', minWidth: 150 },
        { field: 'createBy', title: '操作人', minWidth: 100 },
        { field: 'status', title: '审核状态', minWidth: 100, 
          formatter: ({ cellValue }) => {
            return cellValue === 3 ? '已审核' : '未审核';
          },
        },
        {
          field: 'isOutFinish',
          title: '出库完毕',
          minWidth: 100,
          formatter: ({ cellValue }) => {
            return cellValue ? '是' : '否';
          },
        },
      ]"
      v-bind="$attrs"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.code)" label="单据号">
              <a-input v-model:value="searchParams.code" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.scId)" label="仓库">
              <store-center-selector v-model:value="searchParams.scId" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.createBy)" label="操作人">
              <user-selector v-model:value="searchParams.createBy" />
            </j-form-item>
            <j-form-item label="操作日期" :content-nest="false">
              <div class="date-range-container">
                <a-date-picker
                  v-model:value="searchParams.createTimeStart"
                  placeholder=""
                  value-format="YYYY-MM-DD 00:00:00"
                />
                <span class="date-split">至</span>
                <a-date-picker
                  v-model:value="searchParams.createTimeEnd"
                  placeholder=""
                  value-format="YYYY-MM-DD 23:59:59"
                />
              </div>
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.isOutFinish)" label="出库状态">
              <a-select v-model:value="searchParams.isOutFinish" placeholder="全部" allow-clear>
                <a-select-option :value="false">未完成</a-select-option>
                <a-select-option :value="true">已完成</a-select-option>
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
  import { defineComponent } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/material/order';
  import moment from 'moment';

  export default defineComponent({
    name: 'MaterialOrderSelector',
    components: { SearchOutlined },
    props: {
      requestParams: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    data() {
      return {
        searchParams: {
          code: '',
          scId: '',
          createBy: '',
          createTimeStart: this.$utils.formatDateTime(
            this.$utils.getDateTimeWithMinTime(moment().subtract(1, 'M')),
          ),
          createTimeEnd: this.$utils.formatDateTime(this.$utils.getDateTimeWithMaxTime(moment())),
          isOutFinish: false, // 默认只查询未完成出库的发料单
          status: 3, // 默认只查询已审核的发料单（3=已审核）
        },
      };
    },
    computed: {
      _requestParams() {
        // 合并默认参数和传入的参数，确保必要的参数不被覆盖
        const params = {};
        
        // 首先添加默认参数
        params.isOutFinish = false; // 默认只查询未完成出库的发料单
        params.status = 3; // 默认只查询已审核的发料单
        
        // 然后添加搜索参数
        Object.assign(params, this.searchParams);
        
        // 最后添加传入的参数，覆盖前面的参数
        Object.assign(params, this.requestParams);
        
        return params;
      },
    },
    methods: {
      /**
       * 获取发料单列表
       * @param {Object} params - 分页参数
       * @returns {Promise} - 返回发料单列表数据
       */
      getList(params) {
        // 合并分页参数和请求参数
        return api.query({
          ...params,
          ...this._requestParams,
        });
      },
      
      /**
       * 根据ID加载发料单详情
       * @param {String|Array} ids - 发料单ID或ID数组
       * @returns {Promise} - 返回发料单详情
       */
      getLoad(ids) {
        // 防止发送空ID请求
        if (!ids || (Array.isArray(ids) && ids.length === 0) || ids === '') {
          return Promise.resolve([]);
        }
        
        // 确保传递单个ID字符串，而不是数组
        const id = Array.isArray(ids) ? ids[0] : ids;
        
        // 验证ID是否有效（不为空且不为空字符串）
        if (!id || id.trim() === '') {
          console.warn('无效的发料单ID，跳过请求');
          return Promise.resolve([]);
        }
        
        // 调用API并将返回的单个对象转换为数组
        return api.get(id)
          .then(data => {
            return data ? [data] : [];
          })
          .catch((error) => {
            console.error('获取发料单详情失败:', error);
            return [];
          });
      },
    },
  });
</script>

<style lang="less"></style>
