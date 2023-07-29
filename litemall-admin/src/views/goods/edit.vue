<template>
  <div class="app-container">

    <el-card class="box-card">
      <h3>{{ $t('goods_edit.section.goods') }}</h3>
      <el-form ref="goods" :rules="rules" :model="goods" label-width="150px">
        <el-form-item :label="$t('goods_edit.form.id')" prop="id">
          <el-input v-model="goods.id" disabled />
        </el-form-item>
        <el-form-item :label="$t('goods_edit.form.name')" prop="name">
          <el-input v-model="goods.name" />
        </el-form-item>
        <el-form-item :label="$t('goods_edit.form.goods_sn')" prop="goodsSn">
          <el-input v-model="goods.goodsSn" />
        </el-form-item>
        <el-form-item :label="$t('goods_edit.form.counter_price')" prop="counterPrice">
          <el-input v-model="goods.counterPrice" placeholder="0.00">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('goods_edit.form.is_new')" prop="isNew">
          <el-radio-group v-model="goods.isNew">
            <el-radio :label="true">{{ $t('goods_edit.value.is_new_true') }}</el-radio>
            <el-radio :label="false">{{ $t('goods_edit.value.is_new_false') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('goods_edit.form.is_hot')" prop="isHot">
          <el-radio-group v-model="goods.isHot">
            <el-radio :label="false">{{ $t('goods_edit.value.is_hot_false') }}</el-radio>
            <el-radio :label="true">{{ $t('goods_edit.value.is_hot_true') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('goods_edit.form.is_on_sale')" prop="isOnSale">
          <el-radio-group v-model="goods.isOnSale">
            <el-radio :label="true">{{ $t('goods_edit.value.is_on_sale_true') }}</el-radio>
            <el-radio :label="false">{{ $t('goods_edit.value.is_on_sale_false') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.pic_url')">
          <el-upload
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="uploadPicUrl"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <img v-if="goods.picUrl" :src="goods.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.gallery')">
          <el-upload
            :action="uploadPath"
            :headers="headers"
            :limit="5"
            :file-list="galleryFileList"
            :on-exceed="uploadOverrun"
            :on-success="handleGalleryUrl"
            :on-remove="handleRemove"
            multiple
            accept=".jpg,.jpeg,.png,.gif"
            list-type="picture-card"
          >
            <i class="el-icon-plus" />
          </el-upload>
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.unit')">
          <el-input v-model="goods.unit" :placeholder="$t('goods_edit.placeholder.unit')" />
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.keywords')">
          <el-tag v-for="tag in keywords" :key="tag" closable type="primary" @close="handleClose(tag)">
            {{ tag }}
          </el-tag>
          <el-input v-if="newKeywordVisible" ref="newKeywordInput" v-model="newKeyword" class="input-new-keyword" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm" />
          <el-button v-else class="button-new-keyword" type="primary" @click="showInput">{{ $t('app.button.add') }}</el-button>
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.category_id')">
          <el-cascader v-model="categoryIds" :options="categoryList" clearable expand-trigger="hover" @change="handleCategoryChange" />
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.brand_id')">
          <el-select v-model="goods.brandId" clearable>
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.brief')">
          <el-input v-model="goods.brief" />
        </el-form-item>

        <el-form-item :label="$t('goods_edit.form.detail')">
          <editor v-model="goods.detail" :init="editorInit" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="box-card">
      <h3>{{ $t('goods_edit.section.specifications') }}</h3>
      <el-table :data="specifications">
        <el-table-column property="specification" :label="$t('goods_edit.table.specification_name')" />
        <el-table-column property="value" :label="$t('goods_edit.table.specification_value')">
          <template slot-scope="scope">
            <el-tag type="primary">
              {{ scope.row.value }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="picUrl" :label="$t('goods_edit.table.specification_pic_url')">
          <template slot-scope="scope">
            <img v-if="scope.row.picUrl" :src="scope.row.picUrl" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('goods_edit.table.specification_actions')" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleSpecificationShow(scope.row)">{{ $t('app.button.settings') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="specVisiable" :title="$t('goods_edit.dialog.edit_specification')">
        <el-form ref="specForm" :rules="rules" :model="specForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <el-form-item :label="$t('goods_edit.form.specification_name')" prop="specification">
            <el-input v-model="specForm.specification" disabled />
          </el-form-item>
          <el-form-item :label="$t('goods_edit.form.specification_value')" prop="value">
            <el-input v-model="specForm.value" disabled />
          </el-form-item>
          <el-form-item :label="$t('goods_edit.form.specification_pic_url')" prop="picUrl">
            <el-upload
              :headers="headers"
              :action="uploadPath"
              :show-file-list="false"
              :on-success="uploadSpecPicUrl"
              class="avatar-uploader"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <img v-if="specForm.picUrl" :src="specForm.picUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="specVisiable = false">{{ $t('app.button.cancel') }}</el-button>
          <el-button type="primary" @click="handleSpecificationEdit">{{ $t('app.button.confirm') }}</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>{{ $t('goods_edit.section.products') }}</h3>
      <el-table :data="products">
        <el-table-column property="value" :label="$t('goods_edit.table.product_specifications')">
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.specifications" :key="tag">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="price" :label="$t('goods_edit.table.product_price')" />
        <el-table-column property="number" :label="$t('goods_edit.table.product_number')" />
        <el-table-column property="url" :label="$t('goods_edit.table.product_url')">
          <template slot-scope="scope">
            <img v-if="scope.row.url" :src="scope.row.url" width="40">
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$t('goods_edit.table.product_actions')" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleProductShow(scope.row)">{{ $t('app.button.settings') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="productVisiable" :title="$t('goods_edit.dialog.edit_product')">
        <el-form ref="productForm" :model="productForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <el-form-item :label="$t('goods_edit.form.product_specifications')" prop="specifications">
            <el-tag v-for="tag in productForm.specifications" :key="tag">
              {{ tag }}
            </el-tag>
          </el-form-item>
          <el-form-item :label="$t('goods_edit.form.product_price')" prop="price">
            <el-input v-model="productForm.price" />
          </el-form-item>
          <el-form-item :label="$t('goods_edit.form.product_number')" prop="number">
            <el-input v-model="productForm.number" />
          </el-form-item>
          <el-form-item :label="$t('goods_edit.form.product_url')" prop="url">
            <el-upload
              :headers="headers"
              :action="uploadPath"
              :show-file-list="false"
              :on-success="uploadProductUrl"
              class="avatar-uploader"
              accept=".jpg,.jpeg,.png,.gif"
            >
              <img v-if="productForm.url" :src="productForm.url" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="productVisiable = false">{{ $t('app.button.cancel') }}</el-button>
          <el-button type="primary" @click="handleProductEdit">{{ $t('app.button.confirm') }}</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>{{ $t('goods_edit.section.attributes') }}</h3>
      <el-button type="primary" @click="handleAttributeShow">{{ $t('app.button.create') }}</el-button>
      <el-table :data="attributesData">
        <el-table-column property="attribute" :label="$t('goods_edit.table.attribute_name')" />
        <el-table-column property="value" :label="$t('goods_edit.table.attribute_value')" />
        <el-table-column align="center" :label="$t('goods_edit.table.attribute_actions')" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleAttributeShow(scope.row)">{{ $t('app.button.settings') }}</el-button>
            <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">{{ $t('app.button.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog :visible.sync="attributeVisiable" :title="$t(attributeAdd ? 'goods_edit.dialog.edit_attribute_add' : 'goods_edit.dialog.edit_attribute_edit')">
        <el-form ref="attributeForm" :model="attributeForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
          <el-form-item :label="$t('goods_edit.form.attribute_name')" prop="attribute">
            <el-input v-model="attributeForm.attribute" />
          </el-form-item>
          <el-form-item :label="$t('goods_edit.form.attribute_value')" prop="value">
            <el-input v-model="attributeForm.value" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="attributeVisiable = false">{{ $t('app.button.cancel') }}</el-button>
          <el-button v-if="attributeAdd" type="primary" @click="handleAttributeAdd">{{ $t('app.button.confirm') }}</el-button>
          <el-button v-else type="primary" @click="handleAttributeEdit">{{ $t('app.button.confirm') }}</el-button>
        </div>
      </el-dialog>
    </el-card>

    <div class="op-container">
      <el-button @click="handleCancel">{{ $t('app.button.cancel') }}</el-button>
      <el-button type="primary" @click="handleEdit">{{ $t('goods_edit.button.edit') }}</el-button>
    </div>

  </div>
</template>

<style>
.el-card {
  margin-bottom: 10px;
}
.el-tag + .el-tag {
  margin-left: 10px;
}
.input-new-keyword {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
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
.op-container {
  display: flex;
  justify-content: center;
}
</style>

<script>
import { detailGoods, editGoods, listCatAndBrand } from '@/api/goods'
import { createStorage, uploadPath } from '@/api/storage'
import Editor from '@tinymce/tinymce-vue'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'

export default {
  name: 'GoodsEdit',
  components: { Editor },
  data() {
    return {
      uploadPath,
      newKeywordVisible: false,
      newKeyword: '',
      keywords: [],
      galleryFileList: [],
      categoryList: [],
      brandList: [],
      categoryIds: [],
      goods: { gallery: [] },
      specVisiable: false,
      specForm: { specification: '', value: '', picUrl: '' },
      specifications: [{ specification: '规格', value: '标准', picUrl: '' }],
      productVisiable: false,
      productForm: {
        id: 0,
        specifications: [],
        price: 0.0,
        number: 0,
        url: ''
      },
      products: [
        { id: 0, specifications: ['标准'], price: 0.0, number: 0, url: '' }
      ],
      attributeVisiable: false,
      attributeAdd: true,
      attributeForm: { attribute: '', value: '' },
      attributes: [],
      rules: {
        name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }]
      },
      editorInit: {
        language: 'zh_CN',
        height: '400px',
        convert_urls: false,
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
    },
    attributesData() {
      var attributesData = []
      for (var i = 0; i < this.attributes.length; i++) {
        if (this.attributes[i].deleted) {
          continue
        }
        attributesData.push(this.attributes[i])
      }
      return attributesData
    }
  },
  created() {
    this.init()
  },
  methods: {
    init: function() {
      if (this.$route.query.id == null) {
        return
      }

      const goodsId = this.$route.query.id
      detailGoods(goodsId).then(response => {
        this.goods = response.data.data.goods
        // 稍微调整一下前后端不一致
        if (this.goods.brandId === 0) {
          this.goods.brandId = null
        }
        if (this.goods.keywords === '') {
          this.goods.keywords = null
        }
        this.specifications = response.data.data.specifications
        this.products = response.data.data.products
        this.attributes = response.data.data.attributes
        this.categoryIds = response.data.data.categoryIds

        this.galleryFileList = []
        for (var i = 0; i < this.goods.gallery.length; i++) {
          this.galleryFileList.push({
            url: this.goods.gallery[i]
          })
        }
        const keywords = response.data.data.goods.keywords
        if (keywords !== null) {
          this.keywords = keywords.split(',')
        }
      })

      listCatAndBrand().then(response => {
        this.categoryList = response.data.data.categoryList
        this.brandList = response.data.data.brandList
      })
    },
    handleCategoryChange(value) {
      this.goods.categoryId = value[value.length - 1]
    },
    handleCancel: function() {
      this.$store.dispatch('tagsView/delView', this.$route)
      this.$router.push({ path: '/goods/list' })
    },
    handleEdit: function() {
      const finalGoods = {
        goods: this.goods,
        specifications: this.specifications,
        products: this.products,
        attributes: this.attributes
      }
      editGoods(finalGoods)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '编辑成功'
          })
          this.$store.dispatch('tagsView/delView', this.$route)
          this.$router.push({ path: '/goods/list' })
        })
        .catch(response => {
          MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
            confirmButtonText: '确定',
            type: 'error'
          })
        })
    },
    handleClose(tag) {
      this.keywords.splice(this.keywords.indexOf(tag), 1)
      this.goods.keywords = this.keywords.toString()
    },
    showInput() {
      this.newKeywordVisible = true
      this.$nextTick(_ => {
        this.$refs.newKeywordInput.$refs.input.focus()
      })
    },
    handleInputConfirm() {
      const newKeyword = this.newKeyword
      if (newKeyword) {
        this.keywords.push(newKeyword)
        this.goods.keywords = this.keywords.toString()
      }
      this.newKeywordVisible = false
      this.newKeyword = ''
    },
    uploadPicUrl: function(response) {
      this.goods.picUrl = response.data.url
    },
    uploadOverrun: function() {
      this.$message({
        type: 'error',
        message: '上传文件个数超出限制!最多上传5张图片!'
      })
    },
    handleGalleryUrl(response, file, fileList) {
      if (response.errno === 0) {
        this.goods.gallery.push(response.data.url)
      }
    },
    handleRemove: function(file, fileList) {
      for (var i = 0; i < this.goods.gallery.length; i++) {
        // 这里存在两种情况
        // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
        //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
        // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
        var url
        if (file.response === undefined) {
          url = file.url
        } else {
          url = file.response.data.url
        }

        if (this.goods.gallery[i] === url) {
          this.goods.gallery.splice(i, 1)
        }
      }
    },
    specChanged: function(label) {
      if (label === false) {
        this.specifications = [
          { specification: '规格', value: '标准', picUrl: '' }
        ]
        this.products = [
          { id: 0, specifications: ['标准'], price: 0.0, number: 0, url: '' }
        ]
      } else {
        this.specifications = []
        this.products = []
      }
    },
    uploadSpecPicUrl: function(response) {
      this.specForm.picUrl = response.data.url
    },
    handleSpecificationShow(row) {
      this.specForm = Object.assign({}, row)
      this.specVisiable = true
    },
    handleSpecificationEdit() {
      this.specForm.updateTime = ''
      for (var i = 0; i < this.specifications.length; i++) {
        const v = this.specifications[i]
        if (v.id === this.specForm.id) {
          this.specifications.splice(i, 1, this.specForm)
          break
        }
      }
      this.specVisiable = false
    },
    handleProductShow(row) {
      this.productForm = Object.assign({}, row)
      this.productVisiable = true
    },
    uploadProductUrl: function(response) {
      this.productForm.url = response.data.url
    },
    handleProductEdit() {
      this.productForm.updateTime = ''
      for (var i = 0; i < this.products.length; i++) {
        const v = this.products[i]
        if (v.id === this.productForm.id) {
          this.products.splice(i, 1, this.productForm)
          break
        }
      }
      this.productVisiable = false
    },
    handleAttributeShow(row) {
      if (row.id) {
        this.attributeForm = Object.assign({}, row)
        this.attributeAdd = false
      } else {
        this.attributeForm = {}
        this.attributeAdd = true
      }
      this.attributeVisiable = true
    },
    handleAttributeAdd() {
      this.attributes.unshift(this.attributeForm)
      this.attributeVisiable = false
    },
    handleAttributeEdit() {
      // 这是一个trick，设置updateTime的值为空，告诉后端这个记录已编辑需要更新。
      this.attributeForm.updateTime = ''
      for (var i = 0; i < this.attributes.length; i++) {
        const v = this.attributes[i]
        if (v.id === this.attributeForm.id) {
          this.attributes.splice(i, 1, this.attributeForm)
          break
        }
      }
      this.attributeVisiable = false
    },
    handleAttributeDelete(row) {
      row.deleted = true
    }
  }
}
</script>
