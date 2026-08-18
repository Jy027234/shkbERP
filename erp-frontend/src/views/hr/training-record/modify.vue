<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="700px"
    title="编辑培训记录"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:training:update']" v-loading="loading">
      <a-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-col="80px"
        layout="vertical"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="员工" field="employeeId">
              <dialog-table
                ref="employeeSelector"
                v-model:value="formData.employeeId"
                :label="formData.employeeName"
                :request="queryEmployees"
                :load="loadEmployees"
                :dialog-width="'60%'"
                :request-params="searchParams"
                :option="{ label: 'name', value: 'id' }"
                :column-option="{ label: 'name', value: 'id' }"
                :table-column="[
                  { field: 'code', title: '员工编号', width: 120 },
                  { field: 'name', title: '员工姓名', minWidth: 120 },
                  { field: 'phone', title: '手机号码', minWidth: 120 },
                  { field: 'deptName', title: '部门', minWidth: 120 },
                ]"
                placeholder="请选择员工"
                :immediate-load="false"
                @input-label="formData.employeeName = $event"
              >
                <template #form>
                  <j-border>
                    <j-form>
                      <j-form-item label="员工编号">
                        <a-input v-model:value="searchParams.code" placeholder="请输入员工编号" allow-clear />
                      </j-form-item>
                      <j-form-item label="员工姓名">
                        <a-input v-model:value="searchParams.name" placeholder="请输入员工姓名" allow-clear />
                      </j-form-item>
                      <j-form-item label="手机号码">
                        <a-input v-model:value="searchParams.phone" placeholder="请输入手机号码" allow-clear />
                      </j-form-item>
                    </j-form>
                  </j-border>
                </template>
                <template #toolbar_buttons>
                  <a-space class="operator">
                    <a-button type="primary" @click="$refs.employeeSelector.search()">查询</a-button>
                  </a-space>
                </template>
              </dialog-table>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="培训名称" field="trainingName">
              <a-input v-model:value="formData.trainingName" placeholder="请输入培训名称" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="培训类型" field="trainingType">
              <a-input v-model:value="formData.trainingType" placeholder="请输入培训类型" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="培训机构" field="trainingOrg">
              <a-input v-model:value="formData.trainingOrg" placeholder="请输入培训机构" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="开始日期" field="startDate">
              <a-date-picker
                v-model:value="formData.startDate"
                placeholder="请选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="结束日期" field="endDate">
              <a-date-picker
                v-model:value="formData.endDate"
                placeholder="请选择结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="培训学时" field="trainingHours">
              <a-input-number v-model:value="formData.trainingHours" placeholder="请输入培训学时" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="培训结果" field="trainingResult">
              <a-select v-model:value="formData.trainingResult" placeholder="请选择培训结果">
                <a-select-option value="优秀">优秀</a-select-option>
                <a-select-option value="良好">良好</a-select-option>
                <a-select-option value="合格">合格</a-select-option>
                <a-select-option value="不合格">不合格</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="证书编号" field="certificateNo">
              <a-input v-model:value="formData.certificateNo" placeholder="请输入证书编号" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="培训内容" field="trainingContent">
          <a-textarea v-model:value="formData.trainingContent" placeholder="请输入培训内容" :rows="3" />
        </a-form-item>
        
        <a-form-item label="备注" field="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入备注" :rows="2" />
        </a-form-item>
      </a-form>
      
      <div class="modal-footer">
        <a-button @click="handleCancel">取消</a-button>
        <a-button type="primary" @click="handleSubmit">提交</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
  import { defineComponent } from 'vue';
  import DialogTable from '@/components/DialogTable';
  import * as api from '@/api/hr/training-record';

  export default defineComponent({
    name: 'HrTrainingRecordModify',
    components: { DialogTable },
    props: {
      id: {
        type: String,
        default: ''
      }
    },
    emits: ['confirm'],
    setup(_, { emit }) {
      return {
        emit,
        queryEmployees: api.queryEmployees,
      };
    },
    data() {
      return {
        visible: false,
        loading: false,
        searchParams: {
          code: '',
          name: '',
          phone: '',
        },
        formData: {
          employeeId: '',
          employeeName: '',
          trainingName: '',
          trainingType: '',
          trainingOrg: '',
          trainingContent: '',
          startDate: '',
          endDate: '',
          trainingHours: undefined,
          trainingResult: '',
          certificateNo: '',
          description: '',
        },
        rules: {
          employeeId: [
            { required: true, message: '请选择员工', trigger: 'change' },
          ],
          trainingName: [
            { required: true, message: '请输入培训名称', trigger: 'blur' },
          ],
        },
      };
    },
    methods: {
      loadEmployees(params) {
        const validParams = params ? params.filter(p => p && p !== '') : [];
        if (validParams.length === 0) {
          return Promise.resolve([]);
        }
        return api.queryEmployees({ ids: validParams }).then((res) => {
          return res.datas || [];
        });
      },
      openDialog() {
        this.visible = true;
        this.$nextTick(() => {
          this.loadFormData();
        });
      },
      loadFormData() {
        if (!this.id) return;
        
        this.loading = true;
        return api.get(this.id).then((res) => {
          const data = res || {};
          this.formData = {
            employeeId: data.employeeId || '',
            employeeName: data.employeeName || '',
            trainingName: data.trainingName || '',
            trainingType: data.trainingType || '',
            trainingOrg: data.trainingOrg || '',
            trainingContent: data.trainingContent || '',
            startDate: data.startDate || '',
            endDate: data.endDate || '',
            trainingHours: data.trainingHours,
            trainingResult: data.trainingResult || '',
            certificateNo: data.certificateNo || '',
            description: data.description || '',
          };
          this.$nextTick(() => {
            if (this.$refs.employeeSelector) {
              this.$refs.employeeSelector.label = data.employeeName || '';
            }
          });
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
      },
      handleCancel() {
        this.visible = false;
        this.$refs.formRef?.resetFields();
      },
      async handleSubmit() {
        const valid = await this.$refs.formRef?.validate();
        if (!valid) return;
        
        this.loading = true;
        try {
          const params = {
            ...this.formData,
            id: this.id
          };
          await api.update(params);
          this.$message.success('更新成功');
          this.visible = false;
          this.$refs.formRef?.resetFields();
          this.emit('confirm');
        } catch (error) {
          // 错误已在拦截器处理
        } finally {
          this.loading = false;
        }
      },
    },
  });
</script>

<style scoped>
  .modal-footer {
    margin-top: 24px;
    text-align: right;
  }
</style>
