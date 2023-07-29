<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" :placeholder="$t('user_feedback.placeholder.filter_username')"/>
      <el-input v-model="listQuery.id" clearable class="filter-item" style="width: 200px;" :placeholder="$t('user_feedback.placeholder.filter_id')"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>

      <el-table-column align="center" :label="$t('user_feedback.table.id')" prop="id" />

      <el-table-column align="center" :label="$t('user_feedback.table.username')" prop="username" />

      <el-table-column align="center" :label="$t('user_feedback.table.mobile')" prop="mobile" />

      <el-table-column align="center" :label="$t('user_feedback.table.feed_type')" prop="feedType" />

      <el-table-column align="center" :label="$t('user_feedback.table.content')" prop="content" />

      <el-table-column align="center" :label="$t('user_feedback.table.pic_urls')" prop="picUrls">
        <template slot-scope="scope">
          <el-image v-for="item in scope.row.picUrls" :key="item" :src="item" :preview-src-list="scope.row.picUrls" :lazy="true" style="width: 40px; height: 40px; margin-right: 5px;"/>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('user_feedback.table.add_time')" prop="addTime" />

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listFeedback } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Feedback',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listFeedback(this.listQuery).then(response => {
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
        const tHeader = ['反馈ID', '用户名称', '反馈内容', '反馈图片列表', '反馈时间']
        const filterVal = ['id', 'username', 'content', 'picUrls', 'addTime']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '意见反馈信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
