export default {
  isAttached(element) {
    let currentNode = element.parentNode;
    while (currentNode) {
      if (currentNode.tagName === 'HTML') {
        return true;
      }
      if (currentNode.nodeType === 11) {
        return false;
      }
      currentNode = currentNode.parentNode;
    }
    return false;
  },

  getScrollLeft(element) {
    return 'scrollLeft' in element ? element.scrollLeft : element.pageXOffset;
  },

  getVisibleHeight(element) {
    return element === window
      ? element.innerHeight
      : element.getBoundingClientRect().height;
  },

  getVisibleWidth(element) {
    return element === window
      ? element.innerWidth
      : element.getBoundingClientRect().width;
  }
};
