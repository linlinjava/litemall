<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID" v-model="listQuery.userId">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入订单编号" v-model="listQuery.orderSn">
      </el-input>
      <el-select multiple style="width: 200px" class="filter-item" placeholder="请选择订单状态" v-model="listQuery.orderStatusArray">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value">
        </el-option>
      </el-select>      
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload" :loading="downloadLoading">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn">
      </el-table-column>

      <el-table-column align="center" label="用户ID" prop="userId">
      </el-table-column>

      <el-table-column align="center" label="订单状态" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag>{{scope.row.orderStatus | orderStatusFilter}}</el-tag>
        </template>
      </el-table-column>  

      <el-table-column align="center" label="订单金额" prop="orderPrice">
      </el-table-column>

      <el-table-column align="center" label="支付金额" prop="actualPrice">
      </el-table-column>

      <el-table-column align="center" label="支付时间" prop="payTime">
      </el-table-column>

      <el-table-column align="center" label="物流单号" prop="shipSn">
      </el-table-column>

      <el-table-column align="center" label="物流渠道" prop="shipChannel">
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>
          <el-button type="primary" size="mini" @click="handleShip(scope.row)" v-if="scope.row.orderStatus==201">发货</el-button>
          <el-button type="primary" size="mini"  @click="handleRefund(scope.row)" v-if="scope.row.orderStatus==202">退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page"
        :page-sizes="[10,20,30,50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>


    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" width="800" :visible.sync="orderDialogVisible">
  
    <el-form :data="orderDetail" label-position="left">
      <el-form-item label="订单编号">
        <span>{{ orderDetail.order.orderSn }}</span>
      </el-form-item>
      <el-form-item label="订单状态">
        <template slot-scope="scope">
          <el-tag>{{orderDetail.order.orderStatus | orderStatusFilter}}</el-tag>
        </template>
      </el-form-item>
      <el-form-item label="订单用户">
        <span>{{ orderDetail.user.nickname }}</span>
      </el-form-item>
      <el-form-item label="收货信息">
          <span>（收货人）{{ orderDetail.order.consignee }}</span>
          <span>（手机号）{{ orderDetail.order.mobile }}</span>
          <span>（地址）{{ orderDetail.order.address }}</span>
      </el-form-item>
      <el-form-item label="商品信息">
        <el-table size="small" :data="orderDetail.orderGoods" border fit highlight-current-row>
          <el-table-column align="center" label="商品名称" prop="goodsName" />
          <el-table-column align="center" label="商品编号" prop="goodsSn" />
          <el-table-column align="center" label="货品规格" prop="specifications" />
          <el-table-column align="center" label="货品价格" prop="price" />
          <el-table-column align="center" label="货品数量" prop="number" />
          <el-table-column align="center" label="货品图片" prop="picUrl">
            <template slot-scope="scope">
              <img :src="scope.row.picUrl" width="40"/>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item label="费用信息">
          <span>
          (实际费用){{ orderDetail.order.actualPrice }}元 =
          (商品总价){{ orderDetail.order.goodsPrice }}元 + 
          (快递费用){{ orderDetail.order.freightPrice }}元 -
          (优惠减免){{ orderDetail.order.couponPrice }}元 -
          (积分减免){{ orderDetail.order.integralPrice }}元
          </span>
      </el-form-item>
      <el-form-item label="支付信息">
        <span>（支付渠道）微信支付</span>
        <span>（支付时间）{{ orderDetail.order.payTime }}</span>
      </el-form-item>
      <el-form-item label="快递信息">
        <span>（快递公司）{{ orderDetail.order.shipChannel }}</span>
        <span>（快递单号）{{ orderDetail.order.shipSn }}</span>
        <span>（发货时间）{{ orderDetail.order.shipTime }}</span>
      </el-form-item>
      <el-form-item label="收货信息">
        <span>（确认收货时间）{{ orderDetail.order.confirmTime }}</span>
      </el-form-item>
    </el-form>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog title="发货" :visible.sync="shipDialogVisible">
      <el-form ref="shipForm" :model="shipForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="快递公司" prop="shipChannel">
          <el-input v-model="shipForm.shipChannel"></el-input>
        </el-form-item>
        <el-form-item label="快递编号" prop="shipSn">
          <el-input v-model="shipForm.shipSn"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定</el-button>
      </div>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog title="退款" :visible.sync="refundDialogVisible">
      <el-form ref="refundForm" :model="refundForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="退款金额" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" :disabled="true"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>

</style>

<script>
import { listOrder, shipOrder, refundOrder, detailOrder } from '@/api/order'

const statusMap = {
  101: '未付款',
  102: '用户取消',
  103: '系统取消',
  201: '已付款',
  202: '申请退款',
  203: '已退款',
  301: '已发货',
  401: '用户收货',
  402: '系统收货'
}

export default {
  name: 'Order',
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
        orderStatusArray: [],
        sort: 'add_time',
        order: 'desc'
      },
      statusMap,
      orderDialogVisible: false,
      orderDetail: {
        order: {},
        user: {},
        orderGoods: []
      },
      shipForm: {
        orderId: undefined,
        shipChannel: undefined,
        shipSn: undefined
      },
      shipDialogVisible: false,
      refundForm: {
        orderId: undefined,
        refundMoney: undefined
      },
      refundDialogVisible: false,
      downloadLoading: false
    }
  },
  filters: {
    orderStatusFilter(status) {
      return statusMap[status]
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
    handleDetail(row) {
      detailOrder(row.id).then(response => {
        this.orderDetail = response.data.data
      })
      this.orderDialogVisible = true
    },
    handleShip(row) {
      this.shipForm.orderId = row.id
      this.shipForm.shipChannel = row.shipChannel
      this.shipForm.shipSn = row.shipSn

      this.shipDialogVisible = true
      this.$nextTick(() => {
        this.$refs['shipForm'].clearValidate()
      })
    },
    confirmShip() {
      this.$refs['shipForm'].validate((valid) => {
        if (valid) {
          shipOrder(this.shipForm).then(response => {
            this.shipDialogVisible = false
            this.$notify({
              title: '成功',
              message: '确认发货成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleRefund(row) {
      this.refundForm.orderId = row.id
      this.refundForm.refundMoney = row.actualPrice

      this.refundDialogVisible = true
      this.$nextTick(() => {
        this.$refs['refundForm'].clearValidate()
      })
    },
    confirmRefund() {
      this.$refs['refundForm'].validate((valid) => {
        if (valid) {
          refundOrder(this.refundForm).then(response => {
            this.refundDialogVisible = false
            this.$notify({
              title: '成功',
              message: '确认退款成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['订单ID', '订单编号', '用户ID', '订单状态', '是否删除', '收货人', '收货联系电话', '收货地址']
        const filterVal = ['id', 'orderSn', 'userId', 'orderStatus', 'isDelete', 'consignee', 'mobile', 'address']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '订单信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
