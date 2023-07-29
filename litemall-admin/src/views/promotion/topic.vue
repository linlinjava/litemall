<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 200px;" :placeholder="$t('promotion_topic.placeholder.filter_title')" />
      <el-input v-model="listQuery.subtitle" clearable class="filter-item" style="width: 200px;" :placeholder="$t('promotion_topic.placeholder.filter_subtitle')" />
      <el-select v-model="listQuery.sort" class="filter-item" :placeholder="$t('promotion_topic.placeholder.filter_sort')">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button v-permission="['GET /admin/topic/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <div class="operator-container">
      <el-button v-permission="['POST /admin/topic/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('app.button.create') }}</el-button>
      <el-button v-permission="['POST /admin/topic/batch-delete']" class="filter-item" type="danger" icon="el-icon-delete" @click="handleBatchDelete">{{ $t('app.button.batch_delete') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column align="center" :label="$t('promotion_topic.table.title')" prop="title" />

      <el-table-column align="center" :label="$t('promotion_topic.table.subtitle')" min-width="200" prop="subtitle" />

      <el-table-column align="center" property="picUrl" :label="$t('promotion_topic.table.pic_url')">
        <template slot-scope="scope">
          <el-image :src="thumbnail(scope.row.picUrl)" :preview-src-list="toPreview(scope.row, scope.row.picUrl)" style="width: 40px; height: 40px" />
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_topic.table.content')" prop="content">
        <template slot-scope="scope">
          <el-dialog :visible.sync="contentDialogVisible" :title="$t('promotion_topic.dialog.content_detail')">
            <div v-html="contentDetail" />
          </el-dialog>
          <el-button type="primary" size="mini" @click="showContent(scope.row.content)">{{ $t('app.button.view') }}</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_topic.table.price')" prop="price" />

      <el-table-column align="center" :label="$t('promotion_topic.table.read_count')" prop="readCount" />

      <el-table-column align="center" :label="$t('promotion_topic.table.actions')" min-width="100" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/topic/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('app.button.edit') }}</el-button>
          <el-button v-permission="['POST /admin/topic/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" :content="$t('app.tooltip.back_to_top')">
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
import { listTopic, deleteTopic, batchDeleteTopic } from '@/api/topic'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import _ from 'lodash'
import { thumbnail, toPreview } from '@/utils/index'

export default {
  name: 'Topic',
  components: { BackToTop, Pagination },
  data() {
    return {
      thumbnail,
      toPreview,
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
      options: [{
        value: 'id',
        label: '按序号排序'
      }, {
        value: 'add_time',
        label: '按时间排序'
      }, {
        value: 'price',
        label: '按价格排序'
      }],
      multipleSelection: [],
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
          this.getList()
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    showContent(content) {
      this.contentDetail = content
      this.contentDialogVisible = true
    },
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message.error('请选择至少一条记录')
        return
      }
      const ids = []
      _.forEach(this.multipleSelection, function(item) {
        ids.push(item.id)
      })
      batchDeleteTopic({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量删除专题成功'
          })
          this.getList()
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
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
