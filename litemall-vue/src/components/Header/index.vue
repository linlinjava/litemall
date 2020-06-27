<template>
<div>
    <van-nav-bar :title="title" left-text="返回" left-arrow @click-left="goBack" v-show="showHeader"/>   
</div>
     
</template>
<script>
import { NavBar } from 'vant';
import { mapState } from 'vuex';

export default {
    name:"v-header",
    data(){
        return {
            title:"",
        };
    },
    computed: {
        showHeader:function(){
            let header=this.$store.getters.showHeader;
            this.title=this.$store.getters.titleHeader;
            return header
        }
    },
    methods: {
        goBack() {
            this.$router.back(-1);
        }
   },
   components: {
    [NavBar.name]:NavBar,
  }
}
</script>
