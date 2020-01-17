import { VantComponent } from '../common/component';
VantComponent({
    classes: [
        'active-class',
        'disabled-class',
    ],
    relation: {
        type: 'ancestor',
        name: 'sidebar',
        linked(target) {
            this.parent = target;
        }
    },
    props: {
        dot: Boolean,
        info: null,
        title: String,
        disabled: Boolean
    },
    methods: {
        onClick() {
            const { parent } = this;
            if (!parent || this.data.disabled) {
                return;
            }
            const index = parent.children.indexOf(this);
            parent.setActive(index).then(() => {
                this.$emit('click', index);
                parent.$emit('change', index);
            });
        },
        setActive(selected) {
            return this.setData({ selected });
        }
    }
});
