<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="600px"
    title="编辑实施计划"
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
        <a-form-item label="选择课程" field="courseId">
          <dialog-table
            ref="courseDialogTable"
            v-model:value="formData.courseId"
            :label="formData.courseName"
            :request="queryCourses"
            :load="loadCoursesDetail"
            :dialog-width="'800px'"
            :request-params="courseSearchForm"
            :option="{ label: 'courseName', value: 'id' }"
            :column-option="{ label: 'courseName', value: 'id' }"
            :table-column="courseColumns"
            placeholder="请选择培训课程"
            :immediate-load="false"
            @input-label="formData.courseName = $event"
          >
            <template #form>
              <j-border>
                <j-form>
                  <j-form-item label="课程名称">
                    <a-input v-model:value="courseSearchForm.courseName" placeholder="请输入课程名称" allow-clear />
                  </j-form-item>
                  <j-form-item label="状态">
                    <a-select v-model:value="courseSearchForm.status" placeholder="请选择状态" allow-clear style="width: 120px">
                      <a-select-option :value="1">启用</a-select-option>
                      <a-select-option :value="0">禁用</a-select-option>
                    </a-select>
                  </j-form-item>
                </j-form>
              </j-border>
            </template>
            <template #toolbar_buttons>
              <a-space class="operator">
                <a-button type="primary" @click="$refs.courseDialogTable.search()">查询</a-button>
              </a-space>
            </template>
          </dialog-table>
        </a-form-item>
        
        <a-form-item label="计划开始日期" field="planStartDate">
          <a-date-picker v-model:value="formData.planStartDate" style="width: 100%" value-format="YYYY-MM-DD" />
        </a-form-item>
        
        <a-form-item label="计划结束日期" field="planEndDate">
          <a-date-picker v-model:value="formData.planEndDate" style="width: 100%" value-format="YYYY-MM-DD" />
        </a-form-item>
        
        <a-form-item label="培训地点" field="trainingLocation">
          <a-input v-model:value="formData.trainingLocation" placeholder="请输入培训地点" />
        </a-form-item>
        
        <a-form-item label="培训讲师" field="instructor">
          <a-input v-model:value="formData.instructor" placeholder="请输入培训讲师" />
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
  import { defineComponent, onMounted, ref } from 'vue';
  import DialogTable from '@/components/DialogTable';
  import * as api from '@/api/hr/training-implementation';
  import * as courseApi from '@/api/hr/training-course';

  export default defineComponent({
    name: 'HrTrainingImplementationModify',
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
      const courseList = ref([]);
      
      const loadCourses = async () => {
        try {
          const res = await courseApi.queryTrainingCourses({ pageIndex: 1, pageSize: 100 });
          courseList.value = res.data.datas || [];
        } catch (error) {
          console.error('获取课程列表失败', error);
        }
      };
      
      const queryCourses = async (params) => {
        const res = await courseApi.queryTrainingCourses(params);
        // 统一返回格式，确保有 datas 属性
        if (res.data && res.data.datas) {
          return { datas: res.data.datas, total: res.data.totalCount || res.data.total };
        }
        if (res.datas) {
          return res;
        }
        return { datas: [], total: 0 };
      };

      const loadCoursesDetail = async (params) => {
        const validParams = params ? params.filter(p => p && p !== '') : [];
        if (validParams.length === 0) {
          return Promise.resolve([]);
        }
        // 使用专门的load接口获取课程详情
        const res = await courseApi.loadTrainingCourses(validParams);
        // 直接返回数组，后端返回的是课程列表数组
        return res || [];
      };
      
      onMounted(() => {
        loadCourses();
      });
      
      return {
        emit,
        courseList,
        loadCourses,
        queryCourses,
        loadCoursesDetail,
      };
    },
    data() {
      return {
        visible: false,
        loading: false,
        formData: {
          courseId: '',
          courseName: '',
          planStartDate: '',
          planEndDate: '',
          trainingLocation: '',
          instructor: '',
          description: '',
        },
        courseSearchForm: {
          courseName: '',
          status: undefined,
        },
        courseColumns: [
          {
            title: '课程名称',
            field: 'courseName',
            width: 200,
          },
          {
            title: '课程类型',
            field: 'courseType',
            width: 120,
          },
          {
            title: '培训时长(小时)',
            field: 'trainingHours',
            width: 120,
          },
          {
            title: '实施间隔',
            field: 'implementationInterval',
            width: 120,
          },
          {
            title: '初训时间',
            field: 'initialTrainingTime',
            width: 120,
          },
          {
            title: '复训时间',
            field: 'retrainingTime',
            width: 120,
          },
          {
            title: '状态',
            field: 'status',
            width: 80,
            slots: {
              default: 'status_default'
            },
          },
        ],
        rules: {
          courseId: [
            { required: true, message: '请选择培训课程', trigger: 'change' },
          ],
          planStartDate: [
            { required: true, message: '请选择计划开始日期', trigger: 'change' },
          ],
          planEndDate: [
            { required: true, message: '请选择计划结束日期', trigger: 'change' },
          ],
        },
      };
    },
    methods: {
      openDialog() {
        this.visible = true;
        this.$nextTick(() => {
          this.loadFormData();
        });
      },
      async loadFormData() {
        this.loading = true;
        try {
          const data = await api.get(this.id) || {};
          this.formData = {
            courseId: data.courseId || '',
            courseName: data.courseName || '',
            planStartDate: data.planStartDate || '',
            planEndDate: data.planEndDate || '',
            trainingLocation: data.trainingLocation || '',
            instructor: data.instructor || '',
            description: data.description || '',
          };
          // 手动触发 DialogTable 加载课程详情以显示课程名称
          if (data.courseId) {
            this.$nextTick(() => {
              this.$refs.courseDialogTable?.forceReloadValue();
            });
          }
        } catch (error) {
          this.$message.error('获取数据失败');
        } finally {
          this.loading = false;
        }
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
          const updateData = {
            id: this.id,
            ...this.formData
          };
          await api.update(updateData);
          this.$message.success('更新成功');
          this.visible = false;
          this.$refs.formRef?.resetFields();
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
</style>
