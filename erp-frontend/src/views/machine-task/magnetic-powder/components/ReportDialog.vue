<template>
  <a-modal :open="open" width="1000px" :footer="null" @cancel="onClose">
    <div class="report-container">
      <table class="header-table">
        <tbody>
          <tr>
            <th colspan="14" style="font-size: 20px;">报表</th>
          </tr>
          <tr>
            <th>日期</th><td colspan="3">{{ header.date }}</td>
            <th>工作指令</th><td colspan="5">{{ header.workInstruction }}</td>
            <th>合同号</th><td colspan="3">{{ header.contractNumber }}</td>
          </tr>
          <tr>
            <th>机型</th><td colspan="3">{{ header.model }}</td>
            <th>机轮位置</th><td colspan="5">{{ header.wheelPosition }}</td>
            <th>螺丝数量</th><td colspan="3">{{ header.screwCount }}</td>
          </tr>
          <tr>
            <th>机轮件号</th><td colspan="3">{{ header.wheelPartNumber }}</td>
            <th>机轮序号</th><td colspan="5">{{ header.wheelSerialNumber }}</td>
            <th>操作者</th><td colspan="3">{{ header.operator }}</td>
          </tr>
          <tr>
            <th>初始力矩</th><td colspan="2" style="min-width: 60px;">{{ header.initialTorque }}</td>
            <th>最终力矩</th><td colspan="2">{{ header.finalTorque }}</td>
            <th>转角角度</th><td colspan="2" style="min-width: 60px;">{{ header.angle }}</td>
            <th>最大力矩</th><td colspan="2">{{ header.maxTorque }}</td>
            <th>最小力矩</th><td colspan="2" style="min-width: 60px;">{{ header.minTorque }}</td>
          </tr>
        </tbody>
      </table>

      <vxe-table :data="data" border align="center" header-align="center">
        <vxe-column field="index" title="序号" min-width="60" />
        <vxe-column field="boltNumber" title="螺栓号" width="60" />
        <vxe-column field="initialTime" title="初始时间" width="120" />
        <vxe-column field="initialTorque" title="初始力矩" width="100" />
        <vxe-column field="initialAngle" title="初始角度" width="100" />
        <vxe-column field="initialStatus" title="初始状态" width="90" />
        <vxe-column field="finalTime" title="最终时间" width="120" />
        <vxe-column field="finalTorque" title="最终力矩" width="100" />
        <vxe-column field="finalAngle" title="最终角度" width="100" />
        <vxe-column field="finalStatus" title="最终状态" width="90" />
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'ReportDialog',
  props: {
    modelValue: { type: Boolean, default: false },
  },
  emits: ['update:modelValue'],
  data() {
    return {
      open: this.modelValue,
      header: {
        date: '',
        model: '',
        wheelPartNumber: '',
        initialTorque: 0,
        finalTorque: 0,
        workInstruction: '',
        wheelPosition: '',
        wheelSerialNumber: '',
        angle: 0,
        maxTorque: 0,
        contractNumber: '',
        screwCount: 0,
        operator: '',
        minTorque: 0,
      },
      data: [],
    };
  },
  watch: {
    modelValue(val) {
      this.open = val;
    },
    open(val) {
      this.$emit('update:modelValue', val);
    },
  },
  methods: {
    onClose() {
      this.open = false;
    },
    setReportData(payload) {
      this.header = payload.header || this.header;
      this.data = payload.data || [];
    },
  },
};
</script>

<style scoped>
.report-container {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-table {
  width: 100%;
  border-collapse: collapse;
}
.header-table th,
.header-table td {
  border: 1px solid #ccc;
  padding: 6px 8px;
  text-align: center;
}
</style>
