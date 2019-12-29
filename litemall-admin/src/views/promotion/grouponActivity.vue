<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.grouponRuleId" clearable class="filter-item" style="width: 200px;" placeholder="请输入团购规则ID" />
      <el-button v-permission="['GET /admin/groupon/listRecord']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload"
      >导出
      </el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="scope">
          <el-table :data="scope.row.subGroupons" border style="width: 100%">
            <el-table-column align="center" label="订单ID" prop="orderId" />
            <el-table-column align="center" label="用户ID" prop="userId" />
          </el-table>
        </template>
      </el-table-column>

      <el-table-column align="center" label="订单ID" prop="groupon.orderId" />

      <el-table-column align="center" label="用户ID" prop="groupon.userId" />

      <el-table-column align="center" label="参与人数" prop="subGroupons.length" />

      <el-table-column align="center" label="团购折扣" prop="rules.discount" />

      <el-table-column align="center" label="团购要求" prop="rules.discountMember" />

      <el-table-column align="center" property="iconUrl" label="分享图片">
        <template slot-scope="scope">
          <img :src="scope.row.groupon.shareUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" label="开始时间" prop="rules.addTime" />

      <el-table-column align="center" label="结束时间" prop="rules.expireTime" />
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<style>
  .table-expand {
    font-size: 0;
  }

  .table-expand label {
    width: 100px;
    color: #99a9bf;
  }

  .table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }

  .gallery {
    width: 80px;
    margin-right: 10px;
  }
</style>

<script>
import { listRecord } from '@/api/groupon'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'GrouponActivity',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        grouponRuleId: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      goodsDetail: '',
      detailDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listRecord(this.listQuery).then(response => {
        this.list = response.data.data.list
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
    handleDownload() {
      this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['商品ID', '名称', '首页主图', '折扣', '人数要求', '活动开始时间', '活动结束时间']
          const filterVal = ['id', 'name', 'pic_url', 'discount', 'discountMember', 'addTime', 'expireTime']
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
          this.downloadLoading = false
        })
    }
  }
}
</script>
