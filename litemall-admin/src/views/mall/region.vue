<template>
  <div class="app-container">

    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" row-key="id" style="width: 100%;margin-bottom: 20px;" border="">

      <el-table-column label="区域名称" prop="name"/>

      <el-table-column label="区域类型" prop="type">
        <template slot-scope="scope">
          {{ scope.row.type | typeFilter }}
        </template>
      </el-table-column>

      <el-table-column label="区域编码" prop="code"/>

    </el-table>

  </div>
</template>

<script>
import { listRegion } from '@/api/region'

export default {
  name: 'Region',
  filters: {
    typeFilter(status) {
      const typeMap = {
        '1': '省',
        '2': '市',
        '3': '区'
      }
      return typeMap[status]
    }
  },
  data() {
    return {
      list: [],
      listLoading: true,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listRegion().then(response => {
        this.list = response.data.data.list
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.listLoading = false
      })
    }
  }
}
</script>
