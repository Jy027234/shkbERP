<template>
  <contract-detail
    ref="contractDetail"
    :id="id"
    contract-type="aviation"
    @close="$emit('close')"
  />
</template>

<script>
  import { defineComponent } from 'vue';
  import ContractDetail from '@/views/contract/components/ContractDetail.vue';

  export default defineComponent({
    components: {
      ContractDetail,
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
        this.$refs.contractDetail.openDialog();
      },
    },
  });
</script>
