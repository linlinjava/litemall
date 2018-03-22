<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入行政区域名称" v-model="listQuery.name">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入行政区域编码" v-model="listQuery.code">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-download" @click="handleDownload" :loading="downloadLoading">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" width="100px" label="区域ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="区域父ID" prop="pid">
      </el-table-column>

      <el-table-column align="center" min-width="200px" label="区域名称" prop="name">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="区域类型" prop="type">
        <template slot-scope="scope">
          {{scope.row.type | typeFilter }}
        </template>
      </el-table-column>  

      <el-table-column align="center" min-width="100px" label="区域编码" prop="code">
      </el-table-column>  

    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page"
        :page-sizes="[10,20,30,50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import { listRegion } from '@/api/region'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'Keyword',
  directives: {
    waves
  },
  data() {
    return {
      list: undefined,
      total: undefined,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        code: undefined,
        sort: '+id'
      },
      downloadLoading: false
    }
  },
  filters: {
    typeFilter(status) {
      const typeMap = {
        '1': '省',
        '2': '市',
        '3': '区',
        '4': '街道'
      }
      return typeMap[status]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listRegion(this.listQuery).then(response => {
        this.list = response.data.data.items
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
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['区域ID', '区域父ID', '区域名称', '区域类型', '区域编码']
        const filterVal = ['id', 'pid', 'name', 'type', 'code']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '行政区域信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>