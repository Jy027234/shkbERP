<template>
  <contract-list contract-type="aviation" />
</template>

<script>
  import { defineComponent } from 'vue';
  import ContractList from '../components/ContractList.vue';

  export default defineComponent({
    name: 'AviationContractList',
    components: {
      ContractList
    }
  });
</script>
