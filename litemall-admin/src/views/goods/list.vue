<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号" v-model="listQuery.goodsSn">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称" v-model="listQuery.name">
      </el-input>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>
      <el-button class="filter-item" type="primary" :loading="downloadLoading" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="table-expand">
            <el-form-item label="宣传画廊">
              <img class="gallery" v-for="pic in props.row.gallery" :key="pic" :src="pic"/>
            </el-form-item>
            <el-form-item label="商品介绍">
              <span>{{ props.row.brief }}</span>
            </el-form-item>
            <el-form-item label="商品单位">
              <span>{{ props.row.unit }}</span>
            </el-form-item>
            <el-form-item label="关键字">
              <span>{{ props.row.keyword }}</span>
            </el-form-item>
            <el-form-item label="类目ID">
              <span>{{ props.row.categoryId }}</span>
            </el-form-item>
            <el-form-item label="品牌商ID">
              <span>{{ props.row.brandId }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column align="center" label="商品编号" prop="goodsSn">
      </el-table-column>

      <el-table-column align="center" min-width="100" label="名称" prop="name">
      </el-table-column>

      <el-table-column align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="40"/>
        </template>
      </el-table-column>

      <el-table-column align="center" property="iconUrl" label="分享图">
        <template slot-scope="scope">
          <img :src="scope.row.shareUrl" width="40"/>
        </template>
      </el-table-column>

      <el-table-column align="center" label="详情" prop="detail">
        <template slot-scope="scope">
          <el-dialog title="商品详情" :visible.sync="detailDialogVisible">
            <div v-html="goodsDetail"></div>
          </el-dialog>
          <el-button type="primary" size="mini" @click="showDetail(scope.row.detail)">查看</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" label="专柜价格" prop="counterPrice">
      </el-table-column>

      <el-table-column align="center" label="当前价格" prop="retailPrice">
      </el-table-column>

      <el-table-column align="center" label="是否新品" prop="isNew">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{scope.row.isNew ? '新品' : '非新品'}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否热品" prop="isHot">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isHot ? 'success' : 'error' ">{{scope.row.isHot ? '热品' : '非热品'}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="是否在售" prop="isOnSale">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{scope.row.isOnSale ? '在售' : '未售'}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini"  @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page"
        :page-sizes="[10,20,30,50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibilityHeight="100" ></back-to-top>
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
import { listGoods, deleteGoods } from '@/api/goods'
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
        goodsSn: undefined,
        name: undefined,
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
      listGoods(this.listQuery).then(response => {
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
    handleCreate() {
      this.$router.push({ path: '/goods/create' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/goods/edit', query: { id: row.id }})
    },
    showDetail(detail) {
      this.goodsDetail = detail
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      deleteGoods(row).then(response => {
        this.$notify({
          title: '成功',
          message: '删除成功',
          type: 'success',
          duration: 2000
        })
        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['商品ID', '商品编号', '名称', '专柜价格', '当前价格', '是否新品', '是否热品', '是否在售', '首页主图', '宣传图片列表', '商品介绍', '详细介绍', '商品图片', '商品单位', '关键字', '类目ID', '品牌商ID']
        const filterVal = ['id', 'goodsSn', 'name', 'counterPrice', 'retailPrice', 'isNew', 'isHot', 'isOnSale', 'listPicUrl', 'gallery', 'brief', 'detail', 'picUrl', 'goodsUnit', 'keywords', 'categoryId', 'brandId']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
