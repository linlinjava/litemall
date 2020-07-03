<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户名" />
      <el-input v-model="listQuery.mobile" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable />

      <el-table-column align="center" label="用户名" prop="username" />

      <el-table-column align="center" label="手机号码" prop="mobile" />

      <el-table-column align="center" label="性别" prop="gender">
        <template slot-scope="scope">
          <el-tag>{{ genderDic[scope.row.gender] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="生日" prop="birthday" />

      <el-table-column align="center" label="用户等级" prop="userLevel">
        <template slot-scope="scope">
          <el-tag>{{ levelDic[scope.row.userLevel] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <el-tag>{{ statusDic[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            @click="handleEdit(scope.row)"
          >
            编辑
          </el-button>
        </template>
      </el-table-column>

    </el-table>

    <el-dialog
      :visible.sync="dialogVisible"
      title="编辑用户"
      width="40%"
    >
      <el-form
        ref="editForm"
        :model="user"
        label-width="150px"
        size="small"
        :rules="rules"
      >
        <el-form-item label="用户ID：">
          <el-input v-model="user.id" style="width: 250px" disabled />
        </el-form-item>
        <el-form-item label="用户名：" prop="username">
          <el-input v-model="user.username" style="width: 250px" />
        </el-form-item>
        <el-form-item label="用户密码：" prop="password">
          <el-input v-model="user.password" type="password" style="width: 250px" />
        </el-form-item>
        <el-form-item label="昵称：">
          <el-input v-model="user.nickname" style="width: 250px" />
        </el-form-item>
        <el-form-item label="手机号码：" prop="mobile">
          <el-input v-model="user.mobile" style="width: 250px" />
        </el-form-item>
        <el-form-item label="用户等级：">
          <el-select v-model="user.userLevel" placeholder="请选择">
            <el-option :value="0" label="普通" />
            <el-option :value="1" label="VIP" />
            <el-option :value="2" label="高级VIP" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户状态：">
          <el-select v-model="user.status" placeholder="请选择">
            <el-option :value="0" label="可用" />
            <el-option :value="1" label="禁用" />
            <el-option :value="2" label="注销" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" @click="handleDialogConfirm()">确 定</el-button>
      </span>
    </el-dialog>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { fetchList, updateUser } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

const defaultUser = {
  id: null,
  username: null,
  password: null,
  mobile: null,
  userLevel: 0,
  status: 0,
  addTime: null,
  lastLoginTime: null,
  updateTime: null
}
export default {
  name: 'User',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        mobile: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false,
      genderDic: ['未知', '男', '女'],
      levelDic: ['普通用户', 'VIP用户', '高级VIP用户'],
      statusDic: ['可用', '禁用', '注销'],
      dialogVisible: false,
      user: Object.assign({}, defaultUser),
      rules: {
        mobile: [
          { validator: this.checkPhone, trigger: 'blur' }
        ],
        username: [
          { required: true, message: '用户名不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
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
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['用户名', '手机号码', '性别', '生日', '状态']
        const filterVal = ['username', 'mobile', 'gender', 'birthday', 'status']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户信息')
        this.downloadLoading = false
      })
    },
    handleEdit(row) {
      this.dialogVisible = true
      this.isEdit = true
      const { id, username, password, nickname, mobile, userLevel, status } = row
      this.user = { id, username, password, nickname, mobile, userLevel, status }
    },
    handleDialogConfirm() {
      this.$confirm('是否要确认?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateUser(this.user).then(response => {
          this.$message({
            message: '修改成功！',
            type: 'success'
          })
          this.dialogVisible = false
          this.getList()
        })
      })
    },
    checkPhone(rule, value, callback) {
      const phoneReg = /^1[0-9]{10}$/
      if (!value) {
        return callback(new Error('手机号码不能为空'))
      }
      setTimeout(() => {
        if (!Number.isInteger(+value)) {
          callback(new Error('请输入数字值'))
        } else {
          if (phoneReg.test(value)) {
            callback()
          } else {
            callback(new Error('手机号码格式不正确'))
          }
        }
      }, 100)
    }
  }
}
</script>
