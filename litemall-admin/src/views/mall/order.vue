<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.nickname" clearable class="filter-item" style="width: 160px;" :placeholder="$t('mall_order.placeholder.filter_nickname')" />
      <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 160px;" :placeholder="$t('mall_order.placeholder.filter_consignee')" />
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 160px;" :placeholder="$t('mall_order.placeholder.filter_order_sn')" />
      <el-date-picker v-model="listQuery.timeArray" type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss" class="filter-item" :range-separator="$t('mall_order.text.date_range_separator')" :start-placeholder="$t('mall_order.placeholder.filter_time_start')" :end-placeholder="$t('mall_order.placeholder.filter_time_end')" :picker-options="pickerOptions" />
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" :placeholder="$t('mall_order.placeholder.filter_order_status')">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value" />
      </el-select>
      <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
          <div v-for="item in props.row.goodsVoList" :key="item.id" class="order-goods">
            <div class="picture">
              <img :src="item.picUrl" width="40">
            </div>
            <div class="name">
              {{ $t('mall_order.text.expand_goods_name', { goods_name: item.goodsName }) }}
            </div>
            <div class="spec">
              {{ $t('mall_order.text.expand_specifications', { specifications: item.specifications.join('-') }) }}
            </div>
            <div class="price">
              {{ $t('mall_order.text.expand_unit_price', { price: item.price }) }}
            </div>
            <div class="num">
              {{ $t('mall_order.text.expand_number', { number: item.number }) }}
            </div>
            <div class="price">
              {{ $t('mall_order.text.expand_subtotal_price', { price: item.price * item.number }) }}
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="120" :label="$t('mall_order.table.order_sn')" prop="orderSn" />

      <el-table-column align="center" :label="$t('mall_order.table.avatar')" width="80">
        <template slot-scope="scope">
          <el-avatar :src="scope.row.avatar" />
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('mall_order.table.user_name')" prop="userName" />

      <el-table-column align="center" :label="$t('mall_order.table.add_time')" prop="addTime" min-width="100">
        <template slot-scope="scope">
          {{ (scope.row.addTime || '').substring(0, 10) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="$t('mall_order.table.order_status')" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('mall_order.table.order_price')" prop="orderPrice">
        <template slot-scope="scope">
          {{ scope.row.orderPrice }} 元
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('mall_order.table.actual_price')" prop="actualPrice">
        <template slot-scope="scope">
          {{ scope.row.actualPrice }} 元
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('mall_order.table.pay_time')" prop="payTime" />

      <el-table-column align="center" :label="$t('mall_order.table.consignee')" prop="consignee">
        <template slot-scope="scope">
          <span style="color:red; font-weight:bold;">{{ scope.row.consignee }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('mall_order.table.mobile')" prop="mobile" min-width="100" />

      <el-table-column align="center" :label="$t('mall_order.table.ship_sn')" prop="shipSn" />

      <el-table-column align="center" :label="$t('mall_order.table.ship_channel')" prop="shipChannel" />

      <el-table-column align="center" :label="$t('mall_order.table.actions')" width="250" class-name="oper">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleDetail(scope.row)">{{ $t('app.button.detail') }}</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
          <el-button type="warning" size="mini" @click="handlePay(scope.row)">{{ $t('mall_order.button.pay') }}</el-button>
          <el-button type="primary" size="mini" @click="handleShip(scope.row)">{{ $t('mall_order.button.ship') }}</el-button>
          <el-button type="danger" size="mini" @click="handleRefund(scope.row)">{{ $t('mall_order.button.refund') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" :title="$t('mall_order.dialog.detail')" width="800">
      <section ref="print">
        <el-form :data="orderDetail" label-position="left">
          <el-form-item :label="$t('mall_order.form.detail_order_sn')">
            <span>{{ orderDetail.order.orderSn }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_order_status')">
            <el-tag>{{ orderDetail.order.orderStatus | orderStatusFilter }}</el-tag>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_user_nickname')">
            <span>{{ orderDetail.user.nickname }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_message')">
            <span>{{ orderDetail.order.message }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_receiving_info')">
            <span>{{ $t('mall_order.text.detail_consigne', { consignee: orderDetail.order.consignee }) }}</span>
            <span>{{ $t('mall_order.text.detail_mobile', { mobile: orderDetail.order.mobile }) }}</span>
            <span>{{ $t('mall_order.text.detail_address', { address: orderDetail.order.address }) }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_goods')">
            <el-table :data="orderDetail.orderGoods" border fit highlight-current-row>
              <el-table-column align="center" :label="$t('mall_order.table.detail_goods_name')" prop="goodsName" />
              <el-table-column align="center" :label="$t('mall_order.table.detail_goods_sn')" prop="goodsSn" />
              <el-table-column align="center" :label="$t('mall_order.table.detail_goods_specifications')" prop="specifications" />
              <el-table-column align="center" :label="$t('mall_order.table.detail_goods_price')" prop="price" />
              <el-table-column align="center" :label="$t('mall_order.table.detail_goods_number')" prop="number" />
              <el-table-column align="center" :label="$t('mall_order.table.detail_goods_pic_url')" prop="picUrl">
                <template slot-scope="scope">
                  <img :src="scope.row.picUrl" width="40">
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_price_info')">
            <span>
              {{ $t('mall_order.text.detail_price_info', {
                actual_price: orderDetail.order.actualPrice,
                goods_price: orderDetail.order.goodsPrice,
                freight_price: orderDetail.order.freightPrice,
                coupon_price: orderDetail.order.couponPrice,
                integral_price: orderDetail.order.integralPrice
              }) }}
            </span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_pay_info')">
            <span>{{ $t('mall_order.text.detail_pay_channel', { pay_channel: '微信支付' }) }}</span>
            <span>{{ $t('mall_order.text.detail_pay_time', { pay_time: orderDetail.order.payTime }) }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_ship_info')">
            <span>{{ $t('mall_order.text.detail_ship_channel', { ship_channel: orderDetail.order.shipChannel }) }}</span>
            <span>{{ $t('mall_order.text.detail_ship_sn', { ship_sn: orderDetail.order.shipSn }) }}</span>
            <span>{{ $t('mall_order.text.detail_ship_time', { ship_time: orderDetail.order.shipTime }) }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_refund_info')">
            <span>{{ $t('mall_order.text.detail_refund_amount', { refund_amount: orderDetail.order.refundAmount }) }}</span>
            <span>{{ $t('mall_order.text.detail_refund_type', { refund_type: orderDetail.order.refundType }) }}</span>
            <span>{{ $t('mall_order.text.detail_refund_content', { refund_content: orderDetail.order.refundContent }) }}</span>
            <span>{{ $t('mall_order.text.detail_refund_time', { refund_time: orderDetail.order.refundTime }) }}</span>
          </el-form-item>
          <el-form-item :label="$t('mall_order.form.detail_receipt_info')">
            <span>{{ $t('mall_order.text.detail_confirm_time', { confirm_time: orderDetail.order.confirmTime }) }}</span>
          </el-form-item>
        </el-form>
      </section>
      <span slot="footer" class="dialog-footer">
        <el-button @click="orderDialogVisible = false">{{ $t('mall_order.button.detail_cancel') }}</el-button>
        <el-button type="primary" @click="printOrder">{{ $t('mall_order.button.detail_print') }}</el-button>
      </span>
    </el-dialog>

    <!-- 收款对话框 -->
    <el-dialog :visible.sync="payDialogVisible" :title="$t('mall_order.dialog.pay')" width="40%" center>
      <el-form ref="payForm" :model="payForm" status-icon label-position="left" label-width="100px">
        <div style="margin-bottom: 10px;">
          {{ $t('mall_order.message.pay_confirm', { order_sn: payForm.orderSn }) }}
        </div>
        <el-form-item :label="$t('mall_order.form.pay_old_money')" prop="oldMoney">
          <el-input-number v-model="payForm.oldMoney" :controls="false" disabled />
        </el-form-item>
        <el-form-item :label="$t('mall_order.form.pay_new_money')" prop="newMoney">
          <el-input-number v-model="payForm.newMoney" :controls="false" />
        </el-form-item>
      </el-form>
      <el-table :data="payForm.goodsList">
        <el-table-column property="goodsName" :label="$t('mall_order.table.pay_goods_name')" />
        <el-table-column :label="$t('mall_order.table.pay_goods_specifications')">
          <template slot-scope="scope">
            {{ scope.row.specifications.join('-') }}
          </template>
        </el-table-column>
        <el-table-column property="onumber" width="100" :label="$t('mall_order.table.pay_goods_number')" />
        <!-- <el-table-column label="实际数量" width="100">
          <template slot-scope="scope">
            <el-input-number v-model="scope.row.number" :min="0" :controls="false" />
          </template>
        </el-table-column> -->
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="payDialogVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button type="primary" @click="confirmPay">{{ $t('app.button.confirm') }}</el-button>
      </div>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog :visible.sync="shipDialogVisible" :title="$t('mall_order.dialog.ship')">
      <el-form ref="shipForm" :model="shipForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item :label="$t('mall_order.form.ship_channel')" prop="shipChannel">
          <el-select v-model="shipForm.shipChannel" :placeholder="$t('mall_order.placeholder.ship_channel')">
            <el-option v-for="item in channels" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('mall_order.form.ship_sn')" prop="shipSn">
          <el-input v-model="shipForm.shipSn" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button type="primary" @click="confirmShip">{{ $t('app.button.confirm') }}</el-button>
      </div>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog :visible.sync="refundDialogVisible" :title="$t('mall_order.dialog.refund')">
      <el-form ref="refundForm" :model="refundForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item :label="$t('mall_order.form.refund_money')" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" :disabled="true" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button type="primary" @click="confirmRefund">{{ $t('app.button.confirm') }}</el-button>
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
