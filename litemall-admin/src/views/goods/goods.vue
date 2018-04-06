<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号" v-model="listQuery.goodsSn">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称" v-model="listQuery.name">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>
      <el-button class="filter-item" type="primary" :loading="downloadLoading" v-waves icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="demo-table-expand">
            <el-form-item label="首页主图">
              <span>{{ props.row.listPicUrl }}</span>
            </el-form-item>
            <el-form-item label="宣传画廊">
              <span>{{ props.row.gallery }}</span>
            </el-form-item>            
            <el-form-item label="商品介绍">
              <span>{{ props.row.goodsBrief }}</span>
            </el-form-item> 
            <el-form-item label="商品详细介绍">
              <span>{{ props.row.goodsDesc }}</span>
            </el-form-item>
            <el-form-item label="商品主图">
              <span>{{ props.row.primaryPicUrl }}</span>
            </el-form-item>
            <el-form-item label="商品单位">
              <span>{{ props.row.goodsUnit }}</span>
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

      <el-table-column align="center" width="100px" label="商品ID" prop="id" sortable>
      </el-table-column>
      
      <el-table-column align="center" min-width="100px" label="商品编号" prop="goodsSn">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="名称" prop="name">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="专柜价格" prop="counterPrice">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="当前价格" prop="retailPrice">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="是否新品" prop="isNew">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{scope.row.isNew ? '新品' : '非新品'}}</el-tag>
        </template>
      </el-table-column> 

      <el-table-column align="center" min-width="100px" label="是否热品" prop="isHot">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isHot ? 'success' : 'error' ">{{scope.row.isHot ? '热品' : '非热品'}}</el-tag>
        </template>
      </el-table-column> 

      <el-table-column align="center" min-width="100px" label="是否在售" prop="isOnSale">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{scope.row.isOnSale ? '在售' : '未售'}}</el-tag>
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

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibilityHeight="100" ></back-to-top>
    </el-tooltip>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="商品编号" prop="goodsSn">
          <el-input v-model="dataForm.goodsSn"></el-input>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>        
        <el-form-item label="专柜价格" prop="counterPrice">
          <el-input v-model="dataForm.counterPrice"></el-input>
        </el-form-item>
        <el-form-item label="当前价格" prop="retailPrice">
          <el-input v-model="dataForm.retailPrice"></el-input>
        </el-form-item>                
        <el-form-item label="是否新品" prop="isNew">
          <el-select v-model="dataForm.isNew" placeholder="请选择">
            <el-option label="新品" :value="true">
            </el-option>
            <el-option label="新品" :value="false">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否热品" prop="isHot">
          <el-select v-model="dataForm.isHot" placeholder="请选择">
            <el-option label="热品" :value="true">
            </el-option>
            <el-option label="非热品" :value="false">
            </el-option>
          </el-select>
        </el-form-item>                
        <el-form-item label="是否在售" prop="isOnSale">
          <el-select v-model="dataForm.isOnSale" placeholder="请选择">
            <el-option label="在售" :value="true">
            </el-option>
            <el-option label="未售" :value="false">
            </el-option>
          </el-select>
        </el-form-item>
            
        <el-form-item label="首页主图">
          <el-input v-model="dataForm.listPicUrl"></el-input>
        </el-form-item>
        
        <el-form-item label="宣传画廊">
          <el-input v-model="dataForm.gallery"></el-input>
        </el-form-item>            
        
        <el-form-item label="商品介绍">
          <el-input v-model="dataForm.goodsBrief"></el-input>
        </el-form-item> 
            
        <el-form-item style="width: 700px;" label="商品详细介绍">
          <tinymce v-model="dataForm.goodsDesc"></tinymce>
        </el-form-item>
        
        <el-form-item label="商品主图">
          <el-input v-model="dataForm.primaryPicUrl"></el-input>
        </el-form-item>
            
        <el-form-item label="商品单位">
          <el-input v-model="dataForm.goodsUnit"></el-input>
        </el-form-item>
            
        <el-form-item label="关键字">
          <el-input v-model="dataForm.keyword"></el-input>
        </el-form-item>
            
        <el-form-item label="类目ID">
          <el-input v-model="dataForm.categoryId"></el-input>
        </el-form-item>      
        
        <el-form-item label="品牌商ID">
          <el-input v-model="dataForm.brandId"></el-input>              
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
  .el-dialog {
    width: 800px;
  }

</style>

<script>
import { listGoods, createGoods, updateGoods, deleteGoods } from '@/api/goods'
import waves from '@/directive/waves' // 水波纹指令
import BackToTop from '@/components/BackToTop'
import Tinymce from '@/components/Tinymce'

export default {
  name: 'Goods',
  components: { BackToTop, Tinymce },
  directives: { waves },
  data() {
    return {
      list: undefined,
      total: undefined,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        goodsSn: undefined,
        name: undefined,
        sort: '+id'
      },
      dataForm: {
        id: undefined,
        goodsSn: undefined,
        name: undefined,
        counterPrice: undefined,
        retailPrice: undefined,
        isHot: false,
        isNew: true,
        isOnSale: true,
        listPicUrl: undefined,
        primaryPicUrl: undefined,
        goodsBrief: undefined,
        goodsDesc: undefined,
        keywords: undefined,
        gallery: undefined,
        categoryId: undefined,
        brandId: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        goodsSn: [{ required: true, message: '商品编号不能为空', trigger: 'blur' }],
        name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }]
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
    resetForm() {
      this.dataForm = {
        id: undefined,
        goodsSn: undefined,
        name: undefined,
        counterPrice: undefined,
        retailPrice: undefined,
        isHot: false,
        isNew: true,
        isOnSale: true,
        listPicUrl: undefined,
        primaryPicUrl: undefined,
        goodsBrief: undefined,
        goodsDesc: undefined,
        keywords: undefined,
        gallery: undefined,
        categoryId: undefined,
        brandId: undefined
      }
    },
    filterLevel(value, row) {
      return row.level === value
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
          createGoods(this.dataForm).then(response => {
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
          updateGoods(this.dataForm).then(() => {
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
        const tHeader = ['商品ID', '商品编号', '名称', '专柜价格', '当前价格', '是否新品', '是否热品', '是否在售', '首页主图', '宣传画廊', '商品介绍', '详细介绍', '商品主图', '商品单位', '关键字', '类目ID', '品牌商ID']
        const filterVal = ['id', 'goodsSn', 'name', 'counterPrice', 'retailPrice', 'isNew', 'isHot', 'isOnSale', 'listPicUrl', 'gallery', 'goodsBrief', 'goodsDesc', 'primaryPicUrl', 'goodsUnit', 'keywords', 'categoryId', 'brandId']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>