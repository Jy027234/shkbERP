<template>
  <a-modal :open="open" width="1000px" :footer="null" @cancel="onClose">
    <div class="report-container">
      <table class="header-table">
        <tbody>
          <tr>
            <th colspan="14" style="font-size: 20px;">报表</th>
          </tr>
          <tr>
            <th>日期</th><td colspan="3">{{ header.Date }}</td>
            <th>工作指令</th><td colspan="5">{{ header.TechnologyName }}</td>
            <th>合同号</th><td colspan="3">{{ header.ContractNo }}</td>
          </tr>
          <tr>
            <th>机型</th><td colspan="3">{{ header.AircraftType }}</td>
            <th>机轮位置</th><td colspan="5">{{ header.AircraftWheelPos }}</td>
            <th>螺丝数量</th><td colspan="3">{{ header.ScrewsNo }}</td>
          </tr>
          <tr>
            <th>机轮件号</th><td colspan="3">{{ header.AircraftWheelType }}</td>
            <th>机轮序号</th><td colspan="5">{{ header.WheelProductNo }}</td>
            <th>操作者</th><td colspan="3">{{ header.Operator }}</td>
          </tr>
          <tr>
            <th>初始力矩</th><td colspan="2" style="min-width: 60px;">{{ header.PreTorque }}</td>
            <th>最终力矩</th><td colspan="2">{{ header.FinalTorque }}</td>
            <th>转角角度</th><td colspan="2" style="min-width: 60px;">{{ header.Angel }}</td>
            <th>最大力矩</th><td colspan="2">{{ header.MaxTorque }}</td>
            <th>最小力矩</th><td colspan="2" style="min-width: 60px;">{{ header.MinTorque }}</td>
          </tr>
        </tbody>
      </table>

      <vxe-table :data="data" border align="center" header-align="center">
        <vxe-column field="ID" title="序号" min-width="60" />
        <vxe-column field="ScrewNo" title="螺栓号" width="60" />
        <vxe-column field="PreTime" title="初始时间" width="120" />
        <vxe-column field="PreTorque" title="初始力矩" width="100" />
        <vxe-column field="PreAngel" title="初始角度" width="100" />
        <vxe-column field="PreStatus" title="初始状态" width="90" />
        <vxe-column field="FinalTime" title="最终时间" width="120" />
        <vxe-column field="FinalTorque" title="最终力矩" width="100" />
        <vxe-column field="FinalAngel" title="最终角度" width="100" />
        <vxe-column field="FinalStatus" title="最终状态" width="90" />
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
        Date: '',
        TechnologyName: '',
        ContractNo: '',
        AircraftType: '',
        AircraftWheelPos: '',
        ScrewsNo: '',
        AircraftWheelType: '',
        WheelProductNo: '',
        Operator: '',
        PreTorque: 0,
        FinalTorque: 0,
        Angel: 0,
        MaxTorque: 0,
        MinTorque: 0,
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
      this.header = payload?.header || this.header;
      this.data = payload?.data || [];
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

