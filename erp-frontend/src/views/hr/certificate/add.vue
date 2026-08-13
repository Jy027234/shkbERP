<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="700px"
    title="新增证书"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:certificate:create']" v-loading="loading">
      <a-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="员工" field="employeeId">
              <dialog-table
                ref="employeeDialogTable"
                v-model:value="formData.employeeId"
                :label="formData.employeeName"
                :request="queryEmployees"
                :load="loadEmployeesDetail"
                :dialog-width="'800px'"
                :request-params="employeeSearchForm"
                :option="{ label: 'name', value: 'id' }"
                :column-option="{ label: 'name', value: 'id' }"
                :table-column="employeeColumns"
                placeholder="请选择员工"
                :immediate-load="false"
                @input-label="formData.employeeName = $event"
              >
                <template #form>
                  <j-border>
                    <j-form>
                      <j-form-item label="员工姓名">
                        <a-input v-model:value="employeeSearchForm.name" placeholder="请输入员工姓名" allow-clear />
                      </j-form-item>
                      <j-form-item label="员工工号">
                        <a-input v-model:value="employeeSearchForm.code" placeholder="请输入员工工号" allow-clear />
                      </j-form-item>
                    </j-form>
                  </j-border>
                </template>
                <template #toolbar_buttons>
                  <a-space class="operator">
                    <a-button type="primary" @click="$refs.employeeDialogTable.search()">查询</a-button>
                  </a-space>
                </template>
                <template #status_default="{ row }">
                  <span>{{ row.status === 1 ? '在职' : row.status === 0 ? '离职' : '试用期' }}</span>
                </template>
              </dialog-table>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="证书类型" field="certificateType">
              <a-input v-model:value="formData.certificateType" placeholder="请输入证书类型" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="证书名称" field="certificateName">
              <a-input v-model:value="formData.certificateName" placeholder="请输入证书名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="证书编号" field="certificateNo">
              <a-input v-model:value="formData.certificateNo" placeholder="请输入证书编号" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="发证机构" field="issueOrg">
              <a-input v-model:value="formData.issueOrg" placeholder="请输入发证机构" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发证日期" field="issueDate">
              <a-date-picker v-model:value="formData.issueDate" placeholder="请选择发证日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="有效期类型" field="validityType">
              <a-radio-group v-model:value="formData.validityType" @change="handleValidityTypeChange">
                <a-radio value="period">固定期限</a-radio>
                <a-radio value="permanent">长期有效</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" v-if="formData.validityType === 'period'">
          <a-col :span="12">
            <a-form-item label="有效期开始" field="validStartDate">
              <a-date-picker v-model:value="formData.validStartDate" placeholder="请选择有效期开始" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="有效期结束" field="validEndDate">
              <a-date-picker v-model:value="formData.validEndDate" placeholder="请选择有效期结束" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="状态" field="status">
              <a-radio-group v-model:value="formData.status">
                <a-radio :value="1">有效</a-radio>
                <a-radio :value="0">过期</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item label="备注" field="description" :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }">
              <a-textarea v-model:value="formData.description" :rows="3" placeholder="请输入备注" />
            </a-form-item>
          </a-col>
        </a-row>
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
  import * as api from '@/api/hr/certificate';
  import * as employeeApi from '@/api/hr/employee';

  export default defineComponent({
    name: 'HrCertificateAdd',
    components: {
      DialogTable,
    },
    emits: ['confirm'],
    setup(_, { emit }) {
      const queryEmployees = async (params) => {
        const res = await employeeApi.query(params);
        return res;
      };
      
      const loadEmployeesDetail = async (params) => {
        const validParams = params ? params.filter(p => p && p !== '') : [];
        if (validParams.length === 0) {
          return Promise.resolve([]);
        }
        const res = await employeeApi.query({ ids: validParams.join(',') });
        return res.data.datas || [];
      };
      
      return {
        emit,
        queryEmployees,
        loadEmployeesDetail,
      };
    },
    data() {
      return {
        visible: false,
        loading: false,
        formData: {
          employeeId: '',
          employeeName: '',
          certificateType: '',
          certificateName: '',
          certificateNo: '',
          issueOrg: '',
          issueDate: '',
          validityType: 'period',
          validStartDate: '',
          validEndDate: '',
          status: 1,
          description: '',
        },
        employeeSearchForm: {
          name: '',
          code: '',
        },
        employeeColumns: [
          {
            title: '员工姓名',
            field: 'name',
            width: 120,
          },
          {
            title: '员工工号',
            field: 'code',
            width: 120,
          },
          {
            title: '所属部门',
            field: 'deptName',
            width: 150,
          },
          {
            title: '联系电话',
            field: 'phone',
            width: 150,
          },
          {
            title: '状态',
            field: 'status',
            width: 80,
            slots: {
              default: 'status_default'
            }
          },
        ],
        rules: {
          employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
          certificateType: [{ required: true, message: '请输入证书类型', trigger: 'blur' }],
          certificateName: [{ required: true, message: '请输入证书名称', trigger: 'blur' }],
          status: [{ required: true, message: '请选择状态', trigger: 'change' }],
          validStartDate: [
            {
              validator: (rule, value, callback) => {
                if (this.formData.validityType === 'period' && !value) {
                  callback('请选择有效期开始');
                } else {
                  callback();
                }
              },
              trigger: 'change'
            }
          ],
          validEndDate: [
            {
              validator: (rule, value, callback) => {
                if (this.formData.validityType === 'period' && !value) {
                  callback('请选择有效期结束');
                } else {
                  callback();
                }
              },
              trigger: 'change'
            }
          ]
        },
      };
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => this.$refs.formRef?.resetFields());
      },
      handleCancel() {
        this.visible = false;
        this.$refs.formRef?.resetFields();
        this.formData.employeeName = '';
      },
      handleValidityTypeChange(e) {
        const type = e.target.value;
        if (type === 'permanent') {
          this.formData.validStartDate = '';
          this.formData.validEndDate = '';
        }
      },
      async handleSubmit() {
        try {
          await this.$refs.formRef?.validate();
          const params = {
            ...this.formData,
            validStartDate: this.formData.validityType === 'period' ? this.formData.validStartDate : '',
            validEndDate: this.formData.validityType === 'period' ? this.formData.validEndDate : ''
          };
          
          await api.create(params);
          this.$message.success('新增成功');
          this.visible = false;
          this.$refs.formRef?.resetFields();
          this.formData.employeeName = '';
          this.emit('confirm');
        } catch (error) {
          this.$message.error('新增失败');
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