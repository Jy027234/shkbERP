<template>
  <contract-list contract-type="factory-l" />
</template>

<script>
  import { defineComponent } from 'vue';
  import ContractList from '../components/ContractList.vue';

  export default defineComponent({
    name: 'FactoryLContractList',
    components: {
      ContractList
    }
  });
</script>
