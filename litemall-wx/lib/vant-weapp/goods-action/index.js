import { VantComponent } from '../common/component';
VantComponent({
    relation: {
        type: 'descendant',
        name: 'goods-action-button',
        linked(child) {
            this.children.push(child);
        },
        unlinked(child) {
            this.children = this.children.filter((item) => item !== child);
        }
    },
    beforeCreate() {
        this.children = [];
    },
    props: {
        safeAreaInsetBottom: {
            type: Boolean,
            value: true
        }
    }
});
