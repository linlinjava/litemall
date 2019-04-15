import { Sku, Popup } from 'vant';

export default {
  props: {
    selectSku: Object
  },
  data() {
    return {
      showSku: false,
      showAddCartBtn: false,
      isSkuBuy: false,
      buyText: '确定'
    };
  },
  methods: {
    buyGoods(data) {
      data = this.selectSkuData(data);
      this.showSku = false;
      this.$emit('update:selectSku', data);
      this.isSkuBuy && this.$emit('skuBuy', data);
    },
    selectSkuData(data) {
      debugger
      if (data.selectedSkuComb) {
        data.selectedSkuComb.sku_str = data.selectedSkuComb.props_str_arr
          .map(str => str.match(/[^:]*:([^:]*)/)[1])
          .join(',');
      } else {
        data.selectedSkuComb = {};
      }
      return data;
    },
    skuClick() {
      this.isSkuBuy = false;
      this.showSku = true;
    }
  },
  components: {
    [Sku.name]: Sku,
    [Popup.name]: Popup
  }
};
