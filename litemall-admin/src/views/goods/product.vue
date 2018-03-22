<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品ID" v-model="listQuery.goodsId">
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

      <el-table-column align="center" width="100px" label="货品ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="商品ID" prop="goodsId">
      </el-table-column>

      <el-table-column align="center" min-width="150px" label="商品规格ID列表" prop="goodsSpecificationIds">
        <template slot-scope="scope">
          {{ scope.row.goodsSpecificationIds.join(',') }}
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="货品数量" prop="goodsNumber">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="货品价格" prop="retailPrice">
      </el-table-column>

      <el-table-column align="center" min-width="200px" label="货品图片" prop="url">
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

    <el-dialog title="添加货品" :visible.sync="createDialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="dataForm.goodsId"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="createData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 修改对话框 -->
    <el-dialog title="修改货品" :visible.sync="editDialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="dataForm.goodsId" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="商品规格ID列表" prop="goodsSpecificationIds">
          <el-input v-model="dataForm.goodsSpecificationIds" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="货品数量" prop="goodsNumber">
          <el-input v-model="dataForm.goodsNumber"></el-input>
        </el-form-item>  
        <el-form-item label="货品价格" prop="retailPrice"> 
          <el-input v-model="dataForm.retailPrice"></el-input>
        </el-form-item>          
        <el-form-item label="货品图片" prop="url">
          <el-input v-model="dataForm.url"></el-input>          
          <el-upload action="#" list-type="picture" :show-file-list="false" :limit="1" :http-request="uploadUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>         
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取消</el-button>
\        <el-button type="primary" @click="updateData">确定</el-button>
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
import { listProduct, createProduct, updateProduct, deleteProduct } from '@/api/product'
import { createStorage } from '@/api/storage'

import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'Product',
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
        goodsId: undefined,
        sort: '+id'
      },
      createDialogFormVisible: false,
      editDialogFormVisible: false,
      dataForm: {
        id: undefined,
        goodsId: undefined,
        goodsSpecificationIds: undefined,
        goodsNumber: 0,
        retailPrice: 0,
        url: undefined
      },
      rules: {
        goodsId: [{ required: true, message: '商品ID不能为空', trigger: 'blur' }],
        goodsSpecificationIds: [{ required: true, message: '商品规格ID列表不能为空', trigger: 'blur' }]
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
      listProduct(this.listQuery).then(response => {
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
        goodsId: undefined,
        goodsSpecificationIds: undefined,
        goodsNumber: 0,
        retailPrice: 0,
        url: undefined
      }
    },
    uploadUrl(item) {
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(res => {
        this.dataForm.url = res.data.data.url
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    handleCreate() {
      this.resetForm()
      this.createDialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createProduct(this.dataForm).then(response => {
            this.getList()
            this.createDialogFormVisible = false
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
      this.editDialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateProduct(this.dataForm).then(() => {
            for (const v of this.list) {
              if (v.id === this.dataForm.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, this.dataForm)
                break
              }
            }
            this.editDialogFormVisible = false
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
      deleteProduct(row).then(response => {
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
        const tHeader = ['货品ID', '商品ID', '商品规格ID列表', '货品数量', '货品价格', '货品图片']
        const filterVal = ['id', 'goodsId', 'goodsSpecificationIds', 'goodsNumber', 'retailPrice', 'url']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '货品信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
