<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.goodsId" clearable class="filter-item" style="width: 200px;" :placeholder="$t('promotion_groupon_rule.placeholder.filter_goods_id')" />
      <el-button v-permission="['GET /admin/groupon/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">{{ $t('app.button.search') }}</el-button>
      <el-button v-permission="['POST /admin/groupon/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">{{ $t('app.button.create') }}</el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload"
      >{{ $t('app.button.download') }}
      </el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" :element-loading-text="$t('app.message.list_loading')" border fit highlight-current-row>
      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.id')" prop="id" />

      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.goods_id')" prop="goodsId" />

      <el-table-column align="center" min-width="100" :label="$t('promotion_groupon_rule.table.goods_name')" prop="goodsName" />

      <el-table-column align="center" property="picUrl" :label="$t('promotion_groupon_rule.table.pic_url')">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.discount')" prop="discount" />

      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.discount_member')" prop="discountMember" />

      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.status')" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'error' ">{{ statusMap[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.expire_time')" prop="expireTime" />

      <el-table-column align="center" :label="$t('promotion_groupon_rule.table.actions')" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/groupon/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">{{ $t('app.button.edit') }}</el-button>
          <el-button v-permission="['POST /admin/groupon/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="dataForm"
        status-icon
        label-position="left"
        label-width="120px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item :label="$t('promotion_groupon_rule.form.goods_id')" prop="goodsId">
          <el-input v-model="dataForm.goodsId" />
        </el-form-item>
        <el-form-item :label="$t('promotion_groupon_rule.form.discount')" prop="discount">
          <el-input v-model="dataForm.discount" />
        </el-form-item>
        <el-form-item :label="$t('promotion_groupon_rule.form.discount_member')" prop="discountMember">
          <el-input v-model="dataForm.discountMember" />
        </el-form-item>
        <el-form-item :label="$t('promotion_groupon_rule.form.expire_time')" prop="expireTime">
          <el-date-picker
            v-model="dataForm.expireTime"
            type="datetime"
            :placeholder="$t('promotion_groupon_rule.placeholder.expire_time')"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{ $t('app.button.cancel') }}</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">{{ $t('app.button.confirm') }}</el-button>
        <el-button v-else type="primary" @click="updateData">{{ $t('app.button.confirm') }}</el-button>
      </div>
    </el-dialog>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" :content="$t('app.tooltip.back_to_top')">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<script>
import { listGroupon, publishGroupon, deleteGroupon, editGroupon } from '@/api/groupon'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'GrouponRule',
  components: { BackToTop, Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false,
      dataForm: {
        id: undefined,
        goodsId: '',
        discount: '',
        discountMember: '',
        expireTime: undefined
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      statusMap: [
        '正常',
        '到期下线',
        '提前下线'
      ],
      rules: {
        goodsId: [{ required: true, message: '商品不能为空', trigger: 'blur' }],
        discount: [{ required: true, message: '团购折扣不能为空', trigger: 'blur' }],
        discountMember: [{ required: true, message: '团购人数不能为空', trigger: 'blur' }],
        expireTime: [{ required: true, message: '过期时间不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listGroupon(this.listQuery).then(response => {
        this.list = response.data.data.list
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
    resetForm() {
      this.dataForm = {
        id: undefined,
        goodsId: '',
        discount: '',
        discountMember: '',
        expireTime: undefined
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
          publishGroupon(this.dataForm).then(response => {
            this.list.unshift(response.data.data)
            this.dialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '创建团购规则成功'
            })
          }).catch(response => {
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
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          editGroupon(this.dataForm).then(() => {
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
              message: '更新团购规则成功'
            })
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
        }
      })
    },
    handleDelete(row) {
      deleteGroupon(row).then(response => {
        this.$notify.success({
          title: '成功',
          message: '删除团购规则成功'
        })
        this.getList()
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    },
    handleDownload() {
      this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['商品ID', '名称', '首页主图', '折扣', '人数要求', '活动开始时间', '活动结束时间']
          const filterVal = ['id', 'name', 'pic_url', 'discount', 'discountMember', 'addTime', 'expireTime']
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品信息')
          this.downloadLoading = false
        })
    }
  }
}
</script>
