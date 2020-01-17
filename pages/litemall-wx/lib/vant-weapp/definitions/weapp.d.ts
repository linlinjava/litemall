/// <reference types="miniprogram-api-typings" />
export declare namespace Weapp {
    export interface FormField {
        data: {
            name: string;
            value: any;
        };
    }
    interface Target {
        id: string;
        tagName: string;
        dataset: {
            [key: string]: any;
        };
    }
    export interface Event {
        /**
         * 代表事件的类型。
         */
        type: string;
        /**
         * 页面打开到触发事件所经过的毫秒数。
         */
        timeStamp: number;
        /**
         * 触发事件的源组件。
         */
        target: Target;
        /**
         * 事件绑定的当前组件。
         */
        currentTarget: Target;
        /**
         * 额外的信息
         */
        detail: any;
    }
    interface Touch {
        /**
         * 触摸点的标识符
         */
        identifier: number;
        /**
         * 距离文档左上角的距离，文档的左上角为原点 ，横向为X轴，纵向为Y轴
         */
        pageX: number;
        /**
         * 距离文档左上角的距离，文档的左上角为原点 ，横向为X轴，纵向为Y轴
         */
        pageY: number;
        /**
         * 距离页面可显示区域（屏幕除去导航条）左上角距离，横向为X轴，纵向为Y轴
         */
        clientX: number;
        /**
         * 距离页面可显示区域（屏幕除去导航条）左上角距离，横向为X轴，纵向为Y轴
         */
        clientY: number;
    }
    export interface TouchEvent extends Event {
        touches: Array<Touch>;
        changedTouches: Array<Touch>;
    }
    /**
     * relation定义，miniprogram-api-typings缺少this定义
     */
    export interface RelationOption<Instance> {
        /** 目标组件的相对关系 */
        type: 'parent' | 'child' | 'ancestor' | 'descendant';
        /** 关系生命周期函数，当关系被建立在页面节点树中时触发，触发时机在组件attached生命周期之后 */
        linked?(this: Instance, target: WechatMiniprogram.Component.TrivialInstance): void;
        /** 关系生命周期函数，当关系在页面节点树中发生改变时触发，触发时机在组件moved生命周期之后 */
        linkChanged?(this: Instance, target: WechatMiniprogram.Component.TrivialInstance): void;
        /** 关系生命周期函数，当关系脱离页面节点树时触发，触发时机在组件detached生命周期之后 */
        unlinked?(this: Instance, target: WechatMiniprogram.Component.TrivialInstance): void;
        /** 如果这一项被设置，则它表示关联的目标节点所应具有的behavior，所有拥有这一behavior的组件节点都会被关联 */
        target?: string;
    }
    /**
     * obverser定义，miniprogram-api-typings缺少this定义
     */
    type Observer<Instance, T> = (this: Instance, newVal: T, oldVal: T, changedPath: Array<string | number>) => void;
    /**
     * watch定义
     */
    export interface WatchOption<Instance> {
        [name: string]: string | Observer<Instance, any>;
    }
    /**
     * methods定义，miniprogram-api-typings缺少this定义
     */
    export interface MethodOption<Instance> {
        [name: string]: (this: Instance, ...args: any[]) => any;
    }
    export interface ComputedOption<Instance> {
        [name: string]: (this: Instance) => any;
    }
    type PropertyType = StringConstructor | NumberConstructor | BooleanConstructor | ArrayConstructor | ObjectConstructor | FunctionConstructor | null;
    export interface PropertyOption {
        [name: string]: PropertyType | PropertyType[] | {
            /** 属性类型 */
            type: PropertyType | PropertyType[];
            /** 属性初始值 */
            value?: any;
            /** 属性值被更改时的响应函数 */
            observer?: string | Observer<WechatMiniprogram.Component.TrivialInstance, any>;
            /** 属性的类型（可以指定多个） */
            optionalTypes?: PropertyType[];
        };
    }
    export {};
}
