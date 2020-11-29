<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.nickname" clearable class="filter-item" style="width: 160px;" placeholder="请输入用户昵称" />
      <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 160px;" placeholder="请输入收货人名称" />
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 160px;" placeholder="请输入订单编号" />
      <el-date-picker v-model="listQuery.timeArray" type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss" class="filter-item" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" />
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择订单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
          <div v-for="item in props.row.goodsVoList" :key="item.id" class="order-goods">
            <div class="picture">
              <img :src="item.picUrl" width="40">
            </div>
            <div class="name">
              商品名称：{{ item.goodsName }}
            </div>
            <div class="spec">
              规格：{{ item.specifications.join('-') }}
            </div>
            <div class="price">
              单价：{{ item.price }} 元
            </div>
            <div class="num">
              数量：{{ item.number }} 件
            </div>
            <div class="price">
              小计：{{ item.price * item.number }} 元
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="120" label="订单编号" prop="orderSn" />

      <el-table-column align="center" label="用户头像" width="80">
        <template slot-scope="scope">
          <el-avatar :src="scope.row.avatar" />
        </template>
      </el-table-column>

      <el-table-column align="center" label="下单用户" prop="userName" />

      <el-table-column align="center" label="下单时间" prop="addTime" min-width="100">
        <template slot-scope="scope">
          {{ (scope.row.addTime || '').substring(0, 10) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="订单状态" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="订单金额" prop="orderPrice">
        <template slot-scope="scope">
          {{ scope.row.orderPrice }} 元
        </template>
      </el-table-column>

      <el-table-column align="center" label="实付金额" prop="actualPrice">
        <template slot-scope="scope">
          {{ scope.row.actualPrice }} 元
        </template>
      </el-table-column>

      <el-table-column align="center" label="支付时间" prop="payTime" />

      <el-table-column align="center" label="收货人" prop="consignee">
        <template slot-scope="scope">
          <span style="color:red; font-weight:bold;">{{ scope.row.consignee }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="收货电话" prop="mobile" min-width="100" />

      <el-table-column align="center" label="物流单号" prop="shipSn" />

      <el-table-column align="center" label="物流渠道" prop="shipChannel" />

      <el-table-column align="center" label="操作" width="250" class-name="oper">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          <el-button type="warning" size="mini" @click="handlePay(scope.row)">收款</el-button>
          <el-button type="primary" size="mini" @click="handleShip(scope.row)">发货</el-button>
          <el-button type="danger" size="mini" @click="handleRefund(scope.row)">退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800">
      <section ref="print">
        <el-form :data="orderDetail" label-position="left">
          <el-form-item label="订单编号">
            <span>{{ orderDetail.order.orderSn }}</span>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-tag>{{ orderDetail.order.orderStatus | orderStatusFilter }}</el-tag>
          </el-form-item>
          <el-form-item label="订单用户">
            <span>{{ orderDetail.user.nickname }}</span>
          </el-form-item>
          <el-form-item label="用户留言">
            <span>{{ orderDetail.order.message }}</span>
          </el-form-item>
          <el-form-item label="收货信息">
            <span>（收货人）{{ orderDetail.order.consignee }}</span>
            <span>（手机号）{{ orderDetail.order.mobile }}</span>
            <span>（地址）{{ orderDetail.order.address }}</span>
          </el-form-item>
          <el-form-item label="商品信息">
            <el-table :data="orderDetail.orderGoods" border fit highlight-current-row>
              <el-table-column align="center" label="商品名称" prop="goodsName" />
              <el-table-column align="center" label="商品编号" prop="goodsSn" />
              <el-table-column align="center" label="货品规格" prop="specifications" />
              <el-table-column align="center" label="货品价格" prop="price" />
              <el-table-column align="center" label="货品数量" prop="number" />
              <el-table-column align="center" label="货品图片" prop="picUrl">
                <template slot-scope="scope">
                  <img :src="scope.row.picUrl" width="40">
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
          <el-form-item label="退款信息">
            <span>（退款金额）{{ orderDetail.order.refundAmount }}元</span>
            <span>（退款类型）{{ orderDetail.order.refundType }}</span>
            <span>（退款备注）{{ orderDetail.order.refundContent }}</span>
            <span>（退款时间）{{ orderDetail.order.refundTime }}</span>
          </el-form-item>
          <el-form-item label="收货信息">
            <span>（确认收货时间）{{ orderDetail.order.confirmTime }}</span>
          </el-form-item>
        </el-form>
      </section>
      <span slot="footer" class="dialog-footer">
        <el-button @click="orderDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="printOrder">打 印</el-button>
      </span>
    </el-dialog>

    <!-- 收款对话框 -->
    <el-dialog :visible.sync="payDialogVisible" title="订单收款" width="40%" center>
      <el-form ref="payForm" :model="payForm" status-icon label-position="left" label-width="100px">
        <div style="margin-bottom: 10px;">
          确认当前订单（订单编号 {{ payForm.orderSn }} ) 已经完成线下收款  ？
        </div>
        <el-form-item label="订单金额" prop="oldMoney">
          <el-input-number v-model="payForm.oldMoney" :controls="false" disabled />
        </el-form-item>
        <el-form-item label="付款金额" prop="newMoney">
          <el-input-number v-model="payForm.newMoney" :controls="false" />
        </el-form-item>
      </el-form>
      <el-table :data="payForm.goodsList">
        <el-table-column property="goodsName" label="商品" />
        <el-table-column label="规格">
          <template slot-scope="scope">
            {{ scope.row.specifications.join('-') }}
          </template>
        </el-table-column>
        <el-table-column property="onumber" width="100" label="下单数量" />
        <!-- <el-table-column label="实际数量" width="100">
          <template slot-scope="scope">
            <el-input-number v-model="scope.row.number" :min="0" :controls="false" />
          </template>
        </el-table-column> -->
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确定</el-button>
      </div>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog :visible.sync="shipDialogVisible" title="发货">
      <el-form ref="shipForm" :model="shipForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="快递公司" prop="shipChannel">
          <el-select v-model="shipForm.shipChannel" placeholder="请选择">
            <el-option v-for="item in channels" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递编号" prop="shipSn">
          <el-input v-model="shipForm.shipSn" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定</el-button>
      </div>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog :visible.sync="refundDialogVisible" title="退款">
      <el-form ref="refundForm" :model="refundForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="退款金额" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" :disabled="true" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style lang="scss" scoped>
.el-table--medium th, .el-table--medium td {
    padding: 3px 0;
}

.el-input-number--medium {
  width: 100%;
}

.oper .el-button--mini {
  padding: 7px 4px;
  width: 40px;
  font-size: 10px;
  margin-left: 1px;
}

::v-deep .el-table__expanded-cell {
  padding: 6px 80px;
}

.order-goods {
  display: flex;
  justify-content: space-around;
  justify-items: center;
  align-items:center;
  padding: 6px 0;
}

.name {
  width: 400px;
}

.spec {
  width: 180px;
}

.price {
  width: 120px;
}

.num {
  width: 120px;
}
</style>

<script>
import { detailOrder, listOrder, listChannel, refundOrder, payOrder, deleteOrder, shipOrder } from '@/api/order'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import checkPermission from '@/utils/permission' // 权限判断函数

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
  components: { Pagination },
  filters: {
    orderStatusFilter(status) {
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        nickname: undefined,
        consignee: undefined,
        orderSn: undefined,
        timeArray: [],
        orderStatusArray: [],
        sort: 'add_time',
        order: 'desc'
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
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
      payForm: {
        orderId: undefined,
        orderSn: '',
        oldMoney: 0,
        newMoney: 0,
        goodsList: []
      },
      payDialogVisible: false,
      refundForm: {
        orderId: undefined,
        refundMoney: undefined
      },
      refundDialogVisible: false,
      downloadLoading: false,
      channels: []
    }
  },
  created() {
    this.getList()
    this.getChannel()
  },
  methods: {
    checkPermission,
    getList() {
      this.listLoading = true
      if (this.listQuery.timeArray && this.listQuery.timeArray.length === 2) {
        this.listQuery.start = this.listQuery.timeArray[0]
        this.listQuery.end = this.listQuery.timeArray[1]
      } else {
        this.listQuery.start = null
        this.listQuery.end = null
      }
      if (this.listQuery.orderId) {
        detailOrder(this.listQuery.orderId).then(response => {
          this.list = []
          if (response.data.data.order) {
            this.list.push(response.data.data.order)
            this.total = 1
            this.listLoading = false
          }
        }).catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
      } else {
        listOrder(this.listQuery).then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        }).catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
      }
    },
    getChannel() {
      listChannel().then(response => {
        this.channels = response.data.data
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleDetail(row) {
      detailOrder(row.id).then(response => {
        this.orderDetail = response.data.data
      })
      this.orderDialogVisible = true
    },
    handlePay(row) {
      this.payForm.orderId = row.id
      this.payForm.orderSn = row.orderSn
      this.payForm.oldMoney = row.actualPrice
      this.payForm.newMoney = row.actualPrice
      this.payForm.goodsList = row.goodsVoList
      this.payForm.goodsList.forEach(element => {
        element.onumber = element.number
      })
      this.payDialogVisible = true
    },
    confirmPay() {
      if (this.payForm.oldMoney !== this.payForm.newMoney) {
        const diff = this.payForm.newMoney - this.payForm.oldMoney
        this.$confirm('差额 ' + diff + '元， 是否确认提交')
          .then(_ => {
            this.confirmPay2()
          })
          .catch(_ => {})
      } else {
        this.confirmPay2()
      }
    },
    confirmPay2() {
      payOrder(this.payForm).then(response => {
        this.$notify.success({
          title: '成功',
          message: '订单收款操作成功'
        })
        this.getList()
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      }).finally(() => {
        this.payDialogVisible = false
      })
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
            this.$notify.success({
              title: '成功',
              message: '确认发货成功'
            })
            this.getList()
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
        }
      })
    },
    handleDelete(row) {
      deleteOrder({ orderId: row.id }).then(response => {
        this.$notify.success({
          title: '成功',
          message: '订单删除成功'
        })
        this.getList()
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
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
            this.$notify.success({
              title: '成功',
              message: '确认退款成功'
            })
            this.getList()
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
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
    },
    printOrder() {
      this.$print(this.$refs.print)
      this.orderDialogVisible = false
    }
  }
}
</script>
