<template>
  <div class="app-container calendar-list-container">
    <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
      <el-form-item label="原密码" prop="oldPassword">
        <el-input type="password" v-model="dataForm.oldPassword"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input type="password" v-model="dataForm.newPassword" auto-complete="off"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="newPassword2">
        <el-input type="password" v-model="dataForm.newPassword2" auto-complete="off"></el-input>
      </el-form-item>
    </el-form>
    <div style='margin-left:100px;'>
    <el-button @click="cancel">取消</el-button>
    <el-button type="primary" @click="change">确定</el-button>
    </div>
  </div>
</template>

<script>
import { changePassword } from '@/api/profile'

export default {
  name: 'ChangePassword',
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.dataForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      dataForm: {
        oldPassword: '',
        newPassword: '',
        newPassword2: ''
      },
      rules: {
        oldPassword: [
          { required: true, message: '旧密码不能为空', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '新密码不能为空', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ],
        newPassword2: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    cancel() {
      this.dataForm = {
        oldPassword: '',
        newPassword: '',
        newPassword2: ''
      }
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    change() {
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return
        }
        changePassword(this.dataForm).then(response => {
          this.$notify.success({
            title: '成功',
            message: '修改密码成功'
          })
        }).catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
      })
    }
  }
}
</script>
