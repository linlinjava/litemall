export default {
  props: {
    goods: {
      type: Object,
      default: () => ({})
    }
  },

  computed: {
    goodsStatusToMe() {
      const is_buy = this.goods.is_buy;
      const is_collect = this.goods.is_collect;
      return is_buy ? '我购买过' : is_collect ? '我收藏过' : '';
    }
  },

  methods: {
    OnClick() {
      this.$emit('click');
    }
  }
};
