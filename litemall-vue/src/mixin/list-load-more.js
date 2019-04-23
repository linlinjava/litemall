export default {
  data() {
    return {
      pages: {
        perPage: 8,
        currPage: 1,
        pageCount: 1
      },
      items: [],
      loading: false,
      finished: false,
      isEmpty: false
    };
  },

  methods: {
    async resetInit() {
      this.resetData();
      // debugger;
      const page = await this.initData();
      this.$nextTick(() => {
        this.setPages(page);
      });
    },
    isFinished() {
      this.finished = true;
      this.loading = false;
    },
    async loadMore() {
      console.log('loadmore');
      const vm = this;
      if (vm.pages.pageCount < vm.pages.currPage) {
        vm.$toast({
          message: '没有更多了~',
          position: 'top'
        });
        vm.isFinished();
      } else {
        const page = await vm.initData(true);
        vm.nextPage(page.pageCount);
      }
      vm.loading = false;
    },
    nextPage(pageCount = 1) {
      this.pages.currPage += 1;
      this.pages.pageCount = pageCount;
    },
    setPages(page = {}) {
      this.isEmpty = page.totalCount === 0;
      if (page.totalCount <= this.pages.perPage) {
        // 不满一页
        this.isFinished();
      } else {
        // 下一页
        this.nextPage(page.pageCount);
      }
    },
    resetData() {
      this.items = [];
      this.pages = {
        perPage: 8,
        currPage: 1,
        pageCount: 1
      };
      this.loading = false;
      this.finished = false;
      this.isEmpty = false;
    }
  }
};
