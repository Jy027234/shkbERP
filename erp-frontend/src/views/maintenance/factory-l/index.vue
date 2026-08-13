<template>
  <div>
    <contract-task-list contract-type="FACTORY_L" />
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import ContractTaskList from '@/views/maintenance/components/ContractTaskList.vue';

export default defineComponent({
  name: 'MaintenanceFactoryL',
  components: {
    ContractTaskList
  }
});
</script>