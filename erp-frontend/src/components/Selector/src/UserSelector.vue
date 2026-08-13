<template>
  <div>
    <dialog-table
      ref="selector"
      :request="getList"
      :load="getLoad"
      :table-column="[
        { field: 'code', title: '编号', width: 120 },
        { field: 'name', title: '姓名', minWidth: 160 },
        { field: 'unitCode', title: '单位编码', width: 120 },
        {
          field: 'available',
          title: '状态',
          width: 80,
          slots: { default: 'available_default' },
        },
      ]"
      :column-option="{
        value: 'id',
        label: 'nameWithUnit'
      }"
      :request-params="_requestParams"
      v-bind="$attrs"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.code)" label="编号">
              <a-input v-model:value="searchParams.code" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.name)" label="姓名">
              <a-input v-model:value="searchParams.name" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.unitCode)" label="单位编码">
              <a-input v-model:value="searchParams.unitCode" />
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
  import { defineComponent } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/system/user';

  export default defineComponent({
    name: 'UserSelector',
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
          name: '',
          unitCode: '',
          available: this.$enums.AVAILABLE.ENABLE.code,
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
        }).then(res => {
          // 为每个用户数据添加nameWithUnit属性
          if (res && res.datas) {
            res.datas.forEach(item => {
              item.nameWithUnit = item.unitCode ? `${item.name} (${item.unitCode})` : item.name;
            });
          }
          return res;
        });
      },
      getLoad(ids) {
        return api.loadUser(ids).then(res => {
          // 为每个用户数据添加nameWithUnit属性
          if (res && res.length) {
            res.forEach(item => {
              item.nameWithUnit = item.unitCode ? `${item.name} (${item.unitCode})` : item.name;
            });
          }
          return res;
        });
      },
    },
  });
</script>

<style lang="less"></style>
