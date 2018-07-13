<template>
  <div class="app-container calendar-list-container">
    <ve-line :extend="chartExtend" :data="chartData" :settings="chartSettings"></ve-line>
  </div>
</template>

<script>
import { statGoods } from '@/api/stat'
import VeLine from 'v-charts/lib/line'

export default {
  components: { VeLine },
  data() {
    return {
      chartData: {},
      chartSettings: {},
      chartExtend: {}
    }
  },
  created() {
    statGoods().then(response => {
      this.chartData = response.data.data
      this.chartSettings = {
        labelMap: {
          'orders': '订单量',
          'products': '下单货品数量',
          'amount': '下单货品总额'
        }
      }
      this.chartExtend = {
        xAxis: { boundaryGap: true }
      }
    })
  }

}
</script>