import { VantComponent } from '../common/component';
VantComponent({
    field: true,
    relation: {
        name: 'dropdown-menu',
        type: 'ancestor',
        linked(target) {
            this.parent = target;
            this.updateDataFromParent();
        },
        unlinked() {
            this.parent = null;
        }
    },
    props: {
        value: {
            type: null,
            observer: 'rerender'
        },
        title: {
            type: String,
            observer: 'rerender'
        },
        disabled: Boolean,
        titleClass: {
            type: String,
            observer: 'rerender'
        },
        options: {
            type: Array,
            value: [],
            observer: 'rerender'
        }
    },
    data: {
        transition: true,
        showPopup: false,
        showWrapper: false,
        displayTitle: ''
    },
    methods: {
        rerender() {
            wx.nextTick(() => {
                this.parent && this.parent.updateItemListData();
            });
        },
        updateDataFromParent() {
            if (this.parent) {
                const { overlay, duration, activeColor, closeOnClickOverlay, direction } = this.parent.data;
                this.setData({
                    overlay,
                    duration,
                    activeColor,
                    closeOnClickOverlay,
                    direction
                });
            }
        },
        onClickOverlay() {
            this.toggle();
            this.$emit('close');
        },
        onOptionTap(event) {
            const { option } = event.currentTarget.dataset;
            const { value } = option;
            const shouldEmitChange = this.data.value !== value;
            this.setData({ showPopup: false, value });
            setTimeout(() => {
                this.setData({ showWrapper: false });
            }, this.data.duration || 0);
            this.rerender();
            if (shouldEmitChange) {
                this.$emit('change', value);
            }
        },
        toggle(show, options = {}) {
            const { showPopup, duration } = this.data;
            if (show == null) {
                show = !showPopup;
            }
            if (show === showPopup) {
                return;
            }
            if (!show) {
                const time = options.immediate ? 0 : duration;
                this.setData({ transition: !options.immediate, showPopup: show });
                setTimeout(() => {
                    this.setData({ showWrapper: false });
                }, time);
                this.rerender();
                return;
            }
            this.parent.getChildWrapperStyle().then((wrapperStyle = '') => {
                this.setData({
                    transition: !options.immediate,
                    showPopup: show,
                    wrapperStyle,
                    showWrapper: true
                });
                this.rerender();
            });
        }
    }
});
