<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.key" clearable class="filter-item" style="width: 200px;" :placeholder="$t('sys_os.placeholder.filter_key')" />
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" :placeholder="$t('sys_os.placeholder.filter_name')" />
      <el-button v-permission="['GET /admin/storage/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button v-permission="['POST /admin/storage/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('app.button.create') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>

      <el-table-column align="center" :label="$t('sys_os.table.key')" prop="key" />

      <el-table-column align="center" :label="$t('sys_os.table.name')" prop="name" />

      <el-table-column align="center" :label="$t('sys_os.table.type')" prop="type" />

      <el-table-column align="center" :label="$t('sys_os.table.size')" prop="size" />

      <el-table-column align="center" property="url" :label="$t('sys_os.table.url')">
        <template slot-scope="scope">
          <img :src="scope.row.url" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('sys_os.table.url_link')" prop="url" />

      <el-table-column align="center" :label="$t('sys_os.table.actions')" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/storage/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('app.button.edit') }}</el-button>
          <el-button v-permission="['POST /admin/storage/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加对话框 -->
    <el-dialog :visible.sync="createDialogVisible" :title="$t('sys_os.dialog.create')">
      <el-upload ref="upload" :show-file-list="false" :limit="1" :http-request="handleUpload" action="#" list-type="picture">
        <el-button type="primary">{{ $t('sys_os.button.upload') }}</el-button>
      </el-upload>
    </el-dialog>

    <!-- 修改对话框 -->
    <el-dialog :visible.sync="updateDialogVisible" :title="$t('sys_os.dialog.update')">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item :label="$t('sys_os.form.name')" prop="name">
          <el-input v-model="dataForm.name" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button type="primary" @click="updateData">{{ $t('app.button.confirm') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listStorage, createStorage, updateStorage, deleteStorage } from '@/api/storage'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'Storage',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        key: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      createDialogVisible: false,
      dataForm: {
        id: undefined,
        name: ''
      },
      updateDialogVisible: false,
      rules: {
        name: [{ required: true, message: '对象名称不能为空', trigger: 'blur' }]
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
      listStorage(this.listQuery).then(response => {
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
        name: ''
      }
    },
    handleCreate() {
      this.createDialogVisible = true
    },
    handleUpload(item) {
      this.$refs.upload.clearFiles()

      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(response => {
        this.list.unshift(response.data.data)
        this.createDialogVisible = false
        this.$notify.success({
          title: '成功',
          message: '上传成功'
        })
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.updateDialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateStorage(this.dataForm).then(() => {
            for (const v of this.list) {
              if (v.id === this.dataForm.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, this.dataForm)
                break
              }
            }
            this.updateDialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
        }
      })
    },
    handleDelete(row) {
      deleteStorage(row).then(response => {
        this.$notify.success({
          title: '成功',
          message: '删除成功'
        })
        this.getList()
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['ID', '对象KEY', '对象名称', '对象类型', '对象大小', '访问链接']
        const filterVal = ['id', 'key', 'name', 'type', 'size', 'url']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '对象存储信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
