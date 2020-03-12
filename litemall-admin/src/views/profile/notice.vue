<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 200px;" placeholder="请输入标题关键字" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <div class="operator-container">
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleBatchRead">批量已读</el-button>
      <el-button class="filter-item" type="danger" icon="el-icon-delete" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <el-tabs v-model="listQuery.type" @tab-click="handleFilter">
      <el-tab-pane label="未读通知" name="unread" />
      <el-tab-pane label="已读通知" name="read" />
      <el-tab-pane label="所有通知" name="all" />
    </el-tabs>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" fit highlight-current-row @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column align="center" label="通知标题" prop="noticeTitle" />

      <el-table-column align="center" label="通知时间" prop="addTime" width="180" />

      <el-table-column align="center" label="通知状态" prop="readTime" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.readTime ? 'success' : 'error' ">{{ scope.row.readTime ? '已读' : '未读' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleRead(scope.row)">阅读</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-dialog :title="notice.title" :visible.sync="noticeVisible" center>
      <el-divider content-position="left">{{ notice.admin }} 于 {{ notice.time }} 通知如下内容：</el-divider>
      <div v-html="notice.content" />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="afterRead">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotice, catNotice, bcatNotice, rmNotice, brmNotice } from '@/api/profile'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import _ from 'lodash'

export default {
  name: 'AdminNotice',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        title: '',
        type: 'unread',
        sort: 'add_time',
        order: 'desc'
      },
      multipleSelection: [],
      notice: {
        title: '',
        source: '',
        content: '',
        addTime: ''
      },
      noticeVisible: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listNotice(this.listQuery)
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
      rmNotice(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除通知成功'
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
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })
      brmNotice({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量删除通知成功'
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
    handleRead(row) {
      catNotice(row)
        .then(response => {
          this.notice = response.data.data
          this.noticeVisible = true
        })
    },
    afterRead() {
      this.noticeVisible = false
      this.getList()
    },
    handleBatchRead() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })
      bcatNotice({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量设置通知已读成功'
          })
          this.getList()
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
