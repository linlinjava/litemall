<template>
	<van-area v-once :areaList="areaList" @confirm="areaConfirm" @cancel="areaCanccel" />
</template>


<script>
import areaList from './area.json';
import { Area } from 'vant';

export default {
  name: 'popup-area',

  data() {
    return {
      areaList
    };
  },

  methods: {
    areaCanccel() {
      this.$emit('cancel');
    },
    areaConfirm(areaData) {
      if (areaData.every(area => area.code != -1)) {
        this.$emit('confirm', this.analyArea(areaData));
        this.$emit('cancel');
      } else {
        this.$toast('请选择完整地区');
      }
    },
    analyArea(areaData) {
      const province = areaData[0] || {};
      const city = areaData[1] || {};
      const district = areaData[2] || {};

      return {
        id: null,
        area_name: `${province.name}  ${city.name}  ${district.name} `,
        district: district.code,
        city: city.code,
        province: province.code
      };
    }
  },

  components: {
    [Area.name]: Area
  }
};
</script>
