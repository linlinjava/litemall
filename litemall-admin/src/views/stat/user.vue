<template>
  <div class="app-container">
    <ve-histogram :extend="chartExtend" :data="chartData" :settings="chartSettings"/>
  </div>
</template>

<script>
import { statUser } from '@/api/stat'
import VeHistogram from 'v-charts/lib/histogram'

export default {
  components: { VeHistogram },
  data() {
    return {
      chartData: {},
      chartSettings: {},
      chartExtend: {}
    }
  },
  created() {
    statUser().then(response => {
      this.chartData = response.data.data
      this.chartSettings = {
        labelMap: {
          'users': '用户增长数'
        }
      }
      this.chartExtend = {
        xAxis: { boundaryGap: true },
        series: {
          label: { show: true, position: 'top' }
        }
      }
    })
  }

}
</script>
