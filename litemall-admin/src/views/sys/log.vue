<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入操作管理员"/>
      <el-button v-permission="['GET /admin/log/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="操作管理员" prop="admin"/>
      <el-table-column align="center" label="IP地址" prop="ip"/>
      <el-table-column align="center" label="操作时间" prop="addTime"/>
      <el-table-column align="center" label="操作类别" prop="type">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.type | typeFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作动作" prop="action"/>
      <el-table-column align="center" label="操作状态" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status ? 'success' : 'error' ">{{ scope.row.status ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作结果" prop="result"/>
      <el-table-column align="center" label="备注信息" prop="comment"/>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listLog } from '@/api/log'
import Pagination from '@/components/Pagination'

const typeMap = {
  0: '一般操作',
  1: '安全操作',
  2: '订单操作',
  3: '其他操作'
}

export default {
  name: 'Log',
  components: { Pagination },
  filters: {
    typeFilter(type) {
      return typeMap[type]
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      rules: {
        name: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listLog(this.listQuery)
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
    }
  }
}
</script>
