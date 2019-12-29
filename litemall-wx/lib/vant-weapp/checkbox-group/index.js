import { VantComponent } from '../common/component';
VantComponent({
    field: true,
    relation: {
        name: 'checkbox',
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
        max: Number,
        value: {
            type: Array,
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
                value: value.indexOf(child.data.name) !== -1,
                disabled: disabled || child.data.disabled
            });
        }
    }
});
