import { debounce } from 'lodash';
import scroll from '@/utils/scroll';

const CONTEXT = '$scrollArrow';
const OFFSET = 30;

// 绑定事件
function startBind(el) {
  const context = el[CONTEXT];

  context.vm.$nextTick(() => {
    if (scroll.isAttached(el)) {
      doBindEvent.call(el[CONTEXT]);
    }
  });
}

// 绑定事件到元素上
// 读取基本的控制变量
function doBindEvent() {
  if (this.el[CONTEXT].binded) {
    return;
  }
  this.el[CONTEXT].binded = true;

  this.scrollEventListener = debounce(handleScrollEvent.bind(this), 100);
  //  this.scrollEventTarget = this.el;

  //  var disabledExpr = this.el.getAttribute('waterfall-disabled');
  //  var disabled = false;
  //  if (disabledExpr) {
  //    this.vm.$watch(disabledExpr, (value) => {
  //      this.disabled = value;
  //      this.scrollEventListener();
  //    });
  //    disabled = Boolean(this.vm[disabledExpr]);
  //  }
  //  this.disabled = disabled;

  const offset = this.el.getAttribute('scroll-offset');
  this.offset = Number(offset) || OFFSET;
  this.el.addEventListener('scroll', this.scrollEventListener);

  //	this.scrollEventListener();
}

// 处理滚动函数
function handleScrollEvent() {
  const element = this.el;
  // 已被禁止的滚动处理
  //	if (this.disabled) return;

  const targetScrollLeft = scroll.getScrollLeft(element);
  const targetVisibleWidth = scroll.getVisibleWidth(element);
  // 滚动元素可视区域下边沿到滚动元素元素最顶上 距离
  const targetRight = targetScrollLeft + targetVisibleWidth;
  // 如果无元素高度，考虑为元素隐藏，直接返回
  if (!targetVisibleWidth) return;

  // 判断是否到了最右边
  const isRightOver = element.scrollWidth - targetRight < this.offset;

  // 判断是否到了最左边
  const isLeftOver = targetScrollLeft < this.offset;

  this.cb &&
    this.cb({
      target: element,
      isRightOver,
      isLeftOver
    });

  //	// 判断是否到了顶
  //	let needLoadMoreToUpper =  targetScrollTop < this.offset;
  //	if (needLoadMoreToUpper) {
  //		this.cb.upper && this.cb.upper({
  //			target: scrollEventTarget,
  //			top: targetScrollTop
  //		});
  //	}
}

// 确认何时绑事件监听函数
function doCheckStartBind(el) {
  const context = el[CONTEXT];

  if (context.vm._isMounted) {
    startBind(el);
  } else {
    context.vm.$on('hook:mounted', () => {
      startBind(el);
    });
  }
}

export default {
  bind(el, binding, vnode) {
    if (!el[CONTEXT]) {
      el[CONTEXT] = {
        el,
        vm: vnode.context,
        cb: {}
      };
    }
    el[CONTEXT].cb = binding.value;

    doCheckStartBind(el);
  },
  update(el) {
    const context = el[CONTEXT];
    context.scrollEventListener && context.scrollEventListener();
  }
};
