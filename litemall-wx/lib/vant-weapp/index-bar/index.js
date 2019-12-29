import { VantComponent } from '../common/component';
import { GREEN } from '../common/color';
const indexList = () => {
    const indexList = [];
    const charCodeOfA = 'A'.charCodeAt(0);
    for (let i = 0; i < 26; i++) {
        indexList.push(String.fromCharCode(charCodeOfA + i));
    }
    return indexList;
};
VantComponent({
    relation: {
        name: 'index-anchor',
        type: 'descendant',
        linked() {
            this.updateData();
        },
        linkChanged() {
            this.updateData();
        },
        unlinked() {
            this.updateData();
        }
    },
    props: {
        sticky: {
            type: Boolean,
            value: true
        },
        zIndex: {
            type: Number,
            value: 1
        },
        highlightColor: {
            type: String,
            value: GREEN
        },
        scrollTop: {
            type: Number,
            value: 0,
            observer: 'onScroll'
        },
        stickyOffsetTop: {
            type: Number,
            value: 0
        },
        indexList: {
            type: Array,
            value: indexList()
        }
    },
    data: {
        activeAnchorIndex: null,
        showSidebar: false
    },
    methods: {
        updateData() {
            this.timer && clearTimeout(this.timer);
            this.timer = setTimeout(() => {
                this.children = this.getRelationNodes('../index-anchor/index');
                this.setData({
                    showSidebar: !!this.children.length
                });
                this.setRect().then(() => {
                    this.onScroll();
                });
            }, 0);
        },
        setRect() {
            return Promise.all([
                this.setAnchorsRect(),
                this.setListRect(),
                this.setSiderbarRect()
            ]);
        },
        setAnchorsRect() {
            return Promise.all(this.children.map(anchor => (anchor.getRect('.van-index-anchor-wrapper').then((rect) => {
                Object.assign(anchor, {
                    height: rect.height,
                    top: rect.top + this.data.scrollTop
                });
            }))));
        },
        setListRect() {
            return this.getRect('.van-index-bar').then((rect) => {
                Object.assign(this, {
                    height: rect.height,
                    top: rect.top + this.data.scrollTop
                });
            });
        },
        setSiderbarRect() {
            return this.getRect('.van-index-bar__sidebar').then(res => {
                this.sidebar = {
                    height: res.height,
                    top: res.top
                };
            });
        },
        setDiffData({ target, data }) {
            const diffData = {};
            Object.keys(data).forEach(key => {
                if (target.data[key] !== data[key]) {
                    diffData[key] = data[key];
                }
            });
            if (Object.keys(diffData).length) {
                target.setData(diffData);
            }
        },
        getAnchorRect(anchor) {
            return anchor.getRect('.van-index-anchor-wrapper').then((rect) => ({
                height: rect.height,
                top: rect.top
            }));
        },
        getActiveAnchorIndex() {
            const { children } = this;
            const { sticky, scrollTop, stickyOffsetTop } = this.data;
            for (let i = this.children.length - 1; i >= 0; i--) {
                const preAnchorHeight = i > 0 ? children[i - 1].height : 0;
                const reachTop = sticky ? preAnchorHeight + stickyOffsetTop : 0;
                if (reachTop + scrollTop >= children[i].top) {
                    return i;
                }
            }
            return -1;
        },
        onScroll() {
            const { children = [] } = this;
            if (!children.length) {
                return;
            }
            const { sticky, stickyOffsetTop, zIndex, highlightColor, scrollTop } = this.data;
            const active = this.getActiveAnchorIndex();
            this.setDiffData({
                target: this,
                data: {
                    activeAnchorIndex: active
                }
            });
            if (sticky) {
                let isActiveAnchorSticky = false;
                if (active !== -1) {
                    isActiveAnchorSticky = children[active].top <= stickyOffsetTop + scrollTop;
                }
                children.forEach((item, index) => {
                    if (index === active) {
                        let wrapperStyle = '';
                        let anchorStyle = `
              color: ${highlightColor};
            `;
                        if (isActiveAnchorSticky) {
                            wrapperStyle = `
                height: ${children[index].height}px;
              `;
                            anchorStyle = `
                position: fixed;
                top: ${stickyOffsetTop}px;
                z-index: ${zIndex};
                color: ${highlightColor};
              `;
                        }
                        this.setDiffData({
                            target: item,
                            data: {
                                active: true,
                                anchorStyle,
                                wrapperStyle
                            }
                        });
                    }
                    else if (index === active - 1) {
                        const currentAnchor = children[index];
                        const currentOffsetTop = currentAnchor.top;
                        const targetOffsetTop = index === children.length - 1
                            ? this.top
                            : children[index + 1].top;
                        const parentOffsetHeight = targetOffsetTop - currentOffsetTop;
                        const translateY = parentOffsetHeight - currentAnchor.height;
                        const anchorStyle = `
              position: relative;
              transform: translate3d(0, ${translateY}px, 0);
              z-index: ${zIndex};
              color: ${highlightColor};
            `;
                        this.setDiffData({
                            target: item,
                            data: {
                                active: true,
                                anchorStyle
                            }
                        });
                    }
                    else {
                        this.setDiffData({
                            target: item,
                            data: {
                                active: false,
                                anchorStyle: '',
                                wrapperStyle: '',
                            }
                        });
                    }
                });
            }
        },
        onClick(event) {
            this.scrollToAnchor(event.target.dataset.index);
        },
        onTouchMove(event) {
            const sidebarLength = this.children.length;
            const touch = event.touches[0];
            const itemHeight = this.sidebar.height / sidebarLength;
            let index = Math.floor((touch.clientY - this.sidebar.top) / itemHeight);
            if (index < 0) {
                index = 0;
            }
            else if (index > sidebarLength - 1) {
                index = sidebarLength - 1;
            }
            this.scrollToAnchor(index);
        },
        onTouchStop() {
            this.scrollToAnchorIndex = null;
        },
        scrollToAnchor(index) {
            if (typeof index !== 'number' || this.scrollToAnchorIndex === index) {
                return;
            }
            this.scrollToAnchorIndex = index;
            const anchor = this.children.filter(item => item.data.index === this.data.indexList[index])[0];
            this.$emit('select', anchor.data.index);
            anchor && wx.pageScrollTo({
                duration: 0,
                scrollTop: anchor.top
            });
        }
    }
});
