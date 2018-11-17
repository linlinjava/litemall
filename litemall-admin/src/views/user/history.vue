<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>
      <el-input v-model="listQuery.keyword" clearable class="filter-item" style="width: 200px;" placeholder="请输入搜索历史关键字"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" size="small" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="搜索ID" prop="id" sortable/>

      <el-table-column align="center" min-width="100px" label="用户ID" prop="userId"/>

      <el-table-column align="center" min-width="100px" label="关键字" prop="keyword"/>

      <el-table-column align="center" min-width="100px" label="添加时间" prop="addTime"/>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listHistory } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'History',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        userId: undefined,
        keyword: undefined,
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
      listHistory(this.listQuery)
        .then(response => {
          this.list = response.data.data.items
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
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['用户ID', '搜索历史关键字', '添加时间']
        const filterVal = ['userId', 'keyword', 'addTime']
        excel.export_json_to_excel2(
          tHeader,
          this.list,
          filterVal,
          '用户搜索历史信息'
        )
        this.downloadLoading = false
      })
    }
  }
}
</script>
