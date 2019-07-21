<template>
  <div class="app-container">

    <div class="table-layout">
      <el-row>
        <el-col :span="4" class="table-cell-title">名称</el-col>
        <el-col :span="4" class="table-cell-title">介绍名称</el-col>
        <el-col :span="4" class="table-cell-title">标签</el-col>
        <el-col :span="4" class="table-cell-title">优惠券类型</el-col>
        <el-col :span="4" class="table-cell-title">最低消费</el-col>
        <el-col :span="4" class="table-cell-title">优惠面值</el-col>
      </el-row>
      <el-row>
        <el-col :span="4" class="table-cell">{{ coupon.name }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.desc }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.tag }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.type | formatType }}</el-col>
        <el-col :span="4" class="table-cell">满{{ coupon.min }}元可用</el-col>
        <el-col :span="4" class="table-cell">减免{{ coupon.discount }}元</el-col>
      </el-row>
      <el-row>
        <el-col :span="4" class="table-cell-title">每人限额</el-col>
        <el-col :span="4" class="table-cell-title">当前状态</el-col>
        <el-col :span="4" class="table-cell-title">商品范围</el-col>
        <el-col :span="4" class="table-cell-title">有效期</el-col>
        <el-col :span="4" class="table-cell-title">优惠兑换码</el-col>
        <el-col :span="4" class="table-cell-title">发行数量</el-col>
      </el-row>
      <el-row>
        <el-col :span="4" class="table-cell">{{ coupon.limit }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.status | formatStatus }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.goodsType | formatGoodsType }}</el-col>
        <el-col :span="4" class="table-cell">{{ getTimeScope() }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.code }}</el-col>
        <el-col :span="4" class="table-cell">{{ coupon.total === 0 ? "不限" : coupon.total }}</el-col>
      </el-row>
    </div>

    <!-- 查询操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>
      <el-select v-model="listQuery.status" clearable style="width: 200px" class="filter-item" placeholder="请选择使用状态">
        <el-option v-for="type in useStatusOptions" :key="type.value" :label="type.label" :value="type.value"/>
      </el-select>
      <el-button v-permission="['GET /admin/coupon/listuser']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="用户优惠券ID" prop="id" sortable/>

      <el-table-column align="center" label="用户ID" prop="userId"/>

      <el-table-column align="center" label="领取时间" prop="addTime"/>

      <el-table-column align="center" label="使用状态" prop="status">
        <template slot-scope="scope">{{ scope.row.status | formatUseStatus }}</template>
      </el-table-column>

      <el-table-column align="center" label="订单ID" prop="orderId"/>

      <el-table-column align="center" label="使用时间" prop="usedTime"/>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { readCoupon, listCouponUser } from '@/api/coupon'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

const defaultTypeOptions = [
  {
    label: '通用领券',
    value: 0
  },
  {
    label: '注册赠券',
    value: 1
  },
  {
    label: '兑换码',
    value: 2
  }
]

const defaultUseStatusOptions = [
  {
    label: '未使用',
    value: 0
  },
  {
    label: '已使用',
    value: 1
  },
  {
    label: '已过期',
    value: 2
  },
  {
    label: '已下架',
    value: 3
  }
]

export default {
  name: 'CouponDetail',
  components: { Pagination },
  filters: {
    formatType(type) {
      for (let i = 0; i < defaultTypeOptions.length; i++) {
        if (type === defaultTypeOptions[i].value) {
          return defaultTypeOptions[i].label
        }
      }
      return ''
    },
    formatGoodsType(goodsType) {
      if (goodsType === 0) {
        return '全场通用'
      } else if (goodsType === 1) {
        return '指定分类'
      } else {
        return '指定商品'
      }
    },
    formatStatus(status) {
      if (status === 0) {
        return '正常'
      } else if (status === 1) {
        return '已过期'
      } else {
        return '已下架'
      }
    },
    formatUseStatus(status) {
      if (status === 0) {
        return '未使用'
      } else if (status === 1) {
        return '已使用'
      } else if (status === 3) {
        return '已过期'
      } else {
        return '已下架'
      }
    }
  },
  data() {
    return {
      typeOptions: Object.assign({}, defaultTypeOptions),
      useStatusOptions: Object.assign({}, defaultUseStatusOptions),
      coupon: {},
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        couponId: 0,
        userId: undefined,
        status: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      if (this.$route.query.id == null) {
        return
      }
      readCoupon(this.$route.query.id).then(response => {
        this.coupon = response.data.data
      })
      this.listQuery.couponId = this.$route.query.id
      this.getList()
    },
    getList() {
      this.listLoading = true
      listCouponUser(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    getTimeScope() {
      if (this.coupon.timeType === 0) {
        return '领取' + this.coupon.days + '天有效'
      } else if (this.coupon.timeType === 1) {
        return '自' + this.coupon.startTime + '至' + this.coupon.endTime + '有效'
      } else {
        return '未知'
      }
    },
    getGoodsScope() {
    }
  }
}
</script>
<style scoped>
  .filter-container {
    margin-top: 20px;
  }

  .table-layout {
    margin-top: 20px;
    border-left: 1px solid #DCDFE6;
    border-top: 1px solid #DCDFE6;
  }
  .table-cell {
    height: 60px;
    line-height: 40px;
    border-right: 1px solid #DCDFE6;
    border-bottom: 1px solid #DCDFE6;
    padding: 10px;
    font-size: 14px;
    color: #606266;
    text-align: center;
    overflow: hidden;
  }
  .table-cell-title {
    border-right: 1px solid #DCDFE6;
    border-bottom: 1px solid #DCDFE6;
    padding: 10px;
    background: #F2F6FC;
    text-align: center;
    font-size: 14px;
    color: #303133;
  }
</style>
