<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID" v-model="listQuery.userId">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入订单编号" v-model="listQuery.orderSn">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-download" @click="handleDownload" :loading="downloadLoading">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
        </template>
      </el-table-column>

      <el-table-column align="center" width="100px" label="订单ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="用户ID" prop="userId">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="订单编号" prop="orderSn">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="订单状态" prop="orderStatus">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="是否删除" prop="isDelete">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isDelete ? 'success' : 'error' ">{{scope.row.isDelete ? '未删除' : '已删除'}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="订单费用" prop="orderPrice">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="实际费用" prop="actualPrice">
      </el-table-column>

      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleSend(scope.row)">发货</el-button>
          <el-button type="primary" size="mini"  @click="handleRecv(scope.row)">收货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page"
        :page-sizes="[10,20,30,50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <!-- 发货对话框 -->
    <el-dialog title="发货" :visible.sync="sendDialogFormVisible">
      <el-form ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="快递公司" prop="shipChannel">
          <el-input v-model="dataForm.shipChannel"></el-input>
        </el-form-item>
        <el-form-item label="快递编号" prop="shipSn">
          <el-input v-model="dataForm.shipSn"></el-input>
        </el-form-item>
        <el-form-item label="快递发货时间" prop="shipStartTime">
          <el-date-picker v-model="dataForm.shipStartTime" type="datetime" placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="sendDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="sendData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 收货对话框 -->
    <el-dialog title="收货" :visible.sync="recvDialogFormVisible">
      <el-form ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
       <el-form-item label="快递公司" prop="shipChannel">
          <el-input disabled v-model="dataForm.shipChannel"></el-input>
        </el-form-item>
        <el-form-item label="快递编号" prop="shipSn">
          <el-input disabled v-model="dataForm.shipSn"></el-input>
        </el-form-item>
        <el-form-item label="快递发货时间" prop="shipStartTime">
          <el-date-picker disabled v-model="dataForm.shipStartTime" type="datetime" placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>        
        <el-form-item label="快递收货时间" prop="shipEndTime">
          <el-date-picker v-model="dataForm.shipEndTime" type="datetime" placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="recvDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="recvData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 200px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }
</style>

<script>
import { listOrder, updateOrder } from '@/api/order'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'Order',
  directives: {
    waves
  },
  data() {
    return {
      list: undefined,
      total: undefined,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        sort: '+id'
      },
      dataForm: {
        id: undefined,
        shipChannel: undefined,
        shipSn: undefined,
        shipStartTime: undefined,
        shipEndTime: undefined
      },
      sendDialogFormVisible: false,
      recvDialogFormVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listOrder(this.listQuery).then(response => {
        this.list = response.data.data.items
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    resetForm(row) {
      this.dataForm.id = row.id
      this.dataForm.shipChannel = row.shipChannel
      this.dataForm.shipSn = row.shipSn
      this.dataForm.shipStartTime = row.shipStartTime
      this.dataForm.shipEndTime = row.shipEndTime
    },
    handleSend(row) {
      this.resetForm(row)
      this.sendDialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    sendData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateOrder(this.dataForm).then(response => {
            const updatedOrder = response.data.data
            for (const v of this.list) {
              if (v.id === updatedOrder.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, updatedOrder)
                break
              }
            }
            this.sendDialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleRecv(row) {
      this.resetForm(row)
      this.recvDialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    recvData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateOrder(this.dataForm).then(response => {
            const updatedOrder = response.data.data
            for (const v of this.list) {
              if (v.id === updatedOrder.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, updatedOrder)
                break
              }
            }
            this.recvDialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['订单ID', '订单编号', '用户ID', '订单状态', '是否删除', '收货人', '收货联系电话', '收货地址']
        const filterVal = ['id', 'orderSn', 'userId', 'orderStatis', 'isDelete', 'consignee', 'mobile', 'address']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '订单信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
