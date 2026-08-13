<template>
    <div>
      <contract-task-list contract-type="AVIATION" />
    </div>
  </template>
  
  <script>
  import { defineComponent } from 'vue';
  import ContractTaskList from '@/views/maintenance/components/ContractTaskList.vue';
  
  export default defineComponent({
    name: 'MaintenanceAviation',
    components: {
      ContractTaskList
    }
  });
  </script>