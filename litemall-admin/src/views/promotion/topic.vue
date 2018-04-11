<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入专题标题" v-model="listQuery.title">
      </el-input>
      <el-input clearable  class="filter-item" style="width: 200px;" placeholder="请输入专题子标题" v-model="listQuery.subtitle">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-download" @click="handleDownload" :loading="downloadLoading">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" width="100px" label="专题ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="专题标题" prop="title">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="专题子内容" prop="subtitle">
      </el-table-column>

      <el-table-column align="center" min-width="400px" label="内容" prop="content">
      </el-table-column>

      <el-table-column align="center" min-width="80px" label="底价" prop="priceInfo">
      </el-table-column>

      <el-table-column align="center" min-width="80px" label="阅读数量" prop="readCount">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="是否显示" prop="isShow">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isShow ? 'success' : 'error' ">{{scope.row.isShow ? '显示' : '不显示'}}</el-tag>
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
        <el-form-item label="专题标题" prop="title">
          <el-input v-model="dataForm.title"></el-input>
        </el-form-item>
        <el-form-item label="专题子标题" prop="subtitle">
          <el-input v-model="dataForm.subtitle"></el-input>
        </el-form-item>
        <el-form-item style="width: 700px;" label="专题内容">
          <tinymce v-model="dataForm.content"></tinymce>
        </el-form-item>        
        <el-form-item label="商品低价" prop="priceInfo">
          <el-input v-model="dataForm.priceInfo"></el-input>
        </el-form-item>
        <el-form-item label="阅读量" prop="readCount">
          <el-input v-model="dataForm.readCount"></el-input>
        </el-form-item>
        <el-form-item label="是否显示" prop="isShow">
          <el-select v-model="dataForm.isShow" placeholder="请选择">
            <el-option label="显示" :value="true">
            </el-option>
            <el-option label="不显示" :value="false">
            </el-option>
          </el-select>
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
import { listTopic, createTopic, updateTopic, deleteTopic } from '@/api/topic'
import { createStorage } from '@/api/storage'
import waves from '@/directive/waves' // 水波纹指令
import BackToTop from '@/components/BackToTop'
import Tinymce from '@/components/Tinymce'

export default {
  name: 'Topic',
  components: { BackToTop, Tinymce },
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
        title: undefined,
        subtitle: undefined,
        sort: '+id'
      },
      dataForm: {
        id: undefined,
        titile: undefined,
        subtitle: undefined,
        content: undefined,
        priceInfo: undefined,
        readCount: undefined,
        isShow: false
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        title: [{ required: true, message: '专题标题不能为空', trigger: 'blur' }],
        subtitle: [{ required: true, message: '专题子标题不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '专题内容不能为空', trigger: 'blur' }]
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
      listTopic(this.listQuery).then(response => {
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
        titile: undefined,
        subtitle: undefined,
        content: undefined,
        priceInfo: undefined,
        readCount: undefined,
        isShow: false
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
    uploadUrl(item) {
      const formData = new FormData()
      formData.append('file', item.file)
      createStorage(formData).then(res => {
        this.dataForm.url = res.data.data.url
      }).catch(() => {
        this.$message.error('上传失败，请重新上传')
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createTopic(this.dataForm).then(response => {
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
          updateTopic(this.dataForm).then(() => {
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
      deleteTopic(row).then(response => {
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
        const tHeader = ['专题ID', '专题标题', '专题子标题', '专题内容', '商品低价', '阅读量', '是否显示']
        const filterVal = ['id', 'title', 'subtitle', 'content', 'priceInfo', 'readCount', 'isShow']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '专题信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>