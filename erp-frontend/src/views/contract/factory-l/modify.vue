<template>
  <contract-modify
    ref="contractModify"
    :id="id"
    contract-type="factory-l"
    @confirm="$emit('confirm')"
    @close="$emit('close')"
  />
</template>

<script>
  import { defineComponent } from 'vue';
  import ContractModify from '@/views/contract/components/ContractModify.vue';

  export default defineComponent({
    components: {
      ContractModify,
    },
    props: {
      id: {
        type: String,
        required: true,
      },
    },
    methods: {
      // 打开对话框 由父页面触发
      openDialog() {
        this.$refs.contractModify.openDialog();
      },
    },
  });
</script>
