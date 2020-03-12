import { VantComponent } from '../common/component';
import { transition } from '../mixins/transition';
VantComponent({
    classes: [
        'enter-class',
        'enter-active-class',
        'enter-to-class',
        'leave-class',
        'leave-active-class',
        'leave-to-class'
    ],
    mixins: [transition(false)],
    props: {
        round: Boolean,
        closeable: Boolean,
        customStyle: String,
        overlayStyle: String,
        transition: {
            type: String,
            observer: 'observeClass'
        },
        zIndex: {
            type: Number,
            value: 100
        },
        overlay: {
            type: Boolean,
            value: true
        },
        closeIcon: {
            type: String,
            value: 'cross'
        },
        closeIconPosition: {
            type: String,
            value: 'top-right'
        },
        closeOnClickOverlay: {
            type: Boolean,
            value: true
        },
        position: {
            type: String,
            value: 'center',
            observer: 'observeClass'
        },
        safeAreaInsetBottom: {
            type: Boolean,
            value: true
        },
        safeAreaInsetTop: {
            type: Boolean,
            value: false
        }
    },
    created() {
        this.observeClass();
    },
    methods: {
        onClickCloseIcon() {
            this.$emit('close');
        },
        onClickOverlay() {
            this.$emit('click-overlay');
            if (this.data.closeOnClickOverlay) {
                this.$emit('close');
            }
        },
        observeClass() {
            const { transition, position } = this.data;
            const updateData = {
                name: transition || position
            };
            if (transition === 'none') {
                updateData.duration = 0;
            }
            this.setData(updateData);
        }
    }
});
