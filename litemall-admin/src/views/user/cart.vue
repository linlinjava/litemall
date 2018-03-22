<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID" v-model="listQuery.userId">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品ID" v-model="listQuery.goodsId">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>
      <el-button class="filter-item" type="primary" :loading="downloadLoading" v-waves icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="购物车ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="用户ID" prop="userId">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="商品ID" prop="goodsId">
      </el-table-column>
        
      <el-table-column align="center" min-width="100px" label="商品编码" prop="goodsSn">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="商品名称" prop="goodsName">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="商品图片" prop="picUrl">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="货品ID" prop="productId">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="货品价格" prop="retailPrice">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="货品数量" prop="number">
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
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="dataForm.userId"></el-input>
        </el-form-item>
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="dataForm.goodsId"></el-input>
        </el-form-item>
        <el-form-item label="商品编码" prop="goodsId">
          <el-input v-model="dataForm.goodsId"></el-input>
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

<script>
import { listCart, createCart, updateCart, deleteCart } from '@/api/cart'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'FootPrint',
  directives: {
    waves
  },
  data() {
    return {
      list: null,
      total: null,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        userId: undefined,
        goodsId: undefined,
        sort: '+id'
      },
      dataForm: {
        id: undefined,
        userId: '',
        goodsId: '',
        productId: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
        goodsId: [{ required: true, message: '商品ID不能为空', trigger: 'blur' }],
        productId: [{ required: true, message: '货品ID不能为空', trigger: 'blur' }]
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
      listCart(this.listQuery).then(response => {
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
        userId: '',
        goodsId: '',
        productId: ''
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
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createCart(this.dataForm).then(response => {
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
          updateCart(this.dataForm).then(() => {
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
      deleteCart(row).then(response => {
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
        const tHeader = ['购物车ID', '用户ID', '商品ID', '商品名称', '商品编号', '商品图片', '货品ID', '货品价格', '货品数量']
        const filterVal = ['id', 'userId', 'goodsId', 'goodsName', 'goodsSn', 'pic_url', 'productId', 'retailPrice', 'number']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户购物车信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>