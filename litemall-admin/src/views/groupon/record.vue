<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号" v-model="listQuery.goodsId">
      </el-input>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <!--<el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>-->
      <el-button class="filter-item" type="primary" :loading="downloadLoading" icon="el-icon-download"
                 @click="handleDownload">导出
      </el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit
              highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="scope">
          <el-table :data="scope.row.subGroupons" border style="width: 100%">
            <el-table-column align="center" label="订单ID" prop="orderId">
            </el-table-column>
            <el-table-column align="center" label="用户ID" prop="userId">
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>

      <el-table-column align="center" label="订单ID" prop="groupon.orderId">
      </el-table-column>

      <el-table-column align="center" label="用户ID" prop="groupon.userId">
      </el-table-column>

      <el-table-column align="center" label="参与人数" prop="subGroupons.length">
      </el-table-column>

      <el-table-column align="center" label="团购折扣" prop="rules.discount">
      </el-table-column>

      <el-table-column align="center" label="团购要求" prop="rules.discountMember">
      </el-table-column>

      <el-table-column align="center" property="iconUrl" label="分享图片">
        <template slot-scope="scope">
          <img :src="scope.row.groupon.shareUrl" width="40"/>
        </template>
      </el-table-column>

      <el-table-column align="center" label="开始时间" prop="rules.addTime">
      </el-table-column>

      <el-table-column align="center" label="结束时间" prop="rules.expireTime">
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                     :current-page="listQuery.page"
                     :page-sizes="[10,20,30,50]" :page-size="listQuery.limit"
                     layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibilityHeight="100"></back-to-top>
    </el-tooltip>

  </div>
</template>

<style>
  .table-expand {
    font-size: 0;
  }

  .table-expand label {
    width: 100px;
    color: #99a9bf;
  }

  .table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }

  .gallery {
    width: 80px;
    margin-right: 10px;
  }
</style>

<script>
  import { listRecord } from '@/api/groupon'
  import BackToTop from '@/components/BackToTop'

  export default {
    name: 'GoodsList',
    components: { BackToTop },
    data() {
      return {
        list: [],
        total: 0,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20,
          goodsId: undefined,
          sort: 'add_time',
          order: 'desc'
        },
        goodsDetail: '',
        detailDialogVisible: false,
        downloadLoading: false
      }
    },
    created() {
      this.getList()
    },
    methods: {
      getList() {
        this.listLoading = true
        listRecord(this.listQuery).then(response => {
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
          const tHeader = ['商品ID', '名称', '首页主图', '折扣', '人数要求', '活动开始时间', '活动结束时间']
          const filterVal = ['id', 'name', 'pic_url', 'discount', 'discountMember', 'addTime', 'expireTime']
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
          this.downloadLoading = false
        })
      }
    }
  }
</script>
