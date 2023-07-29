<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.title" clearable class="filter-item" style="width: 200px;" :placeholder="$t('sys_notice.placeholder.filter_title')" />
      <el-input v-model="listQuery.content" clearable class="filter-item" style="width: 200px;" :placeholder="$t('sys_notice.placeholder.filter_content')" />
      <el-button v-permission="['GET /admin/notice/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <div class="operator-container">
      <el-button v-permission="['POST /admin/notice/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('app.button.create') }}</el-button>
      <el-button v-permission="['GET /admin/notice/batch-delete']" class="filter-item" type="danger" icon="el-icon-delete" @click="handleBatchDelete">{{ $t('app.button.batch_delete') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />

      <el-table-column align="center" :label="$t('sys_notice.table.title')" prop="title" />

      <el-table-column align="center" :label="$t('sys_notice.table.content')" prop="content">
        <template slot-scope="scope">
          <el-dialog :visible.sync="contentDialogVisible" :title="$t('sys_notice.dialog.content_detail')">
            <div v-html="contentDetail" />
          </el-dialog>
          <el-button type="primary" size="mini" @click="showContent(scope.row.content)">{{ $t('app.button.view') }}</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('sys_notice.table.add_time')" prop="addTime" />

      <el-table-column align="center" :label="$t('sys_notice.table.admin_id')" prop="adminId" />

      <el-table-column align="center" :label="$t('sys_notice.table.actions')" min-width="100" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/notice/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('app.button.edit') }}</el-button>
          <el-button v-permission="['POST /admin/notice/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px">
        <el-form-item :label="$t('sys_notice.form.title')" prop="title">
          <el-input v-model="dataForm.title" />
        </el-form-item>
        <el-form-item :label="$t('sys_notice.form.content')" prop="content">
          <editor v-model="dataForm.content" :init="editorInit" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{ $t('app.button.confirm') }}</el-button>
        <el-button v-else type="primary" @click="updateData">{{ $t('app.button.confirm') }}</el-button>
      </div>
    </el-dialog>

    <el-tooltip placement="top" :content="$t('app.tooltip.back_to_top')">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<script>
import { listNotice, createNotice, updateNotice, deleteNotice, batchDeleteNotice } from '@/api/notice'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import _ from 'lodash'
import Editor from '@tinymce/tinymce-vue'
import { createStorage } from '@/api/storage'
import { getToken } from '@/utils/auth'

export default {
  name: 'Notice',
  components: { BackToTop, Pagination, Editor },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        title: undefined,
        content: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      multipleSelection: [],
      contentDetail: '',
      contentDialogVisible: false,
      dataForm: {
        id: undefined,
        title: undefined,
        content: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [
          { required: true, message: '通知标题不能为空', trigger: 'blur' }
        ]
      },
      editorInit: {
        language: 'zh_CN',
        height: 200,
        convert_urls: false,
        plugins: ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
        toolbar: ['searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample', 'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData).then(res => {
            success(res.data.data.url)
          }).catch(() => {
            failure('上传失败，请重新上传')
          })
        }
      },
      downloadLoading: false
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listNotice(this.listQuery)
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
    resetForm() {
      this.dataForm = {
        id: undefined,
        title: undefined,
        content: undefined
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createNotice(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '创建成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateNotice(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新广告成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleDelete(row) {
      deleteNotice(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除通知成功'
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
      batchDeleteNotice({ ids: ids })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '批量删除通知成功'
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
          '通知ID',
          '通知标题',
          '管理员ID',
          '添加时间',
          '更新时间'
        ]
        const filterVal = [
          'id',
          'title',
          'content',
          'adminId',
          'addTime',
          'updateTime'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '通知')
        this.downloadLoading = false
      })
    }
  }
}
</script>
