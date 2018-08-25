Component({
  properties: {
    text: String,
    color: {
      type: String,
      value: '#fff'
    },
    backgroundColor: {
      type: String,
      value: '#e64340'
    },
    duration: {
      type: Number,
      value: 3000
    }
  },

  methods: {
    show() {
      const { duration } = this.data;

      clearTimeout(this.timer);
      this.setData({
        show: true
      });

      if (duration > 0 && duration !== Infinity) {
        this.timer = setTimeout(() => {
          this.hide();
        }, duration);
      }
    },

    hide() {
      clearTimeout(this.timer);
      this.setData({
        show: false
      });
    }
  }
});

const defaultOptions = {
  selector: '#van-notify',
  duration: 3000
};

function Notify(options = {}) {
  const pages = getCurrentPages();
  const ctx = pages[pages.length - 1];

  options = Object.assign({}, defaultOptions, parseParam(options));

  const el = ctx.selectComponent(options.selector);
  delete options.selector;

  if (el) {
    el.setData({
      ...options
    });
    el.show();
  }
}

function parseParam(params = '') {
  return typeof params === 'object' ? params : { text: params };
}

module.exports = Notify;
