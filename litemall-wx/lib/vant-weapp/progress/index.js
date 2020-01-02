import { VantComponent } from '../common/component';
import { BLUE } from '../common/color';
import { addUnit } from '../common/utils';
VantComponent({
    props: {
        inactive: Boolean,
        percentage: Number,
        pivotText: String,
        pivotColor: String,
        trackColor: String,
        showPivot: {
            type: Boolean,
            value: true
        },
        color: {
            type: String,
            value: BLUE
        },
        textColor: {
            type: String,
            value: '#fff'
        },
        strokeWidth: {
            type: null,
            observer: 'setStrokeWidthUnit'
        }
    },
    data: {
        strokeWidthUnit: '4px'
    },
    methods: {
        setStrokeWidthUnit(val) {
            this.setData({
                strokeWidthUnit: addUnit(val)
            });
        }
    }
});
