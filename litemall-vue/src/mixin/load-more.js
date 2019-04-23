export default {
  data() {
    return {
      pages: {
        perPage: 8,
        currPage: 1,
        pageCount: 1
      },
      loading: false,
      finished: false,
      isEmpty: false
    };
  },

  methods: {
    async resetInit() {
      this.resetData();
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
      const {
        pages: { pageCount, currPage }
      } = this;
      const finish = pageCount < currPage;
      if (finish) {
        this.isFinished();
      } else {
        this.setPages(await this.initData(true));
      }
      this.loading = false;
    },
    nextPage(pageCount = 1) {
      this.pages.currPage += 1;
      this.pages.pageCount = pageCount;
      this.loading = false;
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
      this.pages = {
        perPage: 8,
        currPage: 1,
        pageCount: 1
      };
      this.loading = true;
      this.finished = false;
      this.isEmpty = false;
    }
  }
};
