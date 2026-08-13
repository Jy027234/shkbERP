<template>
  <div>
    <contract-task-list contract-type="FACTORY_WB" />
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import ContractTaskList from '@/views/maintenance/components/ContractTaskList.vue';

export default defineComponent({
  name: 'MaintenanceFactoryWb',
  components: {
    ContractTaskList
  }
});
</script>