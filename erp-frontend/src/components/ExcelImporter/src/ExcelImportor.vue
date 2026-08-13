<template>
  <div>
    <a-modal
      v-model:open="visible"
      :mask-closable="false"
      width="40%"
      title="导入"
      :style="{ top: '20px' }"
      :footer="null"
      @cancel="onCancel"
    >
      <div v-loading="loading">
        <div>
          <a-upload-dragger
            name="file"
            accept=".xls,.xlsx"
            :custom-request="doUpload"
            :show-upload-list="false"
          >
            <p class="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p class="ant-upload-text"> 点击或拖拽文件进行导入 </p>
            <p class="ant-upload-hint"> 仅支持xls、xlsx格式 </p>
          </a-upload-dragger>
          <div style="margin-bottom: 8px"></div>
          <slot name="form"></slot>
          <div style="padding: 0 5px">
            <span
              v-if="!$utils.isEmpty(tipMsg)"
              style="font-size: 12px; color: #999999; white-space: pre-wrap"
              >{{ tipMsg }}</span
            >
          </div>
          <div class="content-wrapper">
            <a-space>
              <a-button type="link" block @click="doDownloadTemplate"> 下载模板文件 </a-button>
              <a-button type="link" block @click="clearAll"> 清空记录 </a-button>
            </a-space>
          </div>
        </div>
        <div>
          <a-tooltip title="处理进度" placement="bottom">
            <a-progress
              :percent="process"
              :success-percent="successProcess"
              title="处理进度"
              :status="status"
              style="margin-bottom: 5px"
            />
          </a-tooltip>
          <a-list v-if="!$utils.isEmpty(tipMsgs)" size="small" bordered :data-source="tipMsgs">
            <template #renderItem="{ item }">
              <a-list-item>
                <span style="color: #ff4d4f">{{ item }}</span>
              </a-list-item>
            </template>
            <template #header>
              <div> 失败原因 </div>
            </template>
          </a-list>
        </div>
      </div>
    </a-modal>
  </div>
</template>
<script>
  import { defineComponent } from 'vue';
  import { InboxOutlined } from '@ant-design/icons-vue';
  import * as api from '@/api/components';

  export default defineComponent({
    name: 'ExcelImporter',
    components: {
      InboxOutlined,
    },
    props: {
      // 下载模板url，传入request
      downloadTemplateUrl: {
        type: Function,
        required: true,
      },
      // 上传文件url，传入request
      uploadUrl: {
        type: Function,
        required: true,
      },
      // 执行导入url，传入request（可选）
      executeUrl: {
        type: Function,
        required: false,
      },
      // 提示信息
      tipMsg: {
        type: String,
        default: '',
      },
      // 表单数据
      formData: {
        type: Object,
        default: (e) => {},
      },
      // 完成后是否关闭对话框
      closeAfterFinish: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        visible: false,
        loading: false,
        process: 0,
        successProcess: 0,
        tipMsgs: [],
        timer: null,
        taskId: '',
        status: '',
        reqId: '',
        uploadResp: null,
      };
    },
    beforeUnmount() {
      this.clearTimer();
    },
    methods: {
      initData() {
        this.process = 0;
        this.tipMsgs = [];
        this.clearTimer();
        this.taskId = this.$utils.uuid();
        this.successProcess = 0;
        this.status = 'active';
        this.reqId = '';
        this.uploadResp = null;
      },
      openDialog() {
        this.initData();
        this.visible = true;
      },
      closeDialog() {
        this.visible = false;
      },
      // 下载导入模板
      doDownloadTemplate() {
        this.loading = true;
        this.downloadTemplateUrl(this.formData).finally(() => {
          this.loading = false;
        });
      },
      doUpload(e) {
        this.initData();
        this.loading = true;
        this.uploadUrl(
          Object.assign(
            {
              file: e.file,
            },
            { id: this.taskId },
            this.formData,
          ),
        ).then((resp) => {
          // 保存后端响应，供轮询完成后作为最终结果
          this.uploadResp = resp;
          // console.log('uploadResp:', resp);
          if (this.status !== 'exception') {
            this.process = 100;
            this.successProcess = 100;
          }
        });

        this.timer = setInterval(this.doTimer, 500);
      },
      // 二阶段执行：使用同一 taskId 展示进度
      execute(batchId) {
        if (!this.executeUrl) {
          console.warn('executeUrl not provided');
          return Promise.reject('executeUrl not provided');
        }
        if (!this.taskId) {
          this.taskId = this.$utils.uuid();
        }
        // 重置进度到执行阶段
        this.loading = true;
        this.process = 0;
        this.successProcess = 0;
        this.tipMsgs = [];
        this.status = 'active';
        this.clearTimer();
        this.timer = setInterval(this.doTimer, 500);
        return this.executeUrl(Object.assign({ batchId, id: this.taskId }, this.formData || {}))
          .then((resp) => {
            // 将执行结果也暂存，供完成时回传
            this.uploadResp = resp || this.uploadResp;
          })
          .catch((e) => {
            this.loading = false;
            this.clearTimer();
            return Promise.reject(e);
          });
      },
      doTimer() {
        if (!this.$utils.isEmpty(this.reqId)) {
          return;
        }
        this.reqId = this.$utils.uuid();
        this.getTask()
          .then((res) => {
            const total = Number(res.process || 0);
            const done = Number(res.successProcess || 0);
            let percent = total > 0 ? Math.floor((done / total) * 100) : 0;
            if (res.finished && !res.hasError) {
              percent = 100;
            }
            this.process = Math.max(this.process, percent);
            this.successProcess = this.process; // 统一展示一个百分比即可
            this.tipMsgs = res.tipMsgs;
            this.status = res.hasError ? 'exception' : 'active';

            if (res.finished) {
              this.clearTimer();
              this.loading = false;
              if (!res.hasError) {
                // 优先使用上传接口返回的响应；若不存在则回退到轮询接口的 data/res
                const payload = this.uploadResp
                  ? (Object.prototype.hasOwnProperty.call(this.uploadResp, 'data') && this.uploadResp.data != null
                      ? this.uploadResp.data
                      : this.uploadResp)
                  : (res && Object.prototype.hasOwnProperty.call(res, 'data'))
                      ? res.data
                      : res;
                // console.log('emit payload:', payload)
                this.$emit('confirm', payload);
                if (this.closeAfterFinish) {
                  this.closeDialog();
                }
              }
            }

            this.reqId = '';
          })
          .catch(() => {
            this.clearTimer();
            this.loading = false;
          });
      },
      getTask() {
        return api.getExcelImportTask(this.taskId);
      },
      clearTimer() {
        if (this.timer) {
          clearInterval(this.timer);
          this.timer = null;
        }
      },
      // 清空记录：重置内部状态并通知父组件清空外部提示
      clearAll() {
        this.clearTimer();
        // 先关闭再打开，触发完整重渲染
        this.visible = false;
        this.$nextTick(() => {
          this.initData();
          this.visible = true;
          this.$emit('cleared');
        });
      },
      onCancel() {
        this.clearTimer();
      },
    },
  });
</script>
<style lang="less" scoped>
  .content-wrapper {
    text-align: center;
  }
</style>
