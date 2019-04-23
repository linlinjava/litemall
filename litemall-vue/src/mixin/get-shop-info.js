import { SHOPINFO } from '@/api/shop';

export default {
  methods: {
    getShopInfo(...keys) {
      return new Promise((resolve, reject) => {
        const id = window.sessionStorage.getItem('id');
        if (id === null) {
          const shop_id = this.$util.getLocationParam('shop_id');
          this.$reqGet(`${SHOPINFO}/${shop_id}`)
            .then(res => {
              const { data } = res.data;
              this.$util.setSessionStorage(data);
              resolve(data);
            })
            .catch(reject);
        } else {
          resolve(this.$util.getSessionStorage(...keys));
        }
      });
    }
  }
};
