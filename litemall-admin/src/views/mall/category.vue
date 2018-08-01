<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入类目ID" v-model="listQuery.id">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入类目名称" v-model="listQuery.name">
      </el-input>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>
      <el-button class="filter-item" type="primary" :loading="downloadLoading" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" label="类目ID" prop="id">
      </el-table-column>

      <el-table-column align="center" label="类目名" prop="name">
      </el-table-column>

      <el-table-column align="center" property="iconUrl" label="类目图标">
        <template slot-scope="scope">
          <img :src="scope.row.iconUrl" width="40" v-if="scope.row.iconUrl"/>
        </template>
      </el-table-column>

      <el-table-column align="center" property="picUrl" label="类目图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="80" v-if="scope.row.picUrl"/>
        </template>
      </el-table-column>

      <el-table-column align="center" label="关键字" prop="keyword">
      </el-table-column>

      <el-table-column align="center" min-width="100" label="简介" prop="desc">
      </el-table-column>

      <el-table-column align="center" label="级别" prop="level"
        :filters="[{ text: '一级类目', value: 'L1' }, { text: '二级类目', value: 'L2' }]" :filter-method="filterLevel">
        <template slot-scope="scope">
          <el-tag :type="scope.row.level === 'L1' ? 'primary' : 'info' ">{{scope.row.level === 'L1' ? '一级类目' : '二级类目'}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="父类目ID" prop="pid">
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="dataForm" status-icon label-position="left" label-width="100px" style='width: 400px; margin-left:50px;'>
        <el-form-item label="类目名称" prop="name">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>
        <el-form-item label="关键字" prop="keyword">
          <el-input v-model="dataForm.keyword"></el-input>
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select v-model="dataForm.level" @change="onLevelChange">
            <el-option label="一级类目" value="L1">
            </el-option>
            <el-option label="二级类目" value="L2">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="父类目" prop="pid" v-if="dataForm.level === 'L2'">
          <el-select v-model="dataForm.pid">
            <el-option v-for="item in catL1" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类目图标" prop="iconUrl">
          <el-upload class="avatar-uploader" :headers="headers" :action='uploadPath' list-type="picture-card" :show-file-list="false" accept=".jpg,.jpeg,.png,.gif" :on-success="uploadIconUrl">
			      <img v-if="dataForm.iconUrl" :src="dataForm.iconUrl" class="avatar">
						<i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="类目图片" prop="picUrl">
          <el-upload class="avatar-uploader" :headers="headers" :action='uploadPath' list-type="picture-card" :show-file-list="false" accept=".jpg,.jpeg,.png,.gif" :on-success="uploadPicUrl">
			      <img v-if="dataForm.picUrl" :src="dataForm.picUrl" class="avatar">
						<i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="类目简介" prop="desc">
          <el-input v-model="dataForm.desc"></el-input>
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
	    width: 120px;
	    height: 120px;
	    display: block;
	}
</style>

<script>
import { listCategory, listCatL1, createCategory, updateCategory, deleteCategory } from '@/api/category'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'

export default {
  name: 'Category',
  computed: {
    headers() {
      return {
        'Admin-Token': getToken()
      }
    }
  },
  data() {
    return {
      uploadPath,
      list: undefined,
      total: undefined,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      catL1: {},
      dataForm: {
        id: undefined,
        name: '',
        keyword: '',
        level: 'L2',
        pid: undefined,
        desc: '',
        iconUrl: undefined,
        picUrl: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [{ required: true, message: '类目名不能为空', trigger: 'blur' }]
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
        level: 'L2',
        pid: undefined,
        desc: '',
        iconUrl: undefined,
        picUrl: undefined
      }
    },
    filterLevel: function(value, row) {
      return row.level === value
    },
    onLevelChange: function(value) {
      if (value === 'L1') {
        this.pid = undefined
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
    uploadIconUrl: function(response) {
      this.dataForm.iconUrl = response.data.url
    },
    uploadPicUrl: function(response) {
      this.dataForm.picUrl = response.data.url
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
        const tHeader = ['类目ID', '名称', '关键字', '级别', '父类目ID', '类目图标', '类目图片', '简介']
        const filterVal = ['id', 'name', 'keyword', 'level', 'pid', 'iconUrl', 'picUrl', 'desc']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品类目信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
