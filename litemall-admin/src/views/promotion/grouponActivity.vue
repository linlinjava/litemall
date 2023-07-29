<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.grouponRuleId" clearable class="filter-item" style="width: 200px;" :placeholder="$t('promotion_groupon_activity.placeholder.filter_groupon_rule_id')" />
      <el-button v-permission="['GET /admin/groupon/listRecord']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload"
      >{{ $t('app.button.download') }}
      </el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="scope">
          <el-table :data="scope.row.subGroupons" border style="width: 100%">
            <el-table-column align="center" :label="$t('promotion_groupon_activity.table.order_id')" prop="orderId" />
            <el-table-column align="center" :label="$t('promotion_groupon_activity.table.user_id')" prop="userId" />
          </el-table>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.groupon_order_id')" prop="groupon.orderId" />

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.groupon_user_id')" prop="groupon.userId" />

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.subgroupons_length')" prop="subGroupons.length" />

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.rules_discount')" prop="rules.discount" />

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.rules_discount_member')" prop="rules.discountMember" />

      <el-table-column align="center" property="iconUrl" :label="$t('promotion_groupon_activity.table.groupon_share_url')">
        <template slot-scope="scope">
          <img :src="scope.row.groupon.shareUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.rules_add_time')" prop="rules.addTime" />

      <el-table-column align="center" :label="$t('promotion_groupon_activity.table.rules_expire_time')" prop="rules.expireTime" />
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" :content="$t('app.tooltip.back_to_top')">
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
