<template>
  <contract-list contract-type="factory-wb" />
</template>

<script>
  import { defineComponent } from 'vue';
  import ContractList from '../components/ContractList.vue';

  export default defineComponent({
    name: 'FactoryWbContractList',
    components: {
      ContractList
    }
  });
</script>
