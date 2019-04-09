<template>
  <div class="app-container">
    <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-width="300px">
      <el-form-item label="下单后超期自动取消" prop="litemall_order_unpaid">
        <el-input v-model="dataForm.litemall_order_unpaid">
          <template slot="append">分钟</template>
        </el-input>
      </el-form-item>
      <el-form-item label="发货后超期自动确认收货" prop="litemall_order_unconfirm">
        <el-input v-model="dataForm.litemall_order_unconfirm">
          <template slot="append">天</template>
        </el-input>
      </el-form-item>
      <el-form-item label="确认收货后超期取消评论资格" prop="litemall_order_comment">
        <el-input v-model="dataForm.litemall_order_comment">
          <template slot="append">天</template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
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
      dataForm: {}
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
