<template>
  <div class="app-container">

    <el-form ref="topic" :rules="rules" :model="topic" status-icon label-position="left" label-width="100px" style="width: 800px; margin-left:50px;">
      <el-form-item label="专题标题" prop="title">
        <el-input v-model="topic.title"/>
      </el-form-item>
      <el-form-item label="专题子标题" prop="subtitle">
        <el-input v-model="topic.subtitle"/>
      </el-form-item>
      <el-form-item label="专题图片" prop="picUrl">
        <el-upload
          :headers="headers"
          :action="uploadPath"
          :show-file-list="false"
          :on-success="uploadPicUrl"
          class="avatar-uploader"
          accept=".jpg,.jpeg,.png,.gif">
          <img v-if="topic.picUrl" :src="topic.picUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"/>
        </el-upload>
      </el-form-item>
      <el-form-item label="专题内容" prop="content">
        <editor :init="editorInit" v-model="topic.content"/>
      </el-form-item>
      <el-form-item label="商品低价" prop="price">
        <el-input v-model="topic.price"/>
      </el-form-item>
      <el-form-item label="阅读量" prop="readCount">
        <el-input v-model="topic.readCount"/>
      </el-form-item>
      <el-form-item label="专题商品" prop="goods">
        <el-button style="float:right;" size="mini" type="primary" @click="handleCreate()">创建商品</el-button>

        <!-- 查询结果 -->
        <el-table :data="goodsList" border fit highlight-current-row>

          <el-table-column align="center" label="商品ID" prop="id"/>
          <el-table-column align="center" property="picUrl" label="图片">
            <template slot-scope="scope">
              <img :src="scope.row.picUrl" width="60">
            </template>
          </el-table-column>
          <el-table-column align="center" label="商品名称" prop="name"/>
          <el-table-column align="center" label="商品介绍" prop="brief"/>
          <el-table-column align="center" label="操作" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </div>

    <el-dialog :visible.sync="addVisiable" title="添加商品">
      <div class="search">
        <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号"/>
        <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称"/>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
        <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"/>
          <el-table-column align="center" label="商品ID" prop="id"/>
          <el-table-column align="center" property="picUrl" label="图片">
            <template slot-scope="scope">
              <img :src="scope.row.picUrl" width="40">
            </template>
          </el-table-column>
          <el-table-column align="center" label="商品名称" prop="name"/>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addVisiable = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>
.el-dialog {
  width: 800px;
}
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
  width: 145px;
  height: 145px;
  display: block;
}
</style>

<script>
import { readTopic, updateTopic } from '@/api/topic'
import { listGoods } from '@/api/goods'
import { createStorage, uploadPath } from '@/api/storage'
import BackToTop from '@/components/BackToTop'
import Editor from '@tinymce/tinymce-vue'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { getToken } from '@/utils/auth'

export default {
  name: 'TopicEdit',
  components: { BackToTop, Editor, Pagination },
  data() {
    return {
      uploadPath,
      id: 0,
      topic: {},
      goodsList: [],
      addVisiable: false,
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 5,
        id: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      selectedlist: [],
      rules: {
        title: [
          { required: true, message: '专题标题不能为空', trigger: 'blur' }
        ],
        subtitle: [
          { required: true, message: '专题子标题不能为空', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '专题内容不能为空', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '专题低价不能为空', trigger: 'blur' }
        ]
      },
      editorInit: {
        language: 'zh_CN',
        convert_urls: false,
        height: 500,
        plugins: [
          'advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'
        ],
        toolbar: [
          'searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample',
          'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'
        ],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData)
            .then(res => {
              success(res.data.data.url)
            })
            .catch(() => {
              failure('上传失败，请重新上传')
            })
        }
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  created() {
    if (this.$route.query.id == null) {
      return
    }

    this.id = this.$route.query.id
    this.getTopic()
  },
  methods: {
    getTopic() {
      this.listLoading = true
      readTopic({ id: this.id })
        .then(response => {
          this.topic = response.data.data.topic
          this.goodsList = response.data.data.goodsList
          this.listLoading = false
        })
        .catch(() => {
          this.topic = {}
          this.goodsList = []
          this.listLoading = false
        })
    },
    getList() {
      this.listLoading = true
      listGoods(this.listQuery).then(response => {
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
    handleSelectionChange(val) {
      this.selectedlist = val
    },
    uploadPicUrl: function(response) {
      this.topic.picUrl = response.data.url
    },
    handleCreate() {
      this.listQuery = {
        page: 1,
        limit: 5,
        id: undefined,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      }
      this.list = []
      this.total = 0
      this.selectedlist = []
      this.addVisiable = true
    },
    confirmAdd() {
      const newGoodsIds = []
      const newGoodsList = []
      this.selectedlist.forEach(item => {
        const id = item.id
        let found = false
        this.topic.goods.forEach(goodsId => {
          if (id === goodsId) {
            found = true
          }
        })
        if (!found) {
          newGoodsIds.push(id)
          newGoodsList.push(item)
        }
      })

      if (newGoodsIds.length > 0) {
        this.topic.goods = this.topic.goods.concat(newGoodsIds)
        this.goodsList = this.goodsList.concat(newGoodsList)
      }
      this.addVisiable = false
    },
    handleDelete(row) {
      for (var index = 0; index < this.topic.goods.length; index++) {
        if (row.id === this.topic.goods[index]) {
          this.topic.goods.splice(index, 1)
        }
      }
      for (var index2 = 0; index2 < this.goodsList.length; index2++) {
        if (row.id === this.goodsList[index2].id) {
          this.goodsList.splice(index2, 1)
        }
      }
    },
    handleCancel() {
      this.$router.push({ path: '/promotion/topic' })
    },
    handleConfirm() {
      this.$refs['topic'].validate(valid => {
        if (valid) {
          updateTopic(this.topic).then(response => {
            this.$router.push({ path: '/promotion/topic' })
          })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    }
  }
}
</script>
