<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 200px;" placeholder="请输入专题标题"/>
      <el-input v-model="listQuery.subtitle" clearable class="filter-item" style="width: 200px;" placeholder="请输入专题子标题"/>
      <el-button v-permission="['GET /admin/topic/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/topic/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="专题标题" prop="title"/>

      <el-table-column align="center" label="专题子标题" min-width="200" prop="subtitle"/>

      <el-table-column align="center" property="picUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="80">
        </template>
      </el-table-column>

      <el-table-column align="center" label="专题详情" prop="content">
        <template slot-scope="scope">
          <el-dialog :visible.sync="contentDialogVisible" title="专题详情">
            <div v-html="contentDetail"/>
          </el-dialog>
          <el-button type="primary" size="mini" @click="showContent(scope.row.content)">查看</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" label="底价" prop="price"/>

      <el-table-column align="center" label="阅读数量" prop="readCount"/>

      <el-table-column align="center" label="操作" min-width="100" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/topic/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/topic/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<style>
.el-dialog {
  width: 800px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 145px;
  height: 145px;
  display: block;
}
</style>

<script>
import { listTopic, deleteTopic } from '@/api/topic'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Topic',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        title: undefined,
        subtitle: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      contentDetail: '',
      contentDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listTopic(this.listQuery)
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
    handleCreate() {
      this.$router.push({ path: '/promotion/topic-create' })
    },
    handleUpdate(row) {
      this.$router.push({ path: '/promotion/topic-edit', query: { id: row.id }})
    },
    handleDelete(row) {
      deleteTopic(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除专题成功'
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
    showContent(content) {
      this.contentDetail = content
      this.contentDialogVisible = true
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '专题ID',
          '专题标题',
          '专题子标题',
          '专题内容',
          '专题图片',
          '商品低价',
          '阅读量',
          '专题商品'
        ]
        const filterVal = [
          'id',
          'title',
          'subtitle',
          'content',
          'picUrl',
          'price',
          'readCount',
          'goods'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '专题信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
