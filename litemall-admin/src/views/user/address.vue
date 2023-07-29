<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" :placeholder="$t('user_address.placeholder.filter_user_id')"/>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" :placeholder="$t('user_address.placeholder.filter_name')"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>
      <el-table-column align="center" width="100px" :label="$t('user_address.table.id')" prop="id" sortable />

      <el-table-column align="center" min-width="100px" :label="$t('user_address.table.user_id')" prop="userId" />

      <el-table-column align="center" min-width="100px" :label="$t('user_address.table.name')" prop="name" />

      <el-table-column align="center" min-width="100px" :label="$t('user_address.table.tel')" prop="tel" />

      <el-table-column align="center" min-width="300px" :label="$t('user_address.table.address_region')">
        <template slot-scope="scope">
          {{ scope.row.province + scope.row.city + scope.row.county }}
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="300px" :label="$t('user_address.table.address_detail')" prop="addressDetail" />

      <el-table-column align="center" min-width="80px" :label="$t('user_address.table.is_default')" prop="isDefault">
        <template slot-scope="scope">
          {{ scope.row.isDefault ? '是' : '否' }}
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listAddress } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'UserAddress',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        userId: undefined,
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
      listAddress(this.listQuery).then(response => {
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
        const tHeader = ['地址ID', '用户ID', '收货人', '手机号', '省', '市', '区', '地址', '是否默认']
        const filterVal = ['id', 'userId', 'name', 'tel', 'province', 'city', 'county', 'addressDetail', 'isDefault']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户地址信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
