<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.aftersaleSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入售后编号" />
      <el-input v-model="listQuery.orderId" clearable class="filter-item" style="width: 200px;" placeholder="请输入订单ID" />
      <el-button v-permission="['GET /admin/aftersale/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <div class="operator-container">
      <el-button v-permission="['GET /admin/aftersale/batch-recept']" class="filter-item" type="success" icon="el-icon-info" @click="handleBatchRecept">批量通过</el-button>
      <el-button v-permission="['GET /admin/aftersale/batch-reject']" class="filter-item" type="danger" icon="el-icon-delete" @click="handleBatchReject">批量拒绝</el-button>
    </div>

    <el-tabs v-model="tab" @tab-click="handleClick">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待审核" name="uncheck" />
      <el-tab-pane label="待退款" name="unrefund" />
    </el-tabs>

    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" fit highlight-current-row @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column align="center" label="售后编号" prop="aftersaleSn" />
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

      <el-table-column align="center" label="操作" min-width="120" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/aftersale/detail']" type="primary" size="mini" @click="handleRead(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" v-permission="['POST /admin/aftersale/recept']" type="success" size="mini" @click="handleRecept(scope.row)">通过</el-button>
          <el-button v-if="scope.row.status === 1" v-permission="['POST /admin/aftersale/reject']" type="danger" size="mini" @click="handleReject(scope.row)">拒绝</el-button>
          <el-button v-if="scope.row.status === 2" v-permission="['POST /admin/aftersale/refund']" type="warning" size="mini" @click="handleRefund(scope.row)">退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>
    <!-- 详情对话框 -->
    <el-dialog :visible.sync="aftersaleDialogVisible" title="售后详情" width="800">
      <section ref="print">
        <el-form :data="aftersaleDetail" label-position="left">
          <el-form-item label="售后id">
            <el-tag>{{ aftersaleDetail.id }}</el-tag>
          </el-form-item>
          <el-form-item label="售后编号">
            <el-tag>{{ aftersaleDetail.aftersaleSn }}</el-tag>
          </el-form-item>
          <el-form-item label="订单号">
            <el-tag>{{ aftersaleDetail.orderId }}</el-tag>
          </el-form-item>
          <el-form-item label="订单金额">
            <el-tag>{{ aftersaleDetail.amount }}</el-tag>
          </el-form-item>
          <el-form-item label="订单状态">
            <el-tag v-if="aftersaleDetail.status === 1">已申请,待审核</el-tag>
            <el-tag v-if="aftersaleDetail.status === 2">审核通过,待退款</el-tag>
            <el-tag v-if="aftersaleDetail.status === 3">退款成功</el-tag>
            <el-tag v-if="aftersaleDetail.status === 4">审核不通过,已拒绝</el-tag>
          </el-form-item>
          <el-form-item label="订单用户id">
            <el-tag>{{ aftersaleDetail.userId }}</el-tag>
          </el-form-item>
          <el-form-item label="售后类型">
            <el-tag v-if="aftersaleDetail.type === 0">未收货退款</el-tag>
            <el-tag v-if="aftersaleDetail.type === 1">不退货退款</el-tag>
            <el-tag v-if="aftersaleDetail.type === 2">退货退款</el-tag>
          </el-form-item>
          <el-form-item label="退款原因">
            <span>{{ aftersaleDetail.reason }}</span>
          </el-form-item>
          <el-form-item label="申请时间">
            <span>{{ aftersaleDetail.addTime }}</span>
          </el-form-item>
          <el-form-item label="更新时间">
            <span>{{ aftersaleDetail.updateTime }}</span>
          </el-form-item>
          <el-form-item label="处理时间">
            <span>{{ aftersaleDetail.handleTime }}</span>
          </el-form-item>
          <el-form-item label="售后图片">
            <el-table :data="aftersaleDetail.pictures" border fit highlight-current-row>
              <el-table-column align="center" label="售后图片">
                <template slot-scope="scope">
                  <a :href="scope.row" target="_blank">
                    <img :src="scope.row" width="40">
                  </a>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-form>
      </section>
      <span slot="footer" class="dialog-footer">
        <el-button @click="aftersaleDialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listAftersale, receptAftersale, batchReceptAftersale, rejectAftersale, batchRejectAftersale, refundAftersale } from '@/api/aftersale'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import _ from 'lodash'

export default {
  name: 'Aftersale',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      tab: 'all',
      listQuery: {
        page: 1,
        limit: 20,
        aftersaleSn: undefined,
        orderId: undefined,
        status: '',
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
      downloadLoading: false,
      aftersaleDialogVisible:false,
      aftersaleDetail:{}
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
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleClick() {
      if (this.tab === 'all') {
        this.listQuery.status = ''
      } else if (this.tab === 'uncheck') {
        this.listQuery.status = '1'
      } else if (this.tab === 'unrefund') {
        this.listQuery.status = '2'
      }
      this.getList()
    },
    handleRecept(row) {
      receptAftersale(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '审核通过操作成功'
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
    handleBatchRecept() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })
      batchReceptAftersale({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量通过操作成功'
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
    handleReject(row) {
      rejectAftersale(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '审核拒绝操作成功'
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
    handleBatchReject() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })
      batchRejectAftersale({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量拒绝操作成功'
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
    handleRefund(row) {
      refundAftersale(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '退款操作成功'
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
          '售后编号',
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
    },
    handleRead(row) {
      this.aftersaleDetail = row;
      console.log(this.aftersaleDetail);
      this.aftersaleDialogVisible = true
    },
  }
}
</script>
