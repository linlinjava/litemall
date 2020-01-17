import { VantComponent } from '../common/component';
VantComponent({
    field: true,
    relation: {
        name: 'radio',
        type: 'descendant',
        linked(target) {
            this.children = this.children || [];
            this.children.push(target);
            this.updateChild(target);
        },
        unlinked(target) {
            this.children = this.children.filter((child) => child !== target);
        }
    },
    props: {
        value: {
            type: null,
            observer: 'updateChildren'
        },
        disabled: {
            type: Boolean,
            observer: 'updateChildren'
        }
    },
    methods: {
        updateChildren() {
            (this.children || []).forEach((child) => this.updateChild(child));
        },
        updateChild(child) {
            const { value, disabled } = this.data;
            child.setData({
                value,
                disabled: disabled || child.data.disabled
            });
        }
    }
});
