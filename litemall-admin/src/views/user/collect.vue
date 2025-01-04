<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" :placeholder="$t('user_collect.placeholder.filter_user_id')"/>
      <el-input v-model="listQuery.valueId" clearable class="filter-item" style="width: 200px;" :placeholder="$t('user_collect.placeholder.filter_value_id')"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>
      <el-table-column align="center" width="100px" :label="$t('user_collect.table.id')" prop="id" sortable />

      <el-table-column align="center" min-width="100px" :label="$t('user_collect.table.user_id')" prop="userId" />

      <el-table-column align="center" min-width="100px" :label="$t('user_collect.table.value_id')" prop="valueId" />

      <el-table-column align="center" min-width="100px" :label="$t('user_collect.table.add_time')" prop="addTime" />

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listCollect } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Collect',
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
        valueId: undefined,
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
      listCollect(this.listQuery).then(response => {
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
    resetForm() {
      this.dataForm = {
        id: undefined,
        userId: '',
        valueId: '',
        addTime: undefined
      }
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['用户ID', '商品ID', '添加时间']
        const filterVal = ['userId', 'valueId', 'addTime']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户收藏信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
