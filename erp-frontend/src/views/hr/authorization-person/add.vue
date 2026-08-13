<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="900px"
    title="新增人员授权"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:authorization:create']" v-loading="loading">
      <a-form
        ref="form"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        :model="formData"
        :rules="rules"
      >
        <a-form-item label="选择员工" field="employeeId">
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
        
        <a-form-item label="授权项目" required>
          <div class="projects-container">
            <a-table
              :columns="projectTableColumns"
              :data-source="formData.projects"
              :pagination="false"
              size="small"
              bordered
            >
              <template #bodyCell="{ column, record, index }">
                <template v-if="column.key === 'projectId'">
                  <a-form-item
                    :name="['projects', index, 'projectId']"
                    :rules="[{ required: true, message: '请选择岗位', trigger: 'change' }]"
                    :no-style="true"
                  >
                    <dialog-table
                      :ref="(el) => setProjectDialogRef(el, index)"
                      v-model:value="record.projectId"
                      :label="record.projectName"
                      :request="queryProjects"
                      :load="loadProjectsDetail"
                      :dialog-width="'800px'"
                      :request-params="projectSearchForm"
                      :option="{ label: 'projectName', value: 'id' }"
                      :column-option="{ label: 'projectName', value: 'id' }"
                      :table-column="projectColumns"
                      placeholder="请选择岗位"
                      :immediate-load="false"
                      @input-label="record.projectName = $event"
                    >
                      <template #form>
                        <j-border>
                          <j-form>
                            <j-form-item label="岗位">
                              <a-input v-model:value="projectSearchForm.projectName" placeholder="请输入岗位" allow-clear />
                            </j-form-item>
                          </j-form>
                        </j-border>
                      </template>
                      <template #toolbar_buttons>
                        <a-space class="operator">
                          <a-button type="primary" @click="$refs.projectDialogTable.search()">查询</a-button>
                        </a-space>
                      </template>
                    </dialog-table>
                  </a-form-item>
                </template>
                <template v-else-if="column.key === 'authorizationDate'">
                  <a-form-item
                    :name="['projects', index, 'authorizationDate']"
                    :rules="[{ required: true, message: '请选择授权日期', trigger: 'change' }]"
                    :no-style="true"
                  >
                    <a-date-picker v-model:value="record.authorizationDate" style="width: 100%" value-format="YYYY-MM-DD" />
                  </a-form-item>
                </template>
                <template v-else-if="column.key === 'expiryDate'">
                  <a-form-item
                    :name="['projects', index, 'expiryDate']"
                    :rules="[{ required: true, message: '请选择到期日期', trigger: 'change' }]"
                    :no-style="true"
                  >
                    <a-date-picker v-model:value="record.expiryDate" style="width: 100%" value-format="YYYY-MM-DD" />
                  </a-form-item>
                </template>
                <template v-else-if="column.key === 'action'">
                  <a-button
                    type="link"
                    danger
                    size="small"
                    @click="removeProject(index)"
                    :disabled="formData.projects.length <= 1"
                  >
                    删除
                  </a-button>
                </template>
              </template>
            </a-table>
            <a-button type="dashed" style="width: 100%; margin-top: 8px" @click="addProject">
              <plus-outlined /> 新增授权项目
            </a-button>
          </div>
        </a-form-item>
        
        <a-form-item label="凭据附件">
          <a-upload
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            @remove="handleRemove"
            accept=".pdf,.jpg,.jpeg,.png"
          >
            <a-button>
              <upload-outlined />
              上传凭据
            </a-button>
          </a-upload>
          <div class="form-hint">支持 PDF、图片格式，一份凭据可同时证明多个授权项目的有效性</div>
        </a-form-item>
        
        <a-form-item label="备注" field="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入备注" :rows="3" />
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
  import { defineComponent, onMounted } from 'vue';
  import { UploadOutlined, PlusOutlined } from '@ant-design/icons-vue';
  import DialogTable from '@/components/DialogTable';
  import * as api from '@/api/hr/authorization-person';
  import * as employeeApi from '@/api/hr/employee';
  import * as projectApi from '@/api/hr/authorization-project';

  export default defineComponent({
    name: 'HrPersonAuthorizationAdd',
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
        const res = await employeeApi.query({ ids: validParams });
        return res.data.datas || [];
      };
      
      const queryProjects = async (params) => {
        const queryParams = {
          pageIndex: params.currentPage || params.pageIndex || 1,
          pageSize: params.pageSize || 20
        };
        
        if (params.projectName) {
          queryParams.projectName = params.projectName;
        }
        
        if (params.ids) {
          queryParams.ids = params.ids;
        }
        
        const res = await projectApi.query(queryParams);
        return res.data || res;
      };
      
      const loadProjectsDetail = async (params) => {
        const validParams = params ? params.filter(p => p && p !== '') : [];
        if (validParams.length === 0) {
          return Promise.resolve([]);
        }
        // 使用专门的load接口获取项目详情
        const res = await projectApi.loadAuthorizationProjects(validParams);
        // 直接返回数组，后端返回的是项目列表数组
        return res || [];
      };
      
      return {
        emit,
        UploadOutlined,
        PlusOutlined,
        queryEmployees,
        loadEmployeesDetail,
        queryProjects,
        loadProjectsDetail,
      };
    },
    data() {
      return {
        visible: false,
        loading: false,
        fileList: [],
        projectDialogTables: [],
        formData: {
          employeeId: '',
          employeeName: '',
          projects: [
            {
              projectId: '',
              projectName: '',
              authorizationDate: '',
              expiryDate: '',
            }
          ],
          description: '',
        },
        projectSearchForm: {
          projectName: '',
        },
        projectColumns: [
          {
            title: '岗位',
            field: 'projectName',
            width: 200,
          },
          {
            title: '授权项目/限制',
            field: 'authorizationItem',
            width: 200,
          },
          {
            title: '资质要求',
            field: 'qualificationRequirement',
            width: 200,
          },
          {
            title: '培训要求',
            field: 'trainingRequirement',
            width: 200,
          },
          {
            title: '备注',
            field: 'description',
            minWidth: 150,
          },
          {
            title: '有效期',
            field: 'validityPeriod',
            width: 100,
            formatter: function({ row }) {
              const period = row.validityPeriod || 0;
              const unit = row.validityUnit === 'month' ? '个月' : '年';
              return `${period}${unit}`;
            }
          },
          {
            title: '创建时间',
            field: 'createTime',
            width: 150,
          },
        ],
        projectTableColumns: [
          {
            title: '岗位',
            key: 'projectId',
            width: 250,
          },
          {
            title: '授权日期',
            key: 'authorizationDate',
            width: 180,
          },
          {
            title: '到期日期',
            key: 'expiryDate',
            width: 180,
          },
          {
            title: '操作',
            key: 'action',
            width: 80,
          },
        ],
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
          employeeId: [
            { required: true, message: '请选择员工', trigger: 'change' },
          ],
        },
      };
    },
    methods: {
      setProjectDialogRef(el, index) {
        if (el) {
          this.projectDialogTables[index] = el;
        }
      },
      openDialog() {
        this.visible = true;
        this.projectDialogTables = [];
        this.$nextTick(() => this.$refs.form?.resetFields());
        this.fileList = [];
        this.formData.projects = [
          {
            projectId: '',
            projectName: '',
            authorizationDate: '',
            expiryDate: '',
          }
        ];
      },
      addProject() {
        this.formData.projects.push({
          projectId: '',
          projectName: '',
          authorizationDate: '',
          expiryDate: '',
        });
      },
      removeProject(index) {
        this.formData.projects.splice(index, 1);
      },
      // 文件上传前处理
      beforeUpload(file) {
        const isLt10M = file.size / 1024 / 1024 < 10;
        if (!isLt10M) {
          this.$message.error('文件大小不能超过10MB!');
          return false;
        }
        return false;
      },
      // 文件变更处理
      handleFileChange(info) {
        this.fileList = [...info.fileList];
      },
      // 移除文件
      handleRemove(file) {
        const index = this.fileList.indexOf(file);
        const newFileList = this.fileList.slice();
        newFileList.splice(index, 1);
        this.fileList = newFileList;
      },
      handleCancel() {
        this.visible = false;
        this.$refs.form?.resetFields();
        this.formData.employeeName = '';
        this.fileList = [];
      },
      async handleSubmit() {
        console.log('Form data before validate:', this.formData);
        const valid = await this.$refs.form?.validate();
        console.log('Validation result:', valid);
        if (!valid) return;
        
        this.loading = true;
        try {
          const formData = new FormData();
          
          formData.append('employeeId', this.formData.employeeId);
          if (this.formData.description) {
            formData.append('description', this.formData.description);
          }
          
          if (this.fileList && this.fileList.length > 0) {
            this.fileList.forEach(file => {
              if (file.originFileObj) {
                formData.append('credentialFile', file.originFileObj);
              }
            });
          }
          
          if (this.formData.projects && this.formData.projects.length > 0) {
            formData.append('projects', JSON.stringify(this.formData.projects));
          }
          
          console.log('Calling api.create with formData');
          await api.create(formData);
          
          this.$message.success('新增成功');
          this.visible = false;
          this.$refs.form?.resetFields();
          this.formData.employeeName = '';
          this.fileList = [];
          this.emit('confirm');
        } catch (error) {
          this.$message.error('新增失败');
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

  .form-hint {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
  }

  .file-name {
    margin-left: 8px;
    color: #666;
  }

  .projects-container {
    width: 100%;
  }
</style>
