'use strict';

Component({
  externalClasses: ['custom-class'],
  /**
   * 组件的属性列表
   * 用于组件自定义设置
   */
  properties: {
    // 颜色状态
    type: {
      type: String,
      value: ''
    },
    // 自定义颜色
    color: {
      type: String,
      value: ''
    },
    // 左侧内容
    leftText: {
      type: String,
      value: ''
    },
    // 右侧内容
    rightText: {
      type: String,
      value: ''
    }
  }
});