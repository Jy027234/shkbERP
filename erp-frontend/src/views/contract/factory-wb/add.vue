<template>
  <contract-add
    ref="contractAdd"
    contract-type="factory-wb"
    @confirm="$emit('confirm')"
    @close="$emit('close')"
  />
</template>

<script>
  import { defineComponent } from 'vue';
  import ContractAdd from '@/views/contract/components/ContractAdd.vue';

  export default defineComponent({
    components: {
      ContractAdd,
    },
    methods: {
      // 打开对话框 由父页面触发
      openDialog() {
        this.$refs.contractAdd.openDialog();
      },
    },
  });
</script>
