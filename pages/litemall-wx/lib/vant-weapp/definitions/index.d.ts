/// <reference types="miniprogram-api-typings" />
import { Weapp } from './weapp';
declare type RecordToAny<T> = {
    [K in keyof T]: any;
};
export declare type CombinedComponentInstance<Data, Props, Methods> = Methods & WechatMiniprogram.Component.TrivialInstance & Weapp.FormField & {
    data: Data & RecordToAny<Props>;
};
export interface VantComponentOptions<Data, Props, Methods, Instance> {
    data?: Data;
    field?: boolean;
    classes?: string[];
    mixins?: string[];
    props?: Props & Weapp.PropertyOption;
    watch?: Weapp.WatchOption<Instance>;
    relation?: Weapp.RelationOption<Instance> & {
        name: string;
    };
    relations?: {
        [componentName: string]: Weapp.RelationOption<Instance>;
    };
    methods?: Methods & Weapp.MethodOption<Instance>;
    beforeCreate?: (this: Instance) => void;
    created?: (this: Instance) => void;
    mounted?: (this: Instance) => void;
    destroyed?: (this: Instance) => void;
}
export {};
