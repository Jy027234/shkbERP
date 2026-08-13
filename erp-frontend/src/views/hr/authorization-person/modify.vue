<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="900px"
    title="编辑人员授权"
    :footer="null"
  >
    <div v-if="visible" v-permission="['hr:authorization:update']" v-loading="loading">
      <a-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-col="80px"
        layout="vertical"
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
            :disabled="true"
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
            :custom-request="handleCredentialUpload"
            :show-upload-list="false"
            accept=".pdf,.jpg,.jpeg,.png"
          >
            <a-button>
              <upload-outlined />
              {{ formData.credentialFile ? '重新上传' : '上传凭据' }}
            </a-button>
          </a-upload>
          <span v-if="formData.credentialFileName" class="file-name">{{ formData.credentialFileName }}</span>
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
  import { defineComponent } from 'vue';
  import { UploadOutlined, PlusOutlined } from '@ant-design/icons-vue';
  import DialogTable from '@/components/DialogTable';
  import * as api from '@/api/hr/authorization-person';
  import * as employeeApi from '@/api/hr/employee';
  import * as projectApi from '@/api/hr/authorization-project';

  export default defineComponent({
    name: 'HrPersonAuthorizationModify',
    components: {
      DialogTable,
    },
    props: {
      id: {
        type: String,
        default: ''
      }
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
        projectList: [],
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
          credentialFile: null,
          credentialFileName: '',
          credentialFileUrl: '',
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
        this.$nextTick(() => {
          this.loadFormData();
        });
      },
      async loadFormData() {
        this.loading = true;
        try {
          const data = await api.get(this.id);
          
          const projects = [];
          if (data.projectRelations && data.projectRelations.length > 0) {
            data.projectRelations.forEach(relation => {
              projects.push({
                projectId: relation.projectId,
                projectName: relation.projectName || '',
                authorizationDate: relation.authorizationDate || '',
                expiryDate: relation.expiryDate || '',
              });
            });
          }
          
          // 设置表单数据
          this.formData = {
            employeeId: data.employeeId || '',
            employeeName: data.employeeName || '',
            projects: projects.length > 0 ? projects : [
              {
                projectId: '',
                projectName: '',
                authorizationDate: '',
                expiryDate: '',
              }
            ],
            credentialFile: null,
            credentialFileName: data.credentialFileName || '',
            credentialFileUrl: data.credentialFileUrl || '',
            description: data.description || '',
          };
          
          this.$nextTick(() => {
            // 回显员工名称
            if (this.$refs.employeeDialogTable) {
              this.$refs.employeeDialogTable.label = data.employeeName || '';
            }
            
            // 等待一下，让所有 dialog-table 都挂载完成
            setTimeout(() => {
              console.log('projectDialogTables:', this.projectDialogTables);
              
              // 遍历我们收集到的所有 dialog-table 并设置 label
              this.projectDialogTables.forEach((table, index) => {
                if (table && projects[index]?.projectName) {
                  console.log('Setting label for index:', index, projects[index].projectName);
                  table.label = projects[index].projectName;
                }
              });
            }, 200);
          });
        } catch (error) {
          this.$message.error('获取数据失败');
        } finally {
          this.loading = false;
        }
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
      handleCredentialUpload(options) {
        const { file } = options;
        this.formData.credentialFile = file;
        this.formData.credentialFileName = file.name;
      },
      handleCancel() {
        this.visible = false;
        this.$refs.formRef?.resetFields();
        this.formData.employeeName = '';
      },
      async handleSubmit() {
        const valid = await this.$refs.formRef?.validate();
        if (!valid) return;
        
        this.loading = true;
        try {
          await api.update(this.id, this.formData.description);
          
          await api.updateProjects(this.id, this.formData.projects);
          
          this.$message.success('更新成功');
          this.visible = false;
          this.$refs.formRef?.resetFields();
          this.formData.employeeName = '';
          this.emit('confirm');
        } catch (error) {
          this.$message.error('更新失败');
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
