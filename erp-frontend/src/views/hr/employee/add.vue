<template>
  <a-modal
    v-model:open="visible"
    :mask-closable="false"
    width="800px"
    :footer="null"
    title="新增员工"
  >
    <template #close-icon>
      <a-icon 
        type="close" 
        style="cursor: pointer; font-size: 16px; color: #999;" 
        @click.stop="handleCancel"
      />
    </template>
    <div v-if="visible" v-permission="['hr:employee:create']" v-loading="loading">
      <a-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
        layout="horizontal"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="员工工号" field="code">
              <a-input v-model:value="formData.code" placeholder="请输入员工工号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="姓名" field="name">
              <a-input v-model:value="formData.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="性别" field="gender">
              <a-radio-group v-model:value="formData.gender">
                <a-radio :value="1">男</a-radio>
                <a-radio :value="2">女</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="身份证号" field="idCard">
              <a-input v-model:value="formData.idCard" placeholder="请输入身份证号" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="出生日期" field="birthday">
              <a-date-picker v-model:value="formData.birthday" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="民族" field="nation">
              <a-input v-model:value="formData.nation" placeholder="请输入民族" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="籍贯" field="nativePlace">
              <a-input v-model:value="formData.nativePlace" placeholder="请输入籍贯" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="政治面貌" field="politicalStatus">
              <a-select v-model:value="formData.politicalStatus" placeholder="请选择">
                <a-select-option value="中共党员">中共党员</a-select-option>
                <a-select-option value="共青团员">共青团员</a-select-option>
                <a-select-option value="群众">群众</a-select-option>
                <a-select-option value="民主党派">民主党派</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="学历" field="education">
              <a-select v-model:value="formData.education" placeholder="请选择">
                <a-select-option value="博士">博士</a-select-option>
                <a-select-option value="硕士">硕士</a-select-option>
                <a-select-option value="本科">本科</a-select-option>
                <a-select-option value="大专">大专</a-select-option>
                <a-select-option value="高中">高中</a-select-option>
                <a-select-option value="中专">中专</a-select-option>
                <a-select-option value="初中">初中</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="专业" field="major">
              <a-input v-model:value="formData.major" placeholder="请输入专业" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="毕业院校" field="graduateSchool">
              <a-input v-model:value="formData.graduateSchool" placeholder="请输入毕业院校" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="毕业日期" field="graduateDate">
              <a-date-picker v-model:value="formData.graduateDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="联系电话" field="phone">
              <a-input v-model:value="formData.phone" placeholder="请输入联系电话" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="电子邮箱" field="email">
              <a-input v-model:value="formData.email" placeholder="请输入电子邮箱" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="部门" field="deptId">
              <a-select v-model:value="formData.deptId" placeholder="请选择部门">
                <a-select-option v-for="dept in deptList" :key="dept.id" :value="dept.id">
                  {{ dept.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="职位" field="position">
              <a-input v-model:value="formData.position" placeholder="请输入职位" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="入职日期" field="entryDate">
              <a-date-picker v-model:value="formData.entryDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="转正日期" field="regularDate">
              <a-date-picker v-model:value="formData.regularDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="状态" field="status">
              <a-radio-group v-model:value="formData.status">
                <a-radio :value="1">在职</a-radio>
                <a-radio :value="2">试用期</a-radio>
                <a-radio :value="0">离职</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="紧急联系人" field="emergencyContact">
              <a-input v-model:value="formData.emergencyContact" placeholder="请输入紧急联系人" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="紧急联系电话" field="emergencyPhone">
              <a-input v-model:value="formData.emergencyPhone" placeholder="请输入紧急联系电话" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item label="现居住地址" field="address" :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }">
              <a-textarea v-model:value="formData.address" :rows="2" placeholder="请输入现居住地址" />
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
  import * as api from '@/api/hr/employee';

  export default defineComponent({
    name: 'HrEmployeeAdd',
    emits: ['confirm'],
    setup(_, { emit }) {
      return {
        emit,
      };
    },
    data() {
      return {
        visible: false,
        loading: false,
        deptList: [],
        formData: {
          code: '',
          name: '',
          gender: 1,
          idCard: '',
          birthday: null,
          nation: '',
          nativePlace: '',
          politicalStatus: '',
          education: '',
          major: '',
          graduateSchool: '',
          graduateDate: null,
          phone: '',
          email: '',
          address: '',
          emergencyContact: '',
          emergencyPhone: '',
          deptId: '',
          position: '',
          entryDate: null,
          regularDate: null,
          status: 1,
          description: '',
        },
        rules: {
          code: [
            { required: true, message: '请输入员工工号', trigger: 'blur' },
          ],
          name: [
            { required: true, message: '请输入姓名', trigger: 'blur' },
          ],
          gender: [
            { required: true, message: '请选择性别', trigger: 'change' },
          ],
          deptId: [
            { required: true, message: '请选择部门', trigger: 'change' },
          ],
          status: [
            { required: true, message: '请选择状态', trigger: 'change' },
          ],
        },
      };
    },
    methods: {
      openDialog() {
        // 重置 formData 为初始值
        this.formData = {
          code: '',
          name: '',
          gender: 1,
          idCard: '',
          birthday: null,
          nation: '',
          nativePlace: '',
          politicalStatus: '',
          education: '',
          major: '',
          graduateSchool: '',
          graduateDate: null,
          phone: '',
          email: '',
          address: '',
          emergencyContact: '',
          emergencyPhone: '',
          deptId: '',
          position: '',
          entryDate: null,
          regularDate: null,
          status: 1,
          description: '',
        };
        this.visible = true;
        this.$nextTick(() => this.$refs.formRef?.resetFields());
        this.loadDeptList();
      },
      async loadDeptList() {
        try {
          const res = await api.getDepts();
          this.deptList = res || [];
        } catch (error) {
          // 错误已在拦截器处理
        }
      },
      // 检查表单是否有填写内容
      hasFormData() {
        const { formData } = this;
        console.log('Current formData:', formData);
        
        // 排除默认值的检查
        const hasData = Object.entries(formData).some(([key, value]) => {
          // 排除默认值
          if ((key === 'gender' && value === 1) || (key === 'status' && value === 1)) {
            return false;
          }
          
          if (value === null || value === undefined) return false;
          if (typeof value === 'string') return value.trim() !== '';
          return true;
        });
        
        console.log('Form data check result:', hasData);
        return hasData;
      },
      // 处理取消操作
      handleCancel() {
        if (this.hasFormData()) {
          this.$confirm({
            title: '提示',
            content: '表单已填写内容，确定要取消吗？取消后将丢失当前填写的内容。',
            onOk: () => {
              this.visible = false;
              this.$refs.formRef?.resetFields();
            },
            onCancel: () => {
              // 取消关闭
            },
          });
        } else {
          this.visible = false;
          this.$refs.formRef?.resetFields();
        }
      },
      // 处理关闭前的确认
      handleBeforeClose(done) {
        console.log('====================================');
        console.log('handleBeforeClose called!');
        console.log('done function:', typeof done);
        
        const hasData = this.hasFormData();
        console.log('hasFormData result:', hasData);
        
        if (hasData) {
          console.log('Has form data, showing confirm');
          this.$confirm({
            title: '提示',
            content: '表单已填写内容，确定要取消吗？取消后将丢失当前填写的内容。',
            onOk: () => {
              console.log('User confirmed close');
              this.$refs.formRef?.resetFields();
              done();
            },
            onCancel: () => {
              console.log('User cancelled close');
              // 取消关闭
            },
          });
        } else {
          console.log('No form data, closing directly');
          this.$refs.formRef?.resetFields();
          done();
        }
      },
      async handleSubmit() {
        const valid = await this.$refs.formRef?.validate();
        if (!valid) return;
        
        this.loading = true;
        try {
          await api.create(this.formData);
          this.$message.success('新增成功');
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
