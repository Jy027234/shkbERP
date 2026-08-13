<template>
  <div>
    <excel-importer
      ref="importer"
      :tip-msg="'批量修改仅支持按件号更新机型信息。\n注：\n1、仅会修改机型字段，其他信息不会变更。\n2、件号必须已在系统中存在。\n3、机型名称必须与基础资料中的机型名称完全一致，否则该行会失败。'"
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
    name: 'ProductBatchUpdateImporter',
    components: { ExcelImporter },
    methods: {
      normalizePayload(res) {
        const level1 = res && res.data ? res.data : res;
        const level2 = level1 && level1.data ? level1.data : level1;
        return level2 || {};
      },
      openDialog() {
        this.$refs.importer.openDialog();
      },
      downloadTemplate() {
        return api.downloadAviationBatchUpdateTemplate();
      },
      upload(params) {
        return api.importAviationBatchUpdateExcel(params);
      },
      onConfirm(res) {
        const payload = this.normalizePayload(res);
        const success = (payload && (payload.success ?? (payload.successDetails ? payload.successDetails.length : 0))) || 0;
        const failed = (payload && (payload.failed ?? (payload.failureDetails ? payload.failureDetails.length : 0))) || 0;
        const total = (payload && (payload.total ?? success + failed)) || success + failed;

        if (failed > 0) {
          const lines = (payload.failureDetails || []).map((s) =>
            _h('div', { style: 'margin:2px 0;' }, s),
          );
          Modal.info({
            title: '批量修改结果',
            width: 700,
            content: _h('div', null, [
              _h('div', { style: 'display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;' }, [
                _h('div', null, `总计 ${total} 条，成功 ${success} 条，失败 ${failed} 条`),
                _h('a', { href: 'javascript:void(0);', onClick: () => this.exportFailure(payload) }, '导出失败明细')
              ]),
              _h('div', { style: 'color:#ff4d4f;margin:6px 0;' }, '失败明细：'),
              _h(
                'div',
                {
                  style:
                    'max-height:400px;overflow:auto;border:1px solid #f0f0f0;padding:8px;border-radius:4px;background:#fffaf0;',
                },
                lines,
              ),
            ]),
          });
        } else {
          this.$message.success(`批量修改成功，共 ${total} 条（成功 ${success} 条）`);
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
        a.download = `航材批量修改机型失败明细_${ts}.txt`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      },
    },
  });
</script>

<style lang="less"></style>
