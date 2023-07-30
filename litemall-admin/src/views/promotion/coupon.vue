<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" :placeholder="$t('promotion_coupon.placeholder.filter_name')" />
      <el-select v-model="listQuery.type" clearable style="width: 200px" class="filter-item" :placeholder="$t('promotion_coupon.placeholder.filter_type')">
        <el-option v-for="type in typeOptions" :key="type.value" :label="type.label" :value="type.value" />
      </el-select>
      <el-select v-model="listQuery.status" clearable style="width: 200px" class="filter-item" :placeholder="$t('promotion_coupon.placeholder.filter_status')">
        <el-option v-for="type in statusOptions" :key="type.value" :label="type.label" :value="type.value" />
      </el-select>
      <el-button v-permission="['GET /admin/coupon/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button v-permission="['POST /admin/coupon/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('app.button.create') }}</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">{{ $t('app.button.download') }}</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>

      <el-table-column align="center" :label="$t('promotion_coupon.table.id')" prop="id" sortable />

      <el-table-column align="center" :label="$t('promotion_coupon.table.name')" prop="name" />

      <el-table-column align="center" :label="$t('promotion_coupon.table.desc')" prop="desc" />

      <el-table-column align="center" :label="$t('promotion_coupon.table.tag')" prop="tag" />

      <el-table-column align="center" :label="$t('promotion_coupon.table.min')" prop="min">
        <template slot-scope="scope">{{ $t('promotion_coupon.text.coupon_min', { min: scope.row.min }) }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.discount')" prop="discount">
        <template slot-scope="scope">{{ $t('promotion_coupon.text.coupon_discount', { discount: scope.row.discount }) }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.limit')" prop="limit">
        <template slot-scope="scope">{{ scope.row.limit != 0 ? scope.row.limit : $t('promotion_coupon.text.unlimited') }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.goods_type')" prop="goodsType">
        <template slot-scope="scope">{{ scope.row.goodsType | formatGoodsType }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.type')" prop="type">
        <template slot-scope="scope">{{ scope.row.type | formatType }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.total')" prop="total">
        <template slot-scope="scope">{{ scope.row.total != 0 ? scope.row.total : $t('promotion_coupon.text.unlimited') }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.status')" prop="status">
        <template slot-scope="scope">{{ scope.row.status | formatStatus }}</template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_coupon.table.actions')" width="300" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/coupon/read']" type="primary" size="mini" @click="handleDetail(scope.row)">{{ $t('app.button.detail') }}</el-button>
          <el-button v-permission="['POST /admin/coupon/update']" type="info" size="mini" @click="handleUpdate(scope.row)">{{ $t('app.button.edit') }}</el-button>
          <el-button v-permission="['POST /admin/coupon/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item :label="$t('promotion_coupon.form.name')" prop="name">
          <el-input v-model="dataForm.name" />
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.desc')" prop="desc">
          <el-input v-model="dataForm.desc" />
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.tag')" prop="tag">
          <el-input v-model="dataForm.tag" />
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.min')" prop="min">
          <el-input v-model="dataForm.min">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.discount')" prop="discount">
          <el-input v-model="dataForm.discount">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.limit')" prop="limit">
          <el-input v-model="dataForm.limit">
            <template slot="append">{{ $t('promotion_coupon.text.units') }}</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.type')" prop="type">
          <el-select v-model="dataForm.type">
            <el-option
              v-for="type in typeOptions"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.total')" prop="total">
          <el-input v-model="dataForm.total">
            <template slot="append">{{ $t('promotion_coupon.text.units') }}</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.time_type')">
          <el-radio-group v-model="dataForm.timeType">
            <el-radio-button :label="0">{{ $t('promotion_coupon.value.time_type_0') }}</el-radio-button>
            <el-radio-button :label="1">{{ $t('promotion_coupon.value.time_type_1') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="dataForm.timeType === 0">
          <el-input v-model="dataForm.days">
            <template slot="append">{{ $t('promotion_coupon.text.days') }}</template>
          </el-input>
        </el-form-item>
        <el-form-item v-show="dataForm.timeType === 1">
          <el-col :span="11">
            <el-date-picker v-model="dataForm.startTime" type="datetime" :placeholder="$t('promotion_coupon.placeholder.start_time')" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%;" />
          </el-col>
          <el-col :span="2" class="line">{{ $t('promotion_coupon.text.to_time') }}</el-col>
          <el-col :span="11">
            <el-date-picker v-model="dataForm.endTime" type="datetime" :placeholder="$t('promotion_coupon.placeholder.end_time')" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%;" />
          </el-col>
        </el-form-item>
        <el-form-item :label="$t('promotion_coupon.form.goods_type')">
          <el-radio-group v-model="dataForm.goodsType">
            <el-radio-button :label="0">{{ $t('promotion_coupon.value.goods_type_0') }}</el-radio-button>
            <el-radio-button :label="1">{{ $t('promotion_coupon.value.goods_type_1') }}</el-radio-button>
            <el-radio-button :label="2">{{ $t('promotion_coupon.value.goods_type_2') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="dataForm.goodsType === 1">
          <el-cascader
            v-model="selectGoodsCategory"
            clearable
            :placeholder="$t('promotion_coupon.placeholder.category')"
            :options="goodsCategoryOptions"
          />
          <el-button @click="handleAddGoodsCategory()">{{ $t('app.button.create') }}</el-button>
          <el-table
            ref="goodsCateRelationTable"
            :data="couponCategoryList"
            style="width: 100%;margin-top: 20px"
            border
          >
            <el-table-column :label="$t('promotion_coupon.table.category_name')" align="center">
              <template slot-scope="scope">{{ scope.row.parentCategoryName }}>{{ scope.row.goodsCategoryName }}</template>
            </el-table-column>
            <el-table-column :label="$t('promotion_coupon.table.category_actions')" align="center" width="100">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  @click="handleDeleteGoodsCategory(scope.$index, scope.row)"
                >{{ $t('app.button.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item v-show="dataForm.goodsType === 2">
          <el-select
            v-model="selectGoods"
            filterable
            remote
            reserve-keyword
            :placeholder="$t('promotion_coupon.placeholder.goods')"
          >
            <el-option
              v-for="item in goodsOptions"
              :key="item.goodsId"
              :label="item.goodsName"
              :value="item.goodsId"
            >
              <span style="float: left">{{ item.goodsName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">NO.{{ item.goodsSn }}</span>
            </el-option>
          </el-select>
          <el-button @click="handleAddGoods()">{{ $t('app.button.create') }}</el-button>
          <el-table
            ref="goodsRelationTable"
            :data="couponGoodsList"
            style="width: 100%;margin-top: 20px"
            border
          >
            <el-table-column :label="$t('promotion_coupon.table.goods_name')" align="center">
              <template slot-scope="scope">{{ scope.row.goodsName }}</template>
            </el-table-column>
            <el-table-column :label="$t('promotion_coupon.table.goods_sn')" align="center" width="80">
              <template slot-scope="scope">{{ scope.row.goodsSn }}</template>
            </el-table-column>
            <el-table-column :label="$t('promotion_coupon.table.goods_actions')" align="center" width="60">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  @click="handleDeleteGoods(scope.$index, scope.row)"
                >{{ $t('app.button.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{ $t('app.button.confirm') }}</el-button>
        <el-button v-else type="primary" @click="updateData">{{ $t('app.button.confirm') }}</el-button>
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
import { listCoupon, createCoupon, updateCoupon, deleteCoupon } from '@/api/coupon'
import { listCategory } from '@/api/category.js'
import { listGoods } from '@/api/goods.js'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

const defaultTypeOptions = [
  {
    label: '通用领券',
    value: 0
  },
  {
    label: '注册赠券',
    value: 1
  },
  {
    label: '兑换码',
    value: 2
  }
]

const defaultStatusOptions = [
  {
    label: '正常',
    value: 0
  },
  {
    label: '已过期',
    value: 1
  },
  {
    label: '已下架',
    value: 2
  }
]

export default {
  name: 'Coupon',
  components: { Pagination },
  filters: {
    formatType(type) {
      for (let i = 0; i < defaultTypeOptions.length; i++) {
        if (type === defaultTypeOptions[i].value) {
          return defaultTypeOptions[i].label
        }
      }
      return ''
    },
    formatGoodsType(goodsType) {
      if (goodsType === 0) {
        return '全场通用'
      } else if (goodsType === 1) {
        return '指定分类'
      } else {
        return '指定商品'
      }
    },
    formatStatus(status) {
      if (status === 0) {
        return '正常'
      } else if (status === 1) {
        return '已过期'
      } else {
        return '已下架'
      }
    }
  },
  data() {
    return {
      typeOptions: Object.assign({}, defaultTypeOptions),
      statusOptions: Object.assign({}, defaultStatusOptions),
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        type: undefined,
        status: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      dataForm: {
        id: undefined,
        name: undefined,
        desc: undefined,
        tag: undefined,
        total: 0,
        discount: 0,
        min: 0,
        limit: 1,
        type: 0,
        status: 0,
        goodsType: 0,
        goodsValue: [],
        timeType: 0,
        days: 0,
        startTime: null,
        endTime: null
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        name: [
          { required: true, message: '优惠券标题不能为空', trigger: 'blur' }
        ]
      },
      downloadLoading: false,
      selectGoods: null,
      goodsOptions: [],
      selectGoodsCategory: null,
      goodsCategoryOptions: [],
      couponGoodsList: [],
      couponCategoryList: []
    }
  },
  created() {
    this.getList()
    this.getCategoryList()
    this.getGoodsList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listCoupon(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
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
        name: undefined,
        desc: undefined,
        tag: undefined,
        total: 0,
        discount: 0,
        min: 0,
        limit: 1,
        type: 0,
        status: 0,
        goodsType: 0,
        goodsValue: [],
        timeType: 0,
        days: 0,
        startTime: null,
        endTime: null
      }
      this.couponCategoryList = []
      this.couponGoodsList = []
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
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          if (this.dataForm.goodsType === 1) {
            this.dataForm.goodsValue = this.couponCategoryList.map(item => (item.goodsCategoryId))
          }
          if (this.dataForm.goodsType === 2) {
            this.dataForm.goodsValue = this.couponGoodsList.map(item => (item.goodsId))
          }
          createCoupon(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '创建优惠券成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      if (this.dataForm.goodsType === 1) {
        this.couponCategoryList = []
        for (let i = 0, len = row.goodsValue.length; i < len; i++) {
          this.couponCategoryList.push(this.getGoodsCategoryById(row.goodsValue[i]))
        }
      }
      if (this.dataForm.goodsType === 2) {
        this.couponGoodsList = []
        for (let i = 0, len = row.goodsValue.length; i < len; i++) {
          this.couponGoodsList.push(this.getGoodsById(row.goodsValue[i]))
        }
      }
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        if (this.dataForm.days === 0) {
          this.dataForm.daysType = 1
        } else {
          this.dataForm.daysType = 0
        }
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          if (this.dataForm.goodsType === 1) {
            this.dataForm.goodsValue = this.couponCategoryList.map(item => (item.goodsCategoryId))
          }
          if (this.dataForm.goodsType === 2) {
            this.dataForm.goodsValue = this.couponGoodsList.map(item => (item.goodsId))
          }
          updateCoupon(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新优惠券成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleDelete(row) {
      deleteCoupon(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除优惠券成功'
          })
          this.getList()
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handleDetail(row) {
      this.$router.push({ path: '/promotion/couponDetail', query: { id: row.id }})
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '优惠券ID',
          '名称',
          '内容',
          '标签',
          '最低消费',
          '减免金额',
          '每人限领',
          '优惠券数量'
        ]
        const filterVal = [
          'id',
          'name',
          'desc',
          'tag',
          'min',
          'discount',
          'limit',
          'total'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '优惠券信息')
        this.downloadLoading = false
      })
    },
    getGoodsList() {
      listGoods({ limit: 0 }).then(response => {
        const goodsList = response.data.data.list
        this.goodsOptions = []
        for (let i = 0; i < goodsList.length; i++) {
          const item = goodsList[i]
          this.goodsOptions.push({ goodsId: item.id, goodsName: item.name, goodsSn: item.goodsSn })
        }
      }).catch(() => {
        this.goodsOptions = []
      })
    },
    handleAddGoods() {
      if (this.selectGoods === null) {
        this.$message({
          message: '请先选择商品',
          type: 'warning'
        })
        return
      }
      this.couponGoodsList.push(this.getGoodsById(this.selectGoods))
      this.selectGoods = null
    },
    handleDeleteGoods(index, row) {
      this.couponGoodsList.splice(index, 1)
    },
    handleAddGoodsCategory() {
      if (this.selectGoodsCategory === null || this.selectGoodsCategory.length === 0) {
        this.$message({
          message: '请先选择商品分类',
          type: 'warning'
        })
        return
      }
      this.couponCategoryList.push(this.getGoodsCategoryByIds(this.selectGoodsCategory))
      this.selectGoodsCategory = []
    },
    handleDeleteGoodsCategory(index, row) {
      this.couponCategoryList.splice(index, 1)
    },
    getGoodsById(id) {
      for (let i = 0; i < this.goodsOptions.length; i++) {
        if (id === this.goodsOptions[i].goodsId) {
          return this.goodsOptions[i]
        }
      }
      return null
    },
    getCategoryList() {
      listCategory().then(response => {
        const list = response.data.data.list
        this.goodsCategoryOptions = []
        for (let i = 0; i < list.length; i++) {
          const children = []
          if (list[i].children != null && list[i].children.length > 0) {
            for (let j = 0; j < list[i].children.length; j++) {
              children.push({ label: list[i].children[j].name, value: list[i].children[j].id })
            }
          }
          this.goodsCategoryOptions.push({ label: list[i].name, value: list[i].id, children: children })
        }
      })
    },
    getGoodsCategoryById(id) {
      let name
      let parentName
      for (let i = 0; i < this.goodsCategoryOptions.length; i++) {
        for (let j = 0; j < this.goodsCategoryOptions[i].children.length; j++) {
          if (this.goodsCategoryOptions[i].children[j].value === id) {
            parentName = this.goodsCategoryOptions[i].label
            name = this.goodsCategoryOptions[i].children[j].label
          }
        }
      }
      return { goodsCategoryId: id, goodsCategoryName: name, parentCategoryName: parentName }
    },
    getGoodsCategoryByIds(ids) {
      let name
      let parentName
      for (let i = 0; i < this.goodsCategoryOptions.length; i++) {
        if (this.goodsCategoryOptions[i].value === ids[0]) {
          parentName = this.goodsCategoryOptions[i].label
          for (let j = 0; j < this.goodsCategoryOptions[i].children.length; j++) {
            if (this.goodsCategoryOptions[i].children[j].value === ids[1]) {
              name = this.goodsCategoryOptions[i].children[j].label
            }
          }
        }
      }
      return { goodsCategoryId: ids[1], goodsCategoryName: name, parentCategoryName: parentName }
    }
  }
}
</script>
