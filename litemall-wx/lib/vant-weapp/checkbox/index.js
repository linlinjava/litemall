import { VantComponent } from '../common/component';
import { addUnit } from '../common/utils';
function emit(target, value) {
    target.$emit('input', value);
    target.$emit('change', value);
}
VantComponent({
    field: true,
    relation: {
        name: 'checkbox-group',
        type: 'ancestor',
        linked(target) {
            this.parent = target;
        },
        unlinked() {
            this.parent = null;
        }
    },
    classes: ['icon-class', 'label-class'],
    props: {
        value: Boolean,
        disabled: Boolean,
        useIconSlot: Boolean,
        checkedColor: String,
        labelPosition: String,
        labelDisabled: Boolean,
        shape: {
            type: String,
            value: 'round'
        },
        iconSize: {
            type: null,
            observer: 'setSizeWithUnit'
        }
    },
    data: {
        sizeWithUnit: '20px'
    },
    methods: {
        emitChange(value) {
            if (this.parent) {
                this.setParentValue(this.parent, value);
            }
            else {
                emit(this, value);
            }
        },
        toggle() {
            const { disabled, value } = this.data;
            if (!disabled) {
                this.emitChange(!value);
            }
        },
        onClickLabel() {
            const { labelDisabled, disabled, value } = this.data;
            if (!disabled && !labelDisabled) {
                this.emitChange(!value);
            }
        },
        setParentValue(parent, value) {
            const parentValue = parent.data.value.slice();
            const { name } = this.data;
            const { max } = parent.data;
            if (value) {
                if (max && parentValue.length >= max) {
                    return;
                }
                if (parentValue.indexOf(name) === -1) {
                    parentValue.push(name);
                    emit(parent, parentValue);
                }
            }
            else {
                const index = parentValue.indexOf(name);
                if (index !== -1) {
                    parentValue.splice(index, 1);
                    emit(parent, parentValue);
                }
            }
        },
        setSizeWithUnit(size) {
            this.set({
                sizeWithUnit: addUnit(size)
            });
        },
    }
});
