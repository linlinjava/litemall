<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入品牌商ID" v-model="listQuery.id">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入品牌商名称" v-model="listQuery.name">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-download" @click="handleDownload" :loading="downloadLoading">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
        </template>
      </el-table-column>

      <el-table-column align="center" width="150px" label="品牌商ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="品牌商名称" prop="name">
      </el-table-column>

      <el-table-column align="center" min-width="300px" label="介绍" prop="simpleDesc">
      </el-table-column>

      <el-table-column align="center" min-width="50px" label="底价" prop="floorPrice">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="是否显示" prop="isShow">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isShow ? 'success' : 'error' ">{{scope.row.isShow ? '可显示' : '不显示'}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="是否新上" prop="isNew">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{scope.row.isShow ? '是' : '否'}}</el-tag>
        </template>
      </el-table-column>       

      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="品牌商名称" prop="name">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>
        <el-form-item label="介绍" prop="simpleDesc">
          <el-input v-model="dataForm.simpleDesc"></el-input>
        </el-form-item>
        <el-form-item label="品牌商图片" prop="picUrl">
          <el-input v-model="dataForm.picUrl"></el-input>
          <el-upload action="#" list-type="picture" :show-file-list="false" :limit="1" :http-request="uploadPicUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>  
        <el-form-item label="宣传图片" prop="listPicUrl"> 
          <el-input v-model="dataForm.listPicUrl"></el-input>
          <el-upload action="#" list-type="picture" :show-file-list="false" :limit="1" :http-request="uploadListPicUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>          
        <el-form-item label="APP宣传图片" prop="appListPicUrl">
          <el-input v-model="dataForm.appListPicUrl"></el-input>          
          <el-upload action="#" list-type="picture" :show-file-list="false" :limit="1" :http-request="uploadAppListPicUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>         
        <el-form-item label="底价" prop="floorPrice">
          <el-input v-model="dataForm.floorPrice"></el-input>
        </el-form-item>
        <el-form-item label="是否显示" prop="isShow">
          <el-select v-model="dataForm.isShow" placeholder="请选择">
            <el-option label="显示" :value="true">
            </el-option>
            <el-option label="不显示" :value="false">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否新上" prop="isNew">
          <el-select v-model="dataForm.isNew" placeholder="请选择">
            <el-option label="新上" :value="true">
            </el-option>
            <el-option label="不是新上" :value="false">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="新上宣传图片" prop="newPicUrl" v-if="dataForm.isNew === 'true'">
          <el-input v-model="dataForm.newPicUrl"></el-input>          
          <el-upload action="#" list-type="picture" :show-file-list="false" :limit="1" :http-request="uploadNewPicUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 200px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }
</style>

<script>
import { listBrand, createBrand, updateBrand, deleteBrand } from '@/api/brand'
import { createStorage } from '@/api/storage'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'Brand',
  directives: {
    waves
  },
  data() {
    return {
      list: undefined,
      total: undefined,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        sort: '+id'
      },
      dataForm: {
        id: undefined,
        name: '',
        simpleDesc: '',
        floorPrice: undefined,
        picUrl: undefined,
        listPicUrl: undefined,
        appListPicUrl: undefined,
        isShow: undefined,
        isNew: undefined,
        newPicUrl: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [{ required: true, message: '类目名称不能为空', trigger: 'blur' }]
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
      listBrand(this.listQuery).then(response => {
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
        name: '',
        simpleDesc: '',
        floorPrice: undefined,
        picUrl: undefined,
        listPicUrl: undefined,
        appListPicUrl: undefined,
        isShow: undefined,
        isNew: undefined,
        newPicUrl: undefined
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
    uploadPicUrl(item) {
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(res => {
        this.dataForm.picUrl = res.data.data.url
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    uploadListPicUrl(item) {
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(res => {
        this.dataForm.listPicUrl = res.data.data.url
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    uploadAppListPicUrl(item) {
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(res => {
        this.dataForm.appListPicUrl = res.data.data.url
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    uploadNewPicUrl(item) {
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(res => {
        this.dataForm.newPicUrl = res.data.data.url
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createBrand(this.dataForm).then(response => {
            this.list.unshift(response.data.data)
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
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
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateBrand(this.dataForm).then(() => {
            for (const v of this.list) {
              if (v.id === this.dataForm.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, this.dataForm)
                break
              }
            }
            this.dialogFormVisible = false
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
      deleteBrand(row).then(response => {
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
        const tHeader = ['品牌商ID', '品牌商名称', '介绍', '低价', '是否显示', '品牌商图片', '宣传图片', 'APP宣传图片', '是否新上', '新上宣传图片']
        const filterVal = ['id', 'name', 'simpleDesc', 'floorPrice', 'isShow', 'picUrl', 'listPicUrl', 'appListPicUrl', 'isNew', 'newPicUrl']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '品牌商信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
