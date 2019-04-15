<template>
  <div class="item_cell_group">
    <van-cell-group>
      <van-cell
        title="选择规格"
        isLink
        :value="selectSku.selectedSkuComb.sku_str"
        @click.native="skuClick"
      />
      <van-cell title="商品属性" isLink @click.native="propsPopup = true"/>
      <!-- <van-cell
        title="配送至"
        isLink
        :value="addressVal.area_name"
        @click.native="addressPopup = true"
      />-->
      <!-- <van-cell title="运费" :value="postFee | yuan"/> -->
    </van-cell-group>
    <van-sku
      v-model="showSku"
      :showAddCartBtn="showAddCartBtn"
      :buyText="buyText"
      :sku="skus.sku"
      :goods="skus.goods_info"
      :goodsId="goodsInfo.id"
      :disableStepperInput="true"
      @buy-clicked="buyGoods"
      @add-cart="onAddCartClicked"
    />
    <van-popup v-model="propsPopup" position="bottom">
      <popup-props :propsStr="props_str"></popup-props>
    </van-popup>

    <van-popup v-model="areaPopup" position="bottom">
      <popup-area v-if="areaPopup" @confirm="emitAddressVal" @cancel="areaPopup = false"/>
    </van-popup>

    <van-popup v-model="addressPopup" position="bottom">
      <popup-address
        :is-show="addressPopup"
        :addressVal="addressVal"
        :default-id="defaultId"
        @confirm="emitAddressVal"
        @area-click="areaClick"
      />
    </van-popup>
  </div>
</template>

<script>
const popupArea = () =>
  import(/* webpackChunkName: "popup-area" */ './popup-area');
import popupAddress from './popup-address';
import popupProps from './popup-props';
import actionMixin from '../mix';
import { ADDRESS_DEFAULT } from '@/api/user';
import _ from 'lodash';
import { debug } from 'util';
// import { POST_FEE } from '@/api/shop';

function getGoodInfoFromSpecification(productLists, compareS1, compareS2) {
  if (productLists.length === 0) {
    return productLists;
  }
  _.each(productLists, v => {
    if (_.without(v, compareS1, compareS2).length > 0) {
      return v;
    }
  });
  console.error(
    compareS1 +
      ',' +
      compareS2 +
      ' getGoodInfoFromSpecification no match ！！！'
  );
  return {};
}

export default {
  name: 'entity-group',

  props: {
    goodsInfo: {
      type: Object,
      default: () => ({})
    },
    specification_list: {
      type: Array,
      default: () => []
    },
    selectSku: {
      type: Object,
      default: () => ({})
    },
    addressVal: {
      type: Object,
      default: () => ({})
    }
  },

  mixins: [actionMixin],

  data() {
    const sku = this.skuAdapter(this.goodsInfo.skus, this.goodsInfo.prop_imgs);

    const goods_info = this.setSkuGoodsInfo(this.goodsInfo);
    const postFee = this.goodsInfo.is_fenxiao ? '免邮费' : '';
    return {
      postFee,
      cartOrBuy: '',
      propsPopup: false,
      addressPopup: false,
      areaPopup: false,
      skus: {
        sku,
        goods_info
      },
      defaultId: -1
    };
  },

  computed: {
    props_str() {
      // this.goodsInfo.props_str = '品牌:GOON大王天使;纸尿裤尺码:S58;';
      // if (this.goodsInfo.props_str) {
      //   return this.goodsInfo.props_str
      //     .split(';')
      //     .filter(str => str != '')
      //     .map(str => str.split(':'));
      // }

      let props_arr = [];
      _.each(this.goodsInfo.attribute, json => {
        props_arr.push([json['attribute'], json['value']]);
      });
      return props_arr || [];
    },
    weight() {
      return parseFloat(this.goodsInfo.weight) * this.selectSku.selectedNum;
    }
  },

  created() {
    // this.getAddressDefault();
  },

  methods: {
    skuClick(status) {
      this.cartOrBuy = status;
      this.showSku = true;
    },
    buyGoods(data) {
      switch (this.cartOrBuy) {
        case 'cart':
          this.addCart(data);
          break;
        case 'buy':
          this.bug(data);
          break;
        default:
          break;
      }

      // this.$toast(JSON.stringify(data));
      this.showSku = false;
    },
    getProductId(s1, s2) {
      var productId;
      let s1_name = _.find(this.specification_list, v => {
        return v.id === s1;
      }).value;
      let s2_name = _.find(this.specification_list, v => {
        return v.id === s2;
      }).value;
      _.each(this.goodsInfo.productList, v => {
        let result = _.without(v.specifications, s1_name, s2_name);
        if (result.length === 0) {
          productId = v.id;
        }
      });
      return productId;
    },
    getProductIdByOne(s1) {
      var productId;
      let s1_name = _.find(this.specification_list, v => {
        return v.id === s1;
      }).value;

      _.each(this.goodsInfo.productList, v => {
        let result = _.without(v.specifications, s1_name);
        if (result.length === 0) {
          productId = v.id;
        }
      });
      return productId;
    },
    async bug(data) {
      console.log(data);
      // debugger;
      let params = {
        goodsId: data.goodsId,
        number: data.selectedNum,
        productId: this.getProductIdByOne(data.selectedSkuComb.s1)
      };
      // 如果包含s2说明多种规格,目前支持两个规格
      if (_.has(data.selectedSkuComb, 's2')) {
        params.productId = this.getProductId(
          data.selectedSkuComb.s1,
          data.selectedSkuComb.s2
        );
      }
      this.$reqPost('/wx/cart/fastadd', params).then(req => {
        let cartId = req.data.data;
        // this.$reqGet(
        //   `/wx/cart/checkout?cartId=${cartId}&addressId=0&couponId=0&grouponRulesId=0`
        // ).then(() => {cartId=${cartId}
        this.$router.push({
          name: `placeOrderEntity`,
          params: { cartId: cartId }
        });
        // });
      });
    },
    addCart(data) {
      console.log(data);
      // debugger;
      let params = {
        goodsId: data.goodsId,
        number: data.selectedNum,
        productId: this.getProductIdByOne(data.selectedSkuComb.s1)
      };
      // 如果包含s2说明多种规格,目前支持两个规格
      if (_.has(data.selectedSkuComb, 's2')) {
        params.productId = this.getProductId(
          data.selectedSkuComb.s1,
          data.selectedSkuComb.s2
        );
      }
      this.$reqPost('/wx/cart/add', params).then(() => {
        this.$emit('cart-count', data.selectedNum);
        this.$toast({
          message: '已添加至购物车',
          duration: 1500
        });
        // this.cartInfo = String(parseInt(this.cartInfo) + 1);
      });
    },
    onAddCartClicked(data) {
      this.$toast(JSON.stringify(data));
    },
    areaClick() {
      this.areaPopup = true;
      this.addressPopup = false;
    },
    emitAddressVal(data) {
      this.$emit('update:addressVal', data);
    },
    setSkuGoodsInfo({ name, pic_url, sales_price }) {
      return {
        title: name,
        picture: pic_url,
        price: sales_price
      };
    },
    getAddressDefault() {
      localStorage.getItem('Authorization') &&
        this.$reqGet(ADDRESS_DEFAULT).then(res => {
          const data = res.data.data;
          this.defaultId = data.id;
          this.emitAddressVal(data);
        });
    },
    skuAdapter(skus = [], prop_imgs = []) {
      // debugger;
      const tree = this.setSkuTree(skus, prop_imgs);
      const list = this.setSkuList(skus);
      const skuInfo = {
        price: parseInt(this.goodsInfo.retailPrice), // 未选择规格时的价格
        stock_num: this.goodsInfo.productList[0].number || 1024, // 总库存
        collection_id: '', // 无规格商品skuId取collection_id，否则取所选sku组合对应的id
        none_sku: false, // 是否无规格商品
        hide_stock: false
      };
      return {
        tree,
        list,
        ...skuInfo
      };
    },
    setSkuList(skus) {
      // debugger;
      // return [
      //   // {
      //   //   id: 2259,
      //   //   price: this.goodsInfo.retailPrice * 100,
      //   //   discount: 100,
      //   //   code: '',
      //   //   s1: this.goodsInfo.specificationList[0].valueList[0].id,
      //   //   // s1: this.goodsInfo.productList[0].id,
      //   //   kdt_id: 55,
      //   //   discount_price: 0,
      //   //   stock_num: this.goodsInfo.productList[0].number || 1024,
      //   //   stock_mode: 0,
      //   //   is_sell: null,
      //   //   combin_sku: false,
      //   //   goods_id: 946755
      //   // },
      //   // {
      //   //   id: 2259,
      //   //   price: this.goodsInfo.retailPrice * 100,
      //   //   discount: 100,
      //   //   code: '',
      //   //   s1: this.goodsInfo.specificationList[0].valueList[1].id,
      //   //   // s1: this.goodsInfo.productList[0].id,
      //   //   kdt_id: 55,
      //   //   discount_price: 0,
      //   //   stock_num: this.goodsInfo.productList[0].number || 1024,
      //   //   stock_mode: 0,
      //   //   is_sell: null,
      //   //   combin_sku: false,
      //   //   goods_id: 946755
      //   // },
      //   // {
      //   //   id: 2259,
      //   //   price: this.goodsInfo.retailPrice * 100,
      //   //   discount: 100,
      //   //   code: '',
      //   //   s2: this.goodsInfo.specificationList[1].valueList[0].id,
      //   //   // s1: this.goodsInfo.productList[0].id,
      //   //   kdt_id: 55,
      //   //   discount_price: 0,
      //   //   stock_num: this.goodsInfo.productList[0].number || 1024,
      //   //   stock_mode: 0,
      //   //   is_sell: null,
      //   //   combin_sku: false,
      //   //   goods_id: 946755
      //   // },
      //   // {
      //   //   id: 2259,
      //   //   price: this.goodsInfo.retailPrice * 100,
      //   //   discount: 100,
      //   //   code: '',
      //   //   s2: this.goodsInfo.specificationList[1].valueList[1].id,
      //   //   // s1: this.goodsInfo.productList[0].id,
      //   //   kdt_id: 55,
      //   //   discount_price: 0,
      //   //   stock_num: this.goodsInfo.productList[0].number || 1024,
      //   //   stock_mode: 0,
      //   //   is_sell: null,
      //   //   combin_sku: false,
      //   //   goods_id: 946755
      //   // },

      //   {
      //     id: 2259, // skuId，下单时后端需要
      //     price: 30, // 价格（单位分）
      //     s1: 270, // 规格类目 k_s 为 s1 的对应规格值 id
      //     s2: 272, // 规格类目 k_s 为 s2 的对应规格值 id
      //     s3: '0', // 最多包含3个规格值，为0表示不存在该规格
      //     stock_num: 30 // 当前 sku 组合对应的库存
      //   }
      // ];
      var sku_list = [];
      // 如果是多种规格则需要特殊处理
      if (this.goodsInfo.specificationList.length > 1) {
        _.each(this.goodsInfo.productList, v => {
          if (v.specifications.length > 1) {
            var sku_list_obj = {};
            _.each(v.specifications, (specificationName, index) => {
              sku_list_obj[
                's' + (~~index + 1)
              ] = this.findSpecificationInfoByName(specificationName).id;
            });
          }
          sku_list_obj.price = v.price * 100;
          sku_list_obj.stock_num = v.number;
          sku_list.push(sku_list_obj);
        });
        // debugger;
      } else {
        sku_list = [
          {
            id: 2259,
            price: this.goodsInfo.retailPrice * 100,
            discount: 100,
            code: '',
            s1: this.goodsInfo.specificationList[0].valueList[0].id,
            // s1: this.goodsInfo.productList[0].id,
            kdt_id: 55,
            discount_price: 0,
            stock_num: this.goodsInfo.productList[0].number || 1024,
            stock_mode: 0,
            is_sell: null,
            combin_sku: false,
            goods_id: 946755
          }
        ];
      }
      return sku_list;
    },
    findSpecificationInfoByName(name) {
      let info = {};
      _.each(this.specification_list, specification => {
        if (specification.value === name) {
          info = specification;
        }
      });
      return info;
    },
    setSkuTree(skus, prop_imgs) {
      // const skulist = [];
      // skus.forEach(el => {
      //   const propImg = prop_imgs.find(img => img.props == el.props);
      //   el.props_str_arr = el.props_str.split(';').filter(str => str != '');
      //   el.props_arr = el.props.split(';').filter(str => str != '');
      //   el.imgUrl = propImg ? propImg.url : '';
      // });

      // debugger;
      // skus.forEach(el => {
      //   el.props_str_arr.forEach((sku, i) => {
      //     const prop = el.props_arr[i];
      //     // 大规格
      //     const pName = sku.substr(0, sku.indexOf(':'));
      //     const k_id = prop.substr(0, prop.indexOf(':'));
      //     // 规格值 prop_values
      //     const vName = sku.substr(sku.indexOf(':') + 1);
      //     const vid = prop.substr(prop.indexOf(':') + 1);
      //     debugger;
      //     if (!skulist[i]) {
      //       skulist[i] = {
      //         k_id,
      //         k: pName,
      //         v: [
      //           {
      //             id: vid,
      //             name: vName,
      //             imgUrl: el.imgUrl
      //           }
      //         ],
      //         k_s: `s${i + 1}`
      //       };
      //     } else {
      //       const isPass = skulist[i].v.some(val => val.id == vid);
      //       !isPass &&
      //         skulist[i].v.push({
      //           id: vid,
      //           name: vName,
      //           imgUrl: el.imgUrl
      //         });
      //     }
      //   });
      // });
      // return skulist;
      let that = this;
      let specifications = [];
      _.each(this.goodsInfo.specificationList, (v, k) => {
        _.each(v.valueList, vv => {
          vv.name = vv.value;

          _.each(this.goodsInfo.productList, p => {
            if (p.id === vv.id) {
              vv.imgUrl = p.url;
              vv.number = p.number;
              // vv.id = getGoodInfoFromSpecification(
              //   this.goodsInfo.productList,
              //   v.name,
              //   vv.value
              // ).id;

              //todo id
            }
          });
          _.isEmpty(vv.imgUrl)
            ? (vv.imgUrl = this.goodsInfo.productList[0].url)
            : vv.imgUrl;
        });
        _.each(v.valueList, v => {
          that.specification_list.push(v);
        });

        specifications.push({
          k: v.name,
          v: v.valueList,
          k_s: 's' + (~~k + 1)
        });
      });

      // _.each(this.goodsInfo.productList, v => {
      //   v.imgUrl = v.url;
      //   v.specification = v.specifications[0];

      // });
      // specifications.push({
      //   k: '规格',
      //   v: this.goodsInfo.productList,
      //   k_s: 's1'
      // });
      // debugger;
      return specifications;
    }
  },

  components: {
    popupArea,
    [popupAddress.name]: popupAddress,
    [popupProps.name]: popupProps
  }
};
</script>

<style lang="scss" scoped>
</style>
