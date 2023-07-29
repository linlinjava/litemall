<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-width="300px">
      <el-form-item :label="$t('config_order.form.unpaid')" prop="litemall_order_unpaid">
        <el-input v-model="dataForm.litemall_order_unpaid" class="input-width">
          <template slot="append">{{ $t('config_order.text.minutes') }}</template>
        </el-input>
        <span class="info">{{ $t('config_order.help.unpaid') }}</span>
      </el-form-item>
      <el-form-item :label="$t('config_order.form.unconfirm')" prop="litemall_order_unconfirm">
        <el-input v-model="dataForm.litemall_order_unconfirm" class="input-width">
          <template slot="append"> {{ $t('config_order.text.days') }}</template>
        </el-input>
        <span class="info">{{ $t('config_order.help.unconfirm') }}</span>
      </el-form-item>
      <el-form-item :label="$t('config_order.form.comment')" prop="litemall_order_comment">
        <el-input v-model="dataForm.litemall_order_comment" class="input-width">
          <template slot="append">{{ $t('config_order.text.days') }}</template>
        </el-input>
        <span class="info">{{ $t('config_order.help.comment') }}</span>
      </el-form-item>
      <el-form-item>
        <el-button @click="cancel">{{ $t('app.button.cancel') }}</el-button>
        <el-button type="primary" @click="update">{{ $t('app.button.confirm') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { listOrder, updateOrder } from '@/api/config'

export default {
  name: 'ConfigOrder',
  data() {
    return {
      dataForm: {
        litemall_order_unpaid: 0,
        litemall_order_unconfirm: 0,
        litemall_order_comment: 0
      },
      rules: {
        litemall_order_unpaid: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        litemall_order_unconfirm: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        litemall_order_comment: [
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
      listOrder().then(response => {
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
      updateOrder(this.dataForm)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '订单参数配置成功'
          })
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    }
  }
}
</script>
<style scoped>
  .input-width {
    width: 50%;
  }
  .info {
    margin-left: 15px;
  }
</style>
