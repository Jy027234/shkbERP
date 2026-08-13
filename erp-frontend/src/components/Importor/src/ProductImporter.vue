<template>
  <div>
    <excel-importer
      ref="importer"
      :tip-msg="'导入只支持新增航材信息。\n注：\n1、如果航材分类、制造商没有时，会自动创建。\n2、是否启用批次号管理、是否启用序列号管理必须二选一。\n3、务必核对填写的机型名称在基础信息中的机型是匹配的，避免导入失败。'"
      :download-template-url="downloadTemplate"
      :upload-url="upload"
      @confirm="onConfirm"
    />
  </div>
</template>

<script>
  import { defineComponent, h as _h } from 'vue';
  import ExcelImporter from '@/components/ExcelImporter';
  import * as api from '@/api/base-data/product/info';
  import { Modal } from 'ant-design-vue';

  export default defineComponent({
    name: 'ProductImporter',
    components: { ExcelImporter },
    data() {
      return {};
    },
    computed: {},
    methods: {
      normalizePayload(res) {
        // 兼容 AxiosResponse 与后端 {code,msg,data} 包装
        const level1 = res && res.data ? res.data : res;
        const level2 = level1 && level1.data ? level1.data : level1;
        return level2 || {};
      },
      openDialog() {
        this.$refs.importer.openDialog();
      },
      downloadTemplate() { 
        // return api.downloadImportTemplate();
        return api.downloadCustomImportTemplate();
      },
      upload(params) {
        // return api.importExcel(params);
        return api.importCustomExcel(params);
      },
      onConfirm(res) {
        const payload = this.normalizePayload(res);
         console.log('payload:', payload);
        const success = (payload && (payload.success ?? (payload.successDetails ? payload.successDetails.length : 0))) || 0;
        const failed = (payload && (payload.failed ?? (payload.failureDetails ? payload.failureDetails.length : 0))) || 0;
        const total = (payload && (payload.total ?? success + failed)) || success + failed;

        if (failed > 0) {
          const lines = (payload.failureDetails || []).slice(0, 50).map((s) => _h('div', { style: 'margin:2px 0;' }, s));
          Modal.info({
            title: '导入结果',
            width: 700,
            content: _h('div', null, [
              _h('div', { style: 'display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;' }, [
                _h('div', null, `总计 ${total} 条，成功 ${success} 条，失败 ${failed} 条`),
                _h('a', { href: 'javascript:void(0);', onClick: () => this.exportFailure(payload) }, '导出失败明细')
              ]),
              _h('div', { style: 'color:#ff4d4f;margin:6px 0;' }, '失败明细（前50条）：'),
              ...lines,
            ]),
          });
        } else {
          this.$message.success(`导入成功，共 ${total} 条（成功 ${success} 条）`);
        }

        this.$emit('confirm', payload);
      },
      exportFailure(res) {
        const payload = this.normalizePayload(res);
        const arr = payload && payload.failureDetails ? payload.failureDetails : [];
        const content = arr.join('\n');
        const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        const now = new Date();
        const pad = (n) => (n < 10 ? '0' + n : '' + n);
        const ts = `${now.getFullYear()}${pad(now.getMonth()+1)}${pad(now.getDate())}_${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
        a.href = url;
        a.download = `航材导入失败明细_${ts}.txt`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      },
    },
  });
</script>

<style lang="less"></style>
