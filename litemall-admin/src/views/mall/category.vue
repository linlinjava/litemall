<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入类目ID" v-model="listQuery.id">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入类目名称" v-model="listQuery.name">
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
            <el-form-item label="首页页面类目图标">
       				<img :src="props.row.iconUrl">
            </el-form-item>
            <el-form-item label="首页页面类目横幅图片">
       				<img :src="props.row.bannerUrl">
            </el-form-item>            
            <el-form-item label="类目页标题">
              <span>{{ props.row.frontName }}</span>
            </el-form-item>
            <el-form-item label="类目页介绍">
              <span>{{ props.row.frontDesc }}</span>
            </el-form-item>
            <el-form-item label="类目页横幅">
       				<img :src="props.row.wapBannerUrl">
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column align="center" width="100px" label="类目ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="名称" prop="name">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="关键字" prop="keyword">
      </el-table-column>
      
      <el-table-column align="center" min-width="100px" label="级别" prop="level"
        :filters="[{ text: '一级类目', value: 'L1' }, { text: '二级类目', value: 'L2' }]" :filter-method="filterLevel">
        <template slot-scope="scope">
          <el-tag :type="scope.row.level === 'L1' ? 'primary' : 'info' ">{{scope.row.level === 'L1' ? '一级类目' : '二级类目'}}</el-tag>
        </template>
      </el-table-column>   

      <el-table-column align="center" min-width="100px" label="父类目ID" prop="parentId">
      </el-table-column> 

      <el-table-column align="center" min-width="100px" label="是否显示" prop="isShow">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isShow ? 'success' : 'error' ">{{scope.row.isShow ? '可显示' : '不显示'}}</el-tag>
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
        <el-form-item label="类目名称" prop="name">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>
        <el-form-item label="类目关键字" prop="keyword">
          <el-input v-model="dataForm.keyword"></el-input>
        </el-form-item>
        <el-form-item label="类目级别" prop="level">
          <el-select v-model="dataForm.level" placeholder="请选择">
            <el-option label="一级类目" value="L1">
            </el-option>
            <el-option label="二级类目" value="L2">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="父类目" prop="parentId" v-if="dataForm.level === 'L2'">
          <el-select v-model="dataForm.parentId" placeholder="请选择">
            <el-option v-for="(key, val) in catL1" :key="key" :label="key" :value="val">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标" prop="iconUrl">
          <el-input v-model="dataForm.iconUrl"></el-input>
          <el-upload action="http://localhost:8080/storage/create" :show-file-list="false" :on-success="handleIconUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="首页横幅" prop="bannerUrl">
          <el-input v-model="dataForm.bannerUrl"></el-input>
          <el-upload action="http://localhost:8080/storage/create" :show-file-list="false" :on-success="handleBannerUrl">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
          <el-form-item label="类目页标题" prop="frontName">
          <el-input v-model="dataForm.frontName"></el-input>
        </el-form-item>
        <el-form-item label="类目页介绍" prop="frontDesc">
          <el-input v-model="dataForm.frontDesc"></el-input>
        </el-form-item>
        <el-form-item label="类目页横幅" prop="wapBannerUrl">
          <el-input v-model="dataForm.wapBannerUrl"></el-input>
          <el-upload action="http://localhost:8080/storage/create" :show-file-list="false" :on-success="handleWapBannerUrl">
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
import { listCategory, listCatL1, createCategory, updateCategory, deleteCategory } from '@/api/category'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'Category',
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
      catL1: {},
      dataForm: {
        id: undefined,
        name: '',
        keyword: '',
        level: 'L1',
        parentId: '',
        isShow: 'true',
        frontName: '',
        frontDesc: '',
        iconUrl: undefined,
        bannerUrl: undefined,
        wapBannerUrl: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [{ required: true, message: '类目名称不能为空', trigger: 'blur' }],
        keyword: [{ required: true, message: '类目关键字不能为空', trigger: 'blur' }]
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
    this.getCatL1()
  },
  methods: {
    getList() {
      this.listLoading = true
      listCategory(this.listQuery).then(response => {
        this.list = response.data.data.items
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    getCatL1() {
      listCatL1().then(response => {
        this.catL1 = response.data.data
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
        keyword: '',
        level: 'L1',
        parentId: '',
        isShow: 'true',
        frontName: '',
        frontDesc: '',
        iconUrl: undefined,
        bannerUrl: undefined,
        wapBannerUrl: undefined
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
    handleIconUrl(response, file, fileList) {
      if (response.errno === 0) {
        this.dataForm.iconUrl = response.data.url
      }
    },
    handleBannerUrl(response, file, fileList) {
      if (response.errno === 0) {
        this.dataForm.bannerUrl = response.data.url
      }
    },
    handleWapBannerUrl(response, file, fileList) {
      if (response.errno === 0) {
        this.dataForm.wapBannerUrl = response.data.url
      }
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createCategory(this.dataForm).then(response => {
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
          updateCategory(this.dataForm).then(() => {
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
      deleteCategory(row).then(response => {
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
        const tHeader = ['类目ID', '名称', '关键字', '级别', '父类目ID', '是否显示', '图标', '首页横幅', '类目页标题', '类目页介绍', '类目页横幅']
        const filterVal = ['id', 'name', 'keyword', 'level', 'parentId', 'isShow', 'iconUrl', 'bannerUrl', 'frontName', 'frontDesc', 'wapBannerUrl']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品类目信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
