import { VantComponent } from '../common/component';
VantComponent({
    props: {
        show: Boolean,
        title: String,
        cancelText: String,
        description: String,
        round: {
            type: Boolean,
            value: true
        },
        zIndex: {
            type: Number,
            value: 100
        },
        actions: {
            type: Array,
            value: []
        },
        overlay: {
            type: Boolean,
            value: true
        },
        closeOnClickOverlay: {
            type: Boolean,
            value: true
        },
        closeOnClickAction: {
            type: Boolean,
            value: true
        },
        safeAreaInsetBottom: {
            type: Boolean,
            value: true
        }
    },
    methods: {
        onSelect(event) {
            const { index } = event.currentTarget.dataset;
            const item = this.data.actions[index];
            if (item && !item.disabled && !item.loading) {
                this.$emit('select', item);
                if (this.data.closeOnClickAction) {
                    this.onClose();
                }
            }
        },
        onCancel() {
            this.$emit('cancel');
        },
        onClose() {
            this.$emit('close');
        },
        onClickOverlay() {
            this.$emit('click-overlay');
            this.onClose();
        }
    }
});
