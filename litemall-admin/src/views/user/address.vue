<template>
  <div class="app-container calendar-list-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID" v-model="listQuery.userId">
      </el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入收货人名称" v-model="listQuery.name">
      </el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleCreate" icon="el-icon-edit">添加</el-button>
      <el-button class="filter-item" type="primary" :loading="downloadLoading" v-waves icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="地址ID" prop="id" sortable>
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="用户ID" prop="userId">
      </el-table-column>

      <el-table-column align="center" min-width="100px" label="收货人名称" prop="name">
      </el-table-column>
      
      <el-table-column align="center" min-width="100px" label="手机号码" prop="mobile">
      </el-table-column>   

      <el-table-column align="center" min-width="300px" label="地址" prop="address">
        <template slot-scope="scope">
          {{scope.row.province + scope.row.city + scope.row.area + scope.row.address}}
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="80px" label="默认" prop="isDefault">
        <template slot-scope="scope">
          {{scope.row.isDefault ? '是' : '否'}}
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
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="dataForm.userId"></el-input>
        </el-form-item>
        <el-form-item label="收货人名称" prop="name">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>
        <el-form-item label="收获人手机" prop="mobile">
          <el-input v-model="dataForm.mobile"></el-input>
        </el-form-item>
        <el-form-item label="区域地址">
          <el-select v-model="dataForm.provinceId" placeholder="省" @change="provinceChange">
            <el-option v-for="item in provinces" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
          <el-select v-model="dataForm.cityId" placeholder="市" @change="cityChange">
            <el-option v-for="item in cities" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
          <el-select v-model="dataForm.areaId" placeholder="区" @change="areaChange">
            <el-option v-for="item in areas" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="dataForm.address"></el-input>
        </el-form-item>        
        <el-form-item label="是否默认地址" prop="isDefault">
          <el-select v-model="dataForm.isDefault" placeholder="请选择">
            <el-option label="否" :value="false">
            </el-option>
            <el-option label="是" :value="true">
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

<script>
import { listAddress, createAddress, updateAddress, deleteAddress } from '@/api/address'
import { listSubRegion } from '@/api/region'
import waves from '@/directive/waves' // 水波纹指令

export default {
  name: 'UserAddress',
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
        name: undefined,
        userId: undefined,
        sort: '+id'
      },
      provinces: {},
      cities: {},
      areas: {},
      dataForm: {
        id: undefined,
        userId: undefined,
        name: undefined,
        mobile: undefined,
        address: undefined,
        isDefault: undefined,
        provinceId: undefined,
        cityId: undefined,
        areaId: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
        name: [{ required: true, message: '收货人名称不能为空', trigger: 'blur' }],
        mobile: [{ required: true, message: '收货人手机号码不能为空', trigger: 'blur' }],
        provinceId: [{ required: true, message: '收货人所在省不能为空', trigger: 'blur' }],
        cityId: [{ required: true, message: '收货人所在市不能为空', trigger: 'blur' }],
        areaId: [{ required: true, message: '收货人所在区不能为空', trigger: 'blur' }],
        address: [{ required: true, message: '收货人地址不能为空', trigger: 'blur' }]
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
    this.getProvinces()
  },
  methods: {
    getList() {
      this.listLoading = true
      listAddress(this.listQuery).then(response => {
        this.list = response.data.data.items
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    getProvinces() {
      listSubRegion({ id: 0 }).then(response => {
        this.provinces = response.data.data
      })
    },
    provinceChange(val) {
      if (val === undefined) {
        return
      }
      this.cities = {}
      this.dataForm.cityId = undefined
      this.areas = {}
      this.dataForm.areaId = undefined
      listSubRegion({ id: val }).then(response => {
        this.cities = response.data.data
      })
    },
    cityChange(val) {
      if (val === undefined) {
        return
      }
      this.areas = {}
      this.dataForm.areaId = undefined
      listSubRegion({ id: val }).then(response => {
        this.areas = response.data.data
      })
    },
    areaChange(val) {

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
        userId: undefined,
        name: undefined,
        mobile: undefined,
        address: undefined,
        isDefault: undefined,
        provinceId: undefined,
        cityId: undefined,
        areaId: undefined
      }
    },
    handleCreate() {
      this.resetForm()
      this.cities = {}
      this.areas = {}
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createAddress(this.dataForm).then(response => {
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
      this.cities = {}
      this.areas = {}
      listSubRegion({ id: this.dataForm.provinceId }).then(response => {
        this.cities = response.data.data
      })
      listSubRegion({ id: this.dataForm.cityId }).then(response => {
        this.areas = response.data.data
      })
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateAddress(this.dataForm).then(response => {
            const updatedAddress = response.data.data
            for (const v of this.list) {
              if (v.id === updatedAddress.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, updatedAddress)
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
      deleteAddress(row).then(response => {
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
        const tHeader = ['地址ID', '用户ID', '收获人', '手机号', '省', '市', '区', '地址', '是否默认']
        const filterVal = ['id', 'userId', 'name', 'mobile', 'province', 'city', 'area', 'address', 'isDefault']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户地址信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>