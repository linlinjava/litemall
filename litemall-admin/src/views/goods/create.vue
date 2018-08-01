<template>
  <div class="app-container calendar-list-container">

    <el-card class="box-card">
      <h3>商品介绍</h3>
      <el-form :rules="rules" ref="goods" :model="goods" label-width="150px">
        <el-form-item label="商品编号" prop="goodsSn">
          <el-input v-model="goods.goodsSn"></el-input>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="goods.name"></el-input>
        </el-form-item>
        <el-form-item label="专柜价格" prop="counterPrice">
          <el-input v-model="goods.counterPrice" placeholder="0.00">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="当前价格" prop="retailPrice">
          <el-input v-model="goods.retailPrice" placeholder="0.00">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="是否新品" prop="isNew">
          <el-radio-group v-model="goods.isNew">
            <el-radio :label="true">新品</el-radio>
            <el-radio :label="false">非新品</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否热卖" prop="isHot">
          <el-radio-group v-model="goods.isHot">
            <el-radio :label="false">普通</el-radio>
            <el-radio :label="true">热卖</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否在售" prop="isOnSale">
          <el-radio-group v-model="goods.isOnSale">
            <el-radio :label="true">在售</el-radio>
            <el-radio :label="false">未售</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="商品图片">
          <el-upload class="avatar-uploader" :action="uploadPath" list-type="picture-card" :show-file-list="false" :headers="headers"
                     accept=".jpg,.jpeg,.png,.gif" :on-success="uploadPicUrl">
            <img v-if="goods.picUrl" :src="goods.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="宣传画廊">
          <el-upload :action="uploadPath" :limit="5" multiple accept=".jpg,.jpeg,.png,.gif" list-type="picture-card" :headers="headers"
                     :on-exceed="uploadOverrun" :on-success="handleGalleryUrl" :on-remove="handleRemove">
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="商品单位">
          <el-input v-model="goods.unit" placeholder="件 / 个 / 盒"></el-input>
        </el-form-item>

        <el-form-item label="关键字">
          <el-tag :key="tag" v-for="tag in keywords" closable type="primary" @close="handleClose(tag)">
            {{tag}}
          </el-tag>
          <el-input class="input-new-keyword" v-if="newKeywordVisible" v-model="newKeyword" ref="newKeywordInput"
                    size="small" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm">
          </el-input>
          <el-button v-else class="button-new-keyword" size="small" type="primary" @click="showInput">+ 增加</el-button>
        </el-form-item>

        <el-form-item label="所属分类">
          <el-cascader expand-trigger="hover" :options="categoryList" @change="handleCategoryChange"></el-cascader>
        </el-form-item>

        <el-form-item label="所属品牌商">
          <el-select v-model="goods.brandId">
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="商品简介">
          <el-input v-model="goods.brief"></el-input>
        </el-form-item>

        <el-form-item label="商品详细介绍">
          <editor :init="editorInit" v-model="goods.detail"></editor>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="box-card">
      <h3>商品规格</h3>
      <el-row type="flex" align="middle" :gutter="20" style="padding:20px 0;">
        <el-col :span="10">
          <el-radio-group v-model="multipleSpec" @change="specChanged">
            <el-radio-button :label="false">默认标准规格</el-radio-button>
            <el-radio-button :label="true">多规格支持</el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :span="10" v-if="multipleSpec">
          <el-button :plain="true" @click="handleSpecificationShow" type="primary">添加</el-button>
        </el-col>
      </el-row>

      <el-table :data="specifications">
        <el-table-column property="specification" label="规格名"></el-table-column>
        <el-table-column property="value" label="规格值">
          <template slot-scope="scope">
            <el-tag type="primary">
              {{scope.row.value}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="picUrl" label="规格图片">
          <template slot-scope="scope">
            <img :src="scope.row.picUrl" width="40" v-if="scope.row.picUrl"/>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width"
                         v-if="multipleSpec">
          <template slot-scope="scope">
            <el-button type="danger" size="mini" @click="handleSpecificationDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title="设置规格" :visible.sync="specVisiable">
        <el-form :rules="rules" ref="specForm" :model="specForm" status-icon label-position="left" label-width="100px"
                 style='width: 400px; margin-left:50px;'>
          <el-form-item label="规格名" prop="specification">
            <el-input v-model="specForm.specification"></el-input>
          </el-form-item>
          <el-form-item label="规格值" prop="value">
            <el-input v-model="specForm.value"></el-input>
          </el-form-item>
          <el-form-item label="规格图片" prop="picUrl">
            <el-upload class="avatar-uploader" :action='uploadPath' list-type="picture-card" :show-file-list="false" :headers="headers"
                       accept=".jpg,.jpeg,.png,.gif" :on-success="uploadSpecPicUrl">
              <img v-if="specForm.picUrl" :src="specForm.picUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="specVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleSpecificationAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>商品库存</h3>
      <el-table :data="products">
        <el-table-column property="value" label="货品规格">
          <template slot-scope="scope">
            <el-tag :key="tag" v-for="tag in scope.row.specifications">
              {{tag}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column property="price" width="100" label="货品售价">
        </el-table-column>
        <el-table-column property="number" width="100" label="货品数量">
        </el-table-column>
        <el-table-column property="url" width="100" label="货品图片">
          <template slot-scope="scope">
            <img :src="scope.row.url" width="40" v-if="scope.row.url"/>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" width="100" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleProductShow(scope.row)">设置</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title="设置货品" :visible.sync="productVisiable">
        <el-form ref="productForm" :model="productForm" status-icon label-position="left" label-width="100px"
                 style='width: 400px; margin-left:50px;'>
          <el-form-item label="货品规格列" prop="specifications">
            <el-tag :key="tag" v-for="tag in productForm.specifications">
              {{tag}}
            </el-tag>
          </el-form-item>
          <el-form-item label="货品售价" prop="price">
            <el-input v-model="productForm.price"></el-input>
          </el-form-item>
          <el-form-item label="货品数量" prop="number">
            <el-input v-model="productForm.number"></el-input>
          </el-form-item>
          <el-form-item label="货品图片" prop="url">
            <el-upload class="avatar-uploader" :action='uploadPath' list-type="picture-card" :show-file-list="false" :headers="headers"
                       accept=".jpg,.jpeg,.png,.gif" :on-success="uploadProductUrl">
              <img v-if="productForm.url" :src="productForm.url" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="productVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleProductEdit">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <el-card class="box-card">
      <h3>商品参数</h3>
      <el-button :plain="true" @click="handleAttributeShow" type="primary">添加</el-button>
      <el-table :data="attributes">
        <el-table-column property="attribute" label="商品参数名称">
        </el-table-column>
        <el-table-column property="value" label="商品参数值">
        </el-table-column>
        <el-table-column align="center" label="操作" width="100" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="danger" size="mini" @click="handleAttributeDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title="设置商品参数" :visible.sync="attributeVisiable">
        <el-form ref="attributeForm" :model="attributeForm" status-icon label-position="left" label-width="100px"
                 style='width: 400px; margin-left:50px;'>
          <el-form-item label="商品参数名称" prop="attribute">
            <el-input v-model="attributeForm.attribute"></el-input>
          </el-form-item>
          <el-form-item label="商品参数值" prop="value">
            <el-input v-model="attributeForm.value"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="attributeVisiable = false">取消</el-button>
          <el-button type="primary" @click="handleAttributeAdd">确定</el-button>
        </div>
      </el-dialog>
    </el-card>

    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button @click="handlePublish" type="primary">上架</el-button>
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
    width: 120px;
    height: 120px;
    display: block;
  }
</style>

<script>
  import { publishGoods, listCatAndBrand } from '@/api/goods'
  import { createStorage, uploadPath } from '@/api/storage'
  import Editor from '@tinymce/tinymce-vue'
  import { MessageBox } from 'element-ui'
  import { getToken } from '@/utils/auth'

  export default {
    name: 'GoodsCreate',
    components: { Editor },
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
        newKeywordVisible: false,
        newKeyword: '',
        keywords: [],
        categoryList: [],
        brandList: [],
        goods: { picUrl: '', gallery: [] },
        specVisiable: false,
        specForm: { specification: '', value: '', picUrl: '' },
        multipleSpec: false,
        specifications: [{ specification: '规格', value: '标准', picUrl: '' }],
        productVisiable: false,
        productForm: { id: 0, specifications: [], price: 0.00, number: 0, url: '' },
        products: [{ id: 0, specifications: ['标准'], price: 0.00, number: 0, url: '' }],
        attributeVisiable: false,
        attributeForm: { attribute: '', value: '' },
        attributes: [],
        rules: {
          goodsSn: [{ required: true, message: '商品编号不能为空', trigger: 'blur' }],
          name: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }]
        },
        editorInit: {
          language: 'zh_CN',
          plugins: ['advlist anchor autolink autoresize autosave emoticons fullscreen hr image imagetools importcss insertdatetime legacyoutput link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
          toolbar: ['bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript ', 'hr bullist numlist link image charmap preview anchor pagebreak fullscreen media table emoticons forecolor backcolor'],
          images_upload_handler: function(blobInfo, success, failure) {
            const formData = new FormData()
            formData.append('file', blobInfo.blob())
            createStorage(formData).then(res => {
              success(res.data.data.url)
            }).catch(() => {
              failure('上传失败，请重新上传')
            })
          }
        }
      }
    },
    created() {
      this.init()
    },

    methods: {
      init: function() {
        listCatAndBrand().then(response => {
          this.categoryList = response.data.data.categoryList
          this.brandList = response.data.data.brandList
        })
      },
      handleCategoryChange(value) {
        this.goods.categoryId = value[value.length - 1]
      },
      handleCancel: function() {
        this.$router.push({ path: '/goods/goods' })
      },
      handlePublish: function() {
        const finalGoods = {
          goods: this.goods,
          specifications: this.specifications,
          products: this.products,
          attributes: this.attributes
        }
        publishGoods(finalGoods).then(response => {
          this.$notify({
            title: '成功',
            message: '创建成功',
            type: 'success',
            duration: 2000
          })
          this.$router.push({ path: '/goods/list' })
        }).catch(response => {
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
          this.specifications = [{ specification: '规格', value: '标准', picUrl: '' }]
          this.products = [{ id: 0, specifications: ['标准'], price: 0.00, number: 0, url: '' }]
        } else {
          this.specifications = []
          this.products = []
        }
      },
      uploadSpecPicUrl: function(response) {
        this.specForm.picUrl = response.data.url
      },
      handleSpecificationShow() {
        this.specForm = { specification: '', value: '', picUrl: '' }
        this.specVisiable = true
      },
      handleSpecificationAdd() {
        var index = this.specifications.length - 1
        for (var i = 0; i < this.specifications.length; i++) {
          const v = this.specifications[i]
          if (v.specification === this.specForm.specification) {
            index = i
          }
        }

        this.specifications.splice(index + 1, 0, this.specForm)
        this.specVisiable = false

        this.specToProduct()
      },
      handleSpecificationDelete(row) {
        const index = this.specifications.indexOf(row)
        this.specifications.splice(index, 1)
        this.specToProduct()
      },
      specToProduct() {
        if (this.specifications.length === 0) {
          return
        }
        // 根据specifications创建临时规格列表
        var specValues = []
        var spec = this.specifications[0].specification
        var values = []
        values.push(0)

        for (var i = 1; i < this.specifications.length; i++) {
          const aspec = this.specifications[i].specification

          if (aspec === spec) {
            values.push(i)
          } else {
            specValues.push(values)
            spec = aspec
            values = []
            values.push(i)
          }
        }
        specValues.push(values)

        // 根据临时规格列表生产货品规格
        // 算法基于 https://blog.csdn.net/tyhj_sf/article/details/53893125
        var productsIndex = 0
        var products = []
        var combination = []
        var n = specValues.length
        for (var s = 0; s < n; s++) {
          combination[s] = 0
        }
        var index = 0
        var isContinue = false
        do {
          var specifications = []
          for (var x = 0; x < n; x++) {
            var z = specValues[x][combination[x]]
            specifications.push(this.specifications[z].value)
          }
          products[productsIndex] = { id: productsIndex, specifications: specifications, price: 0.00, number: 0, url: '' }
          productsIndex++

          index++
          combination[n - 1] = index
          for (var j = n - 1; j >= 0; j--) {
            if (combination[j] >= specValues[j].length) {
              combination[j] = 0
              index = 0
              if (j - 1 >= 0) {
                combination[j - 1] = combination[j - 1] + 1
              }
            }
          }
          isContinue = false
          for (var p = 0; p < n; p++) {
            if (combination[p] !== 0) {
              isContinue = true
            }
          }
        } while (isContinue)

        this.products = products
      },
      handleProductShow(row) {
        this.productForm = Object.assign({}, row)
        this.productVisiable = true
      },
      uploadProductUrl: function(response) {
        this.productForm.url = response.data.url
      },
      handleProductEdit() {
        for (var i = 0; i < this.products.length; i++) {
          const v = this.products[i]
          if (v.id === this.productForm.id) {
            this.products.splice(i, 1, this.productForm)
            break
          }
        }
        this.productVisiable = false
      },
      handleAttributeShow() {
        this.attributeForm = {}
        this.attributeVisiable = true
      },
      handleAttributeAdd() {
        this.attributes.unshift(this.attributeForm)
        this.attributeVisiable = false
      },
      handleAttributeDelete(row) {
        const index = this.attributes.indexOf(row)
        this.attributes.splice(index, 1)
      }
    }
  }
</script>
