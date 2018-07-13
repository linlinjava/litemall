<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入对象KEY" v-model="listQuery.key">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入对象名称" v-model="listQuery.name">
      </el-input>
      <el-button class="filter-item" type="primary"  icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>
      <el-button class="filter-item" type="primary" :loading="downloadLoading" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="对象KEY" prop="key">
      </el-table-column>

      <el-table-column align="center" label="对象名称" prop="name">
      </el-table-column>
      
      <el-table-column align="center" label="对象类型" prop="type">
      </el-table-column>          

      <el-table-column align="center" label="对象大小" prop="size">
      </el-table-column>   

      <el-table-column align="center" property="url" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.url" width="40"/>
        </template>
      </el-table-column>

      <el-table-column align="center" label="图片链接" prop="url">
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

    <!-- 添加对话框 -->
    <el-dialog title="上传对象" :visible.sync="createDialogVisible">
      <el-upload action="#" list-type="picture" :show-file-list="false" :limit="1" :http-request="handleUpload">
        <el-button size="small" type="primary">点击上传</el-button>
      </el-upload>
    </el-dialog>

    <!-- 修改对话框 -->
    <el-dialog title="修改对象名称" :visible.sync="updateDialogVisible">
      <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="对象名称" prop="name">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listStorage, createStorage, updateStorage, deleteStorage } from '@/api/storage'

export default {
  name: 'Storage',
  data() {
    return {
      list: null,
      total: null,
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
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(response => {
        this.list.unshift(response.data.data)
        this.createDialogVisible = false
        this.$notify({
          title: '成功',
          message: '创建成功',
          type: 'success',
          duration: 2000
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
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row) {
      deleteStorage(row).then(response => {
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
        const tHeader = ['ID', '对象KEY', '对象名称', '对象类型', '对象大小', '访问链接']
        const filterVal = ['id', 'key', 'name', 'type', 'size', 'url']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '对象存储信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
