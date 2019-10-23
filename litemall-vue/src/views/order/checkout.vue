<template>
<div class="order">
  <van-cell-group>
      <van-cell v-if="checkedAddress" isLink @click="goAddressList()" title="收货地址">
      <div slot="label">
        <div>
         <span>{{ checkedAddress.name }} </span>
         <span>{{ checkedAddress.tel }} </span>
      </div>
      <div>
        {{ checkedAddress.addressDetail }}
      </div>
      </div>
    </van-cell>
  </van-cell-group>

  <van-cell-group>
    <van-cell class="order-coupon" title="优惠券" is-link :value="getCouponValue()" @click="getCoupons" />
  </van-cell-group>

<!-- 优惠券列表 -->
<van-popup v-model="showList" position="bottom">
  <van-coupon-list
    :coupons="coupons"
    :chosen-coupon="chosenCoupon"
    :disabled-coupons="disabledCoupons"
    @change="onChange"
    @exchange="onExchange"
  />
</van-popup>

    <van-card
      v-for="item in checkedGoodsList"
      :key="item.id"
      :title="item.goodsName"
      :num="item.number"
      :price="item.price +'.00'"
      :thumb="item.picUrl"
    >
      <div slot="desc">
        <div class="van-card__desc">
          <van-tag plain style="margin-right:6px;" v-for="(spec, index) in item.specifications" :key="index">
            {{spec}}
          </van-tag>
        </div>
      </div>
    </van-card>

    <van-cell-group>
      <van-cell title="商品金额">
        <span class="red">{{goodsTotalPrice * 100 | yuan}}</span>
      </van-cell>
      <van-cell title="邮费">
        <span class="red">{{ freightPrice * 100| yuan}}</span>
      </van-cell>
      <van-cell title="优惠券">
        <span class="red">-{{ couponPrice * 100| yuan}}</span>
      </van-cell>
      <van-field v-model="message" placeholder="请输入备注" label="订单备注">
      <template slot="icon">{{message.length}}/50</template>
      </van-field>      
    </van-cell-group>

    <van-submit-bar
      :price="actualPrice*100"
      label="总计："
      buttonText="提交订单"
      :disabled="isDisabled"
      @submit="onSubmit"
    />
</div>
</template>

<script>
import { Card, Tag, ard, Field, SubmitBar, Toast  } from 'vant';
import { CouponCell, CouponList, Popup } from 'vant';
import { cartCheckout, orderSubmit, couponSelectList} from '@/api/api';
import { getLocalStorage, setLocalStorage } from '@/utils/local-storage';
import dayjs from 'dayjs';

export default {
  data() {
    return {
      checkedGoodsList: [],
      checkedAddress: {},
      availableCouponLength: 0, // 可用的优惠券数量
      goodsTotalPrice: 0, //商品总价
      freightPrice: 0, //快递费
      couponPrice: 0, //优惠券的价格
      grouponPrice: 0, //团购优惠价格
      orderTotalPrice: 0, //订单总价
      actualPrice: 0, //实际需要支付的总价
      message: '',

      isDisabled: false,
      showList: false,
      chosenCoupon: -1,
      coupons: [],
      disabledCoupons: [] 
    };
  },
  created() {
    this.init();
  },

  methods: {
    onSubmit() {     
      const {AddressId, CartId, CouponId, UserCouponId} = getLocalStorage('AddressId', 'CartId', 'CouponId', 'UserCouponId');

      if (AddressId === null) {
        Toast.fail('请设置收货地址');
        return;
      }


      this.isDisabled = true;

      orderSubmit({
        addressId: AddressId,
        cartId: CartId,
        couponId: CouponId,
        userCouponId: UserCouponId,
        grouponLinkId: 0,
        grouponRulesId: 0,
        message: this.message
      }).then(res => {
        
        // 下单成功，重置下单参数。
        setLocalStorage({AddressId: 0, CartId: 0, CouponId: 0});

        let orderId = res.data.data.orderId;
        this.$router.push({
          name: 'payment',
          params: { orderId: orderId }
        });
      }).catch(error => {
        this.isDisabled = false;
        this.$toast("下单失败");
      })

    },
    goAddressList() {
      this.$router.push({
        path: '/user/address'
      });
    },
    getCouponValue() {
      if(this.couponPrice !== 0 ){
        return "-¥" + this.couponPrice + ".00元"
      }
      if(this.availableCouponLength !== 0){
        return this.availableCouponLength + "张可用"
      }
      return '没有可用优惠券'
    },
    getCoupons() {
      const {AddressId, CartId, CouponId} = getLocalStorage('AddressId', 'CartId', 'CouponId');
      couponSelectList({cartId: CartId, grouponRulesId: 0}).then(res => {
        var cList = res.data.data.list;
        this.coupons = []
        this.disabledCoupons = [];
        for(var i = 0; i < cList.length; i++){
          var c = cList[i]

          var coupon = {
            id: c.id,
            cid: c.cid,
            name: c.name,
            condition: '满' + c.min + '元可用',
            value: c.discount * 100,
            description: c.desc,
            startAt: new Date(c.startTime).getTime()/1000,
            endAt: new Date(c.endTime).getTime()/1000,
            valueDesc: c.discount,
            unitDesc: '元'            
          }
          if (c.available) {
            this.coupons.push(coupon);
          } else {
            this.disabledCoupons.push(coupon);
          }
        }
        
        this.showList = true
      })
    },
    init() {
      const {AddressId, CartId, CouponId, UserCouponId} = getLocalStorage('AddressId', 'CartId', 'CouponId', 'UserCouponId');

      cartCheckout({cartId: CartId, addressId: AddressId, couponId: CouponId, userCouponId: UserCouponId, grouponRulesId: 0}).then(res => {
          var data = res.data.data

          this.checkedGoodsList = data.checkedGoodsList;
          this.checkedAddress= data.checkedAddress;
          this.availableCouponLength= data.availableCouponLength;
          this.actualPrice= data.actualPrice;
          this.couponPrice= data.couponPrice;
          this.grouponPrice= data.grouponPrice;
          this.freightPrice= data.freightPrice;
          this.goodsTotalPrice= data.goodsTotalPrice;
          this.orderTotalPrice= data.orderTotalPrice;

          setLocalStorage({AddressId: data.addressId, CartId: data.cartId, CouponId: data.couponId, UserCouponId: data.userCouponId});
      });

    },
    onChange(index) {
      this.showList = false;
      this.chosenCoupon = index;
      
      if(index === -1 ){
        setLocalStorage({CouponId: -1, UserCouponId: -1});
      }
      else{
        const couponId = this.coupons[index].cid;
        const userCouponId = this.coupons[index].id;
        setLocalStorage({CouponId: couponId, UserCouponId: userCouponId});
      }

      this.init()
    },
    onExchange() {
      this.$toast("兑换暂不支持");
    }    
  },

  components: {
    [Toast.name]: Toast ,
    [SubmitBar.name]: SubmitBar,
    [Card.name]: Card,
    [Field.name]: Field,
    [Tag.name]: Tag,
    [CouponCell.name]: CouponCell,
    [CouponList.name]: CouponList,
    [Popup.name]: Popup
  }
};
</script>


<style lang="scss" scoped>
.order-coupon {
  margin-top: 10px;
}
</style>