<template>
  <div v-permission="['machine-task:magnetic']">
    <page-wrapper content-full-height fixed-height>
      <vxe-grid
        id="MagneticRemoteFolders"
        ref="grid"
        resizable
        show-overflow
        highlight-hover-row
        row-id="path"
        :loading="loading"
        height="auto"
        :toolbar-config="toolbarConfig"
        :columns="tableColumn"
        :data="displayData"
      >
        <template #toolbar_buttons>
          <a-space>
            <a-input
              v-model:value="keyword"
              allow-clear
              style="width: 260px"
              placeholder="按文件夹名称搜索"
            />
            <a-button type="primary" @click="search">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
            <a-button type="primary" @click="loadFolders">
              <template #icon><SearchOutlined /></template>
              刷新
            </a-button>
          </a-space>
        </template>
      </vxe-grid>
      <!-- 文件列表弹窗 -->
      <a-modal
        v-model:open="fileDialogVisible"
        :title="fileDialogTitle"
        width="1000px"
        :footer="null"
        :body-style="{ maxHeight: '65vh', overflow: 'auto' }"
        destroyOnClose
      >
        <a-space direction="vertical" size="middle" style="width: 100%">
          <a-alert type="info" message="点击“查看图片”在下方弹窗预览，支持叠加标注与设置显示尺寸" show-icon />
          <a-space style="width: 100%; justify-content: space-between">
            <a-input
              v-model:value="fileKeyword"
              allow-clear
              style="width: 260px"
              placeholder="按文件名搜索"
            />
            <span style="color: #999">共 {{ filteredFileList.length }} 个文件</span>
          </a-space>
          <div style="max-height: 55vh; overflow: auto;">
            <vxe-table :data="filteredFileList" :loading="fileLoading" auto-resize stripe>
              <vxe-column type="seq" width="60" title="#" />
              <vxe-column field="name" title="文件名" min-width="260" />
              <vxe-column field="size" title="大小" width="130" align="right" :formatter="({ cellValue }) => formatSize(cellValue)" />
              <vxe-column field="ann" title="标注文件" width="100" :formatter="({ row }) => (row.ann ? '是' : '否')" />
              <vxe-column field="mtime" title="修改时间" width="180" :formatter="({ cellValue }) => formatTime(cellValue)" />
              <vxe-column title="操作" width="220">
                <template #default="{ row }">
                  <a-space>
                    <a-button type="link" size="small" @click="preview(row)">查看图片</a-button>
                  </a-space>
                </template>
              </vxe-column>
            </vxe-table>
          </div>
        </a-space>
      </a-modal>

      <!-- 图片预览弹窗 -->
      <a-modal v-model:open="previewVisible" title="图片预览" width="1000px" :mask-closable="true" :footer="null" destroyOnClose>
        <a-space style="margin-bottom: 12px">
          <span>叠加标注：</span>
          <a-segmented
            v-model:value="overlayFlag"
            :options="[{label:'否', value:0}, {label:'是', value:1}]"
            :disabled="!currentFileAnn"
          />
          <span>显示尺寸：</span>
          <a-input v-model:value="thumbSize" placeholder="例如 300x200，可留空" style="width: 180px" />
          <a-button type="primary" @click="refreshPreview">刷新</a-button>
          <a-button @click="downloadPreview">下载</a-button>
        </a-space>
        <div style="width: 100%; text-align: center;">
          <img :src="previewUrl" :style="previewStyle" class="preview-img" />
        </div>
      </a-modal>
    </page-wrapper>
  </div>
</template>

<script>
  // 提供给 tableColumn 的 formatter 闭包使用（模块作用域）
  function formatTimeHelper(val) {
    if (!val) return '-';
    try {
      const d = new Date(Number(val));
      if (isNaN(d.getTime())) return '-';
      const pad = (n) => (n < 10 ? '0' + n : '' + n);
      return (
        d.getFullYear() +
        '-' + pad(d.getMonth() + 1) +
        '-' + pad(d.getDate()) +
        ' ' + pad(d.getHours()) +
        ':' + pad(d.getMinutes()) +
        ':' + pad(d.getSeconds())
      );
    } catch (e) {
      return '-';
    }
  }

  import { defineComponent, h } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/machine-task/magnetic';

  export default defineComponent({
    name: 'MagneticRemoteBrowser',
    setup() {
      return { h, SearchOutlined };
    },
    data() {
      return {
        loading: false,
        tableData: [],
        keyword: '',
        fileKeyword: '',
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons',
          },
        },
        tableColumn: [
          { type: 'seq', width: 60, title: '序号' },
          {
            field: 'name',
            title: '文件夹',
            minWidth: 300,
            slots: {
              default: ({ row }) => {
                return [
                  h(
                    'a',
                    {
                      class: 'clickable-name',
                      onClick: () => this.openFolder(row),
                    },
                    row.name || '-'
                  ),
                ];
              },
            },
          },
          { field: 'path', title: '路径', minWidth: 320, visible: false },
          { field: 'count', title: '文件数', width: 100, align: 'right' },
          { field: 'ctime', title: '创建时间', width: 180, formatter: ({ cellValue }) => formatTimeHelper(cellValue) },
          { field: 'mtime', title: '修改时间', width: 180, formatter: ({ cellValue }) => formatTimeHelper(cellValue) },
        ],
        // 文件弹窗
        fileDialogVisible: false,
        fileDialogTitle: '',
        fileLoading: false,
        fileList: [],
        overlayFlag: 0, // 0 仅原图，1 叠加标注
        currentFileAnn: false, // 当前预览文件是否标注
        thumbSize: '', // 例如 300x200，仅用于前端显示尺寸
        previewUrl: '',
        previewVisible: false,
      };
    },
    computed: {
      displayData() {
        const kw = (this.keyword || '').trim().toLowerCase();
        if (!kw) return this.tableData;
        return this.tableData.filter((it) =>
          (it.name && String(it.name).toLowerCase().includes(kw))
        );
      },
      // 根据 thumbSize（例如 300x200）设置预览尺寸，仅前端渲染使用
      previewStyle() {
        const s = (this.thumbSize || '').trim().toLowerCase();
        const style = {};
        if (!s) return style;
        const m = s.match(/^(\d+)\s*[xX]\s*(\d+)$/);
        if (m) {
          style.width = m[1] + 'px';
          style.height = m[2] + 'px';
          style.objectFit = 'contain';
        }
        return style;
      },
      filteredFileList() {
        const kw = (this.fileKeyword || '').trim().toLowerCase();
        if (!kw) return this.fileList;
        return this.fileList.filter((it) =>
          (it.name && String(it.name).toLowerCase().includes(kw))
        );
      },
    },
    created() {
      this.loadFolders();
    },
    methods: {
      // 参照 index.vue 的做法：点击查询按钮触发一次加载
      search() {
        this.loadFolders();
      },
      // 打开文件夹查看
      openFolder(row) {
        this.fileDialogTitle = row.name || row.path || '文件列表';
        this.fileDialogVisible = true;
        this.loadFiles(row.path || row.name);
      },
      // 加载文件列表
      loadFiles(folder) {
        if (!folder) return;
        this.fileLoading = true;
        this.fileList = [];
        api
          .files(folder)
          .then((res) => {
            this.fileList = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
          })
          .finally(() => (this.fileLoading = false));
      },
      // 构造图片展示URL（overlay: 1/0 -> true/false；thumb 仅用于前端显示尺寸，不传给后端）
      buildImageUrl(path) {
        const overlay = this.currentFileAnn && this.overlayFlag === 1;
        // 初始预览不追加时间戳，避免双请求；需要强制刷新时使用 refreshPreview()
        return api.imageUrl(path, overlay);
      },
      // 触发预览
      preview(file) {
        this.currentFileAnn = !!file.ann;
        if (!this.currentFileAnn) this.overlayFlag = 0; // 非标注文件不允许叠加
        // 先设置 URL，再打开弹窗，避免渲染两次导致双请求
        this.previewUrl = this.buildImageUrl(file.path);
        this.previewVisible = true;
      },
      refreshPreview() {
        if (!this.previewUrl) return;
        // 通过重建URL并附加时间戳避免缓存
        const u = new URL(this.previewUrl, window.location.origin);
        u.searchParams.set('_t', String(Date.now()));
        this.previewUrl = u.toString();
      },
      download(file) {
        const url = this.buildImageUrl(file.path);
        const a = document.createElement('a');
        a.href = url;
        a.download = file.name || 'image';
        a.target = '_blank';
        a.click();
      },
      downloadPreview() {
        if (!this.previewUrl) return;
        const a = document.createElement('a');
        a.href = this.previewUrl;
        a.download = 'image';
        a.target = '_blank';
        a.click();
      },
      loadFolders() {
        this.loading = true;
        api
          .folders()
          .then((res) => {
            // 兼容后端可能返回 {code,msg,data}
            this.tableData = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
          })
          .finally(() => (this.loading = false));
      },
      // 文件大小格式化：B/KB/MB/GB
      formatSize(val) {
        const size = Number(val);
        if (!isFinite(size) || size < 0) return '-';
        const KB = 1024;
        const MB = KB * 1024;
        const GB = MB * 1024;
        if (size < KB) return size + ' B';
        if (size < MB) return (size / KB).toFixed(2) + ' KB';
        if (size < GB) return (size / MB).toFixed(2) + ' MB';
        return (size / GB).toFixed(2) + ' GB';
      },
      // 提供给模板 vxe-column 使用的方法版 formatter
      formatTimeFormatter({ cellValue }) {
        return formatTimeHelper(cellValue);
      },
      formatTime(val) {
        if (!val) return '-';
        try {
          const d = new Date(Number(val));
          if (isNaN(d.getTime())) return '-';
          const pad = (n) => (n < 10 ? '0' + n : '' + n);
          return (
            d.getFullYear() +
            '-' + pad(d.getMonth() + 1) +
            '-' + pad(d.getDate()) +
            ' ' + pad(d.getHours()) +
            ':' + pad(d.getMinutes()) +
            ':' + pad(d.getSeconds())
          );
        } catch (e) {
          return '-';
        }
      },
    },
  });
</script>

<style scoped>
.clickable-name {
  color: #1677ff;
  cursor: pointer;
  text-decoration: underline;
}
.preview-img {
  max-width: 100%;
  display: block;
}
</style>
