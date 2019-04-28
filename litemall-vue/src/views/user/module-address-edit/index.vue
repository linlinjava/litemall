<template>
  <div>
    <van-nav-bar title="编辑地址" left-text="返回" left-arrow @click-left="goback"/>
    <van-address-edit
      style="background-color: #fff;"
      :areaList="areaList"
      :addressInfo="addressInfo"
      :isSaving="isSave"
      showPostal
      @save="save"
    />
  </div>
</template>

<script>
import { AddressEdit, NavBar } from 'vant';
import areaList from './area.json';

export default {
  name: 'address-edit',

  props: {
    addressId: [Number, String]
  },

  data() {
    return {
      areaList,
      isSave: false,
      addressInfo: {}
    };
  },
  created() {
    if (this.addressId !== -1) {
      this.init();
    }
  },

  methods: {
    async init() {
      let { data } = await this.$reqGet(
        `/wx/address/detail?id=${this.addressId}`
      );
      let address = data.data;
      this.addressInfo = {
        name: address.name,
        tel: address.mobile,
        // country: '中国',
        province: address.provinceName,
        city: address.cityName,
        // county: '东城区',
        areaCode: '110101',
        // postalCode: '000000',
        // addressDetail: '1',
        isDefault: false,
        id: this.addressId,
        areaId: ''

        // address_detail: '是的是的',
        // area_code: '330106',
        // postal_code: 123456
      };

      // litemall data
      // {
      //     "errno":0,
      //     "data":{
      //         "isDefault":true,
      //         "areaId":377,
      //         "address":"adadada",
      //         "cityName":"市辖区",
      //         "areaName":"西城区",
      //         "name":"asd",
      //         "mobile":"13664552998",
      //         "id":2,
      //         "cityId":32,
      //         "provinceName":"北京市",
      //         "provinceId":1
      //     },
      //     "errmsg":"成功"
      // }
      console.log(areaList);
    },
    async save(data) {
      let params = {};
      params.address = data.addressDetail;
      params.areaId = 376;
      params.cityId = 32;
      params.id = 0;
      params.isDefault = true;
      params.mobile = data.tel;
      params.name = data.name;
      params.provinceId = 1;
      let { response } = await this.$reqPost(
        '/wx/address/save',
        params
      );

      this.$toast('成功');
      this.$router.push({ path: '/user/address' });
    },
    goback() {
      this.$router.go(-1);
    }
  },

  components: {
    [NavBar.name]: NavBar,
    [AddressEdit.name]: AddressEdit
  }
};
</script>
