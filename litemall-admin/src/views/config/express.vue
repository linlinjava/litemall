<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-width="300px">
      <el-form-item label="运费满减所需最低消费" prop="litemall_express_freight_min">
        <el-input v-model="dataForm.litemall_express_freight_min"/>
      </el-form-item>
      <el-form-item label="运费满减不足所需运费" prop="litemall_express_freight_value">
        <el-input v-model="dataForm.litemall_express_freight_value"/>
      </el-form-item>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </el-form-item>
  </el-form></div>
</template>

<script>
import { listExpress, updateExpress } from '@/api/config'

export default {
  name: 'ConfigExpress',
  data() {
    return {
      dataForm: {
        litemall_express_freight_min: 0,
        litemall_express_freight_value: 0
      },
      rules: {
        litemall_express_freight_min: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        litemall_express_freight_value: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      listExpress().then(response => {
        this.dataForm = response.data.data
      })
    },
    cancel() {
      this.init()
    },
    update() {
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return false
        }
        this.doUpdate()
      })
    },
    doUpdate() {
      updateExpress(this.dataForm).then(response => {
        this.$notify.success({
          title: '成功',
          message: '运费配置修改成功'
        })
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    }
  }
}
</script>
