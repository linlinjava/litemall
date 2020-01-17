import { addUnit, isDef } from '../common/utils';
import { VantComponent } from '../common/component';
import { button } from '../mixins/button';
import { openType } from '../mixins/open-type';
const FIT_MODE_MAP = {
    none: 'center',
    fill: 'scaleToFill',
    cover: 'aspectFill',
    contain: 'aspectFit'
};
VantComponent({
    mixins: [button, openType],
    classes: ['custom-class', 'loading-class', 'error-class', 'image-class'],
    props: {
        src: String,
        round: Boolean,
        width: {
            type: null,
            observer: 'setStyle'
        },
        height: {
            type: null,
            observer: 'setStyle'
        },
        radius: null,
        lazyLoad: Boolean,
        useErrorSlot: Boolean,
        useLoadingSlot: Boolean,
        showMenuByLongpress: Boolean,
        fit: {
            type: String,
            value: 'fill',
            observer: 'setMode'
        },
        showError: {
            type: Boolean,
            value: true
        },
        showLoading: {
            type: Boolean,
            value: true
        }
    },
    data: {
        error: false,
        loading: true
    },
    watch: {
        src() {
            this.setData({
                error: false,
                loading: true
            });
        }
    },
    mounted() {
        this.setMode();
        this.setStyle();
    },
    methods: {
        setMode() {
            this.setData({
                mode: FIT_MODE_MAP[this.data.fit],
            });
        },
        setStyle() {
            const { width, height, radius } = this.data;
            let style = '';
            if (isDef(width)) {
                style += `width: ${addUnit(width)};`;
            }
            if (isDef(height)) {
                style += `height: ${addUnit(height)};`;
            }
            if (isDef(radius)) {
                style += 'overflow: hidden;';
                style += `border-radius: ${addUnit(radius)};`;
            }
            this.setData({ style });
        },
        onLoad(event) {
            this.setData({
                loading: false
            });
            this.$emit('load', event.detail);
        },
        onError(event) {
            this.setData({
                loading: false,
                error: true
            });
            this.$emit('error', event.detail);
        },
        onClick(event) {
            this.$emit('click', event.detail);
        }
    }
});
