<template>
  <div>
    <dialog-table
      ref="selector"
      :value="value"
      :request="getList"
      :load="getLoad"
      :column-option="{ label: 'name', value: 'id' }"
      :request-params="_requestParams"
      @update:value="(val) => $emit('update:value', val)"
      v-bind="$attrs"
    >
      <template #form>
        <!-- 查询条件 -->
        <j-border>
          <j-form>
            <j-form-item v-if="$utils.isEmpty(requestParams.code)" label="编号">
              <a-input v-model:value="searchParams.code" />
            </j-form-item>
            <j-form-item v-if="$utils.isEmpty(requestParams.name)" label="名称">
              <a-input v-model:value="searchParams.name" />
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
  import * as api from '@/api/base-data/store-center';

  export default defineComponent({
    name: 'StoreCenterSelector',
    components: { SearchOutlined },
    props: {
      value: {
        type: [String, Array],
        default: '',
      },
      requestParams: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    emits: ['update:value'],
    watch: {
      value: {
        handler(newVal) {
          if (this.$refs.selector) {
            this.$refs.selector.$props.value = newVal;
          }
        },
        immediate: true,
      },
    },
    data() {
      return {
        searchParams: {
          code: '',
          name: '',
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
        });
      },
      getLoad(ids) {
        return api.loadSc(ids);
      },
    },
  });
</script>

<style lang="less"></style>
