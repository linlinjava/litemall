// 滚动条记录， 适用于 keep-alive 组件
import { debounce } from 'lodash';
export default {
  data() {
    return {
      scrollTop: 0
    };
  },

  mounted() {
    const vm = this;

    vm.$el.addEventListener(
      'scroll',
      debounce(() => {
        vm.scrollTop = vm.$el.scrollTop;
      }, 50)
    );
  },

  activated() {
    this.$el.scrollTop = this.scrollTop;
  }
};
