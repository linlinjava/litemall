<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.aftersaleSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入服务编号" />
      <el-input v-model="listQuery.orderId" clearable class="filter-item" style="width: 200px;" placeholder="请输入订单ID" />
      <el-button v-permission="['GET /admin/aftersale/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <div class="operator-container">
      <el-button v-permission="['GET /admin/aftersale/batch-delete']" class="filter-item" type="danger" icon="el-icon-delete" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column align="center" label="服务编号" prop="aftersaleSn" />
      <el-table-column align="center" label="订单ID" prop="orderId" />
      <el-table-column align="center" label="用户ID" prop="userId" />
      <el-table-column align="center" label="售后类型" prop="type">
        <template slot-scope="scope">
          <el-tag :type="typeTag[scope.row.type]">{{ typeDesc[scope.row.type] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="退款原因" prop="reason" />
      <el-table-column align="center" label="退款价格" prop="amount" />
      <el-table-column align="center" label="申请时间" prop="addTime" />

      <el-table-column align="center" label="操作" min-width="100" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleRead(scope.row)">详情</el-button>
          <el-button v-permission="['POST /admin/aftersale/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<script>
import { listAftersale, deleteAftersale, batchDeleteAftersale } from '@/api/aftersale'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import _ from 'lodash'

export default {
  name: 'Topic',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        aftersaleSn: undefined,
        orderId: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      typeTag: [
        '',
        'success',
        'warning'
      ],
      typeDesc: [
        '未收货退款',
        '不退货退款',
        '退货退款'
      ],
      multipleSelection: [],
      contentDetail: '',
      contentDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listAftersale(this.listQuery)
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
    handleDelete(row) {
      deleteAftersale(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除专题成功'
          })
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    showContent(content) {
      this.contentDetail = content
      this.contentDialogVisible = true
    },
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })
      batchDeleteAftersale({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量删除售后成功'
          })
          this.getList()
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '服务编号',
          '订单ID',
          '用户ID',
          '售后类型',
          '退款原因',
          '退款价格',
          '申请时间'
        ]
        const filterVal = [
          'aftersaleSn',
          'orderId',
          'userId',
          'type',
          'reason',
          'amount',
          'addTime'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '售后信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
