<template>
  <div>
    <van-nav-bar title="收货地址" left-text="返回" left-arrow @click-left="goback"/>
    <van-radio-group v-model="checkedId">
      <van-cell-group v-for="item in addresses" :key="item.id" class="addressGroup">
        <van-cell isLink icon="dingwei" :title="item.name" :label="item.detailedAddress"/>

        <van-cell>
          <van-radio slot="title" :name="item.id" @click="setDefaultAddress(item)">
            <span>{{item.isDefault ? '默认地址' : '设为默认'}}</span>
          </van-radio>
          <div>
            <router-link
              :to="{name: 'address-edit', params: {addressId: item.id}}"
              style="margin-right: 10px;"
            >
              <van-icon name="editor"/>编辑
            </router-link>
            <span @click="delAddress(item.id)">
              <van-icon name="lajitong"/>删除
            </span>
          </div>
        </van-cell>
      </van-cell-group>
    </van-radio-group>

    <van-button class="bottom_btn" @click="setNewAddress" type="primary" bottomAction>添加地址</van-button>
  </div>
</template>

<script>
import { Checkbox, Radio, RadioGroup, NavBar } from 'vant';
import { async } from 'q';
import _ from 'lodash';

export default {
  data() {
    return {
      checkedId: '',
      default_address: 1,
      addressList: [
        {
          id: 1
        },
        {
          id: 2
        },
        {
          id: 3
        },
        {
          id: 4
        }
      ],
      addresses: []
    };
  },

  created() {
    this.loadAddress();
  },
  methods: {
    async setDefaultAddress(item) {
      let { data } = await this.$reqGet(
        `/wx/address/detail?id=${item.id}`
      );
      let params = data.data;
      params.isDefault = true;
      await this.$reqPost('/wx/address/save', params);
    },
    async delAddress(id) {
      let { data } = await this.$reqPost('/wx/address/delete', {
        id: id
      });
      this.loadAddress();
    },
    setNewAddress() {
      this.$router.push({ name: 'address-edit', params: { addressId: -1 } });
    },
    goback() {
      this.$router.go(-1);
    },
    async loadAddress() {
      let { data } = await this.$reqGet('/wx/address/list');
      this.addresses = data.data;
      this.checkedId = _.find(this.addresses, result => {
        return result.isDefault === true;
      }).id;
    }
  },

  components: {
    [NavBar.name]: NavBar,
    [Checkbox.name]: Checkbox,
    [Radio.name]: Radio,
    [RadioGroup.name]: RadioGroup
  }
};
</script>


<style lang="scss" scoped>
.addressGroup {
  margin-bottom: 10px;
  &:last-child {
    margin-bottom: 0;
  }
}

.bottom_btn {
  position: fixed;
  bottom: 0;
}
</style>
