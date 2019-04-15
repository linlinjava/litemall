<template>
	<div class="item_search">
		<form action="/search" @submit="disabledSubmit">
			<van-search placeholder="请输入商品名称" v-model="keyword" @search="enterSearch" autofocus/>
		</form>
		<div class="item_search_content">
			<div class="item_search_text clearfix">
				<div class="float-l">历史搜索</div>
				<div class="float-r" @click="clearHistory">
					<van-icon name="lajitong" style="font-size: 12px;margin-right: 3px" />
					清空历史记录
				</div>
			</div>
			<div class="item_search_history">
				<word-tag
					v-for="(his, i) in wordHistory"
					:key="i"
					@click="toSearchResult(his)"
				>{{his}}</word-tag>
			</div>
		</div>
	</div>
</template>

<script>
import { Search } from 'vant';
import SrarchTag from './search-tag';

export default {
  data() {
    return {
      keyword: '',
      focusStatus: true,
      wordHistory: []
    };
  },
  methods: {
    enterSearch() {
      const keyword = this.keyword;
      this.pushHistoryTolocal(keyword);
      this.toSearchResult(keyword);
    },
    toSearchResult(word) {
      this.keyword = word.trim();
      this.$router.push({
        name: 'search-result',
        query: { keyword: word.trim() }
      });
    },
    pushHistoryTolocal(keyword) {
      const wordHistory = this.wordHistory;
      const historyKeyWord = this.getKeyWordHistory();
      if (!!keyword.trim() && historyKeyWord.indexOf(keyword) < 0) {
        wordHistory.push(keyword);
        window.localStorage.setItem('keyword', wordHistory.join('|'));
      }
    },
    getKeyWordHistory() {
      const listWord = window.localStorage.getItem('keyword');
      return listWord ? listWord.split('|') : [];
    },
    clearHistory() {
      this.$dialog
        .confirm({
          message: '是否清空历史记录'
        })
        .then(() => {
          window.localStorage.setItem('keyword', '');
          this.wordHistory = [];
        });
    },
    disabledSubmit() {
      return false;
    }
  },
  activated() {
    this.wordHistory = this.getKeyWordHistory();
  },
  components: {
    [Search.name]: Search,
    [SrarchTag.name]: SrarchTag
  }
};
</script>


<style lang="scss" scoped>
.item_search {
  background-color: #fff;
}
.item_search_content {
  padding: 15px 10px 0;
}
.item_search_text {
  font-size: $font-size-normal;
  color: $font-color-gray;
  margin-bottom: 20px;
}

.item_search_history > span {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
