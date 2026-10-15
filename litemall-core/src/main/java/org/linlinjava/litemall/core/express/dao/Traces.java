/**
 * Copyright 2018 bejson.com
 */
package org.linlinjava.litemall.core.express.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 物流轨迹中的一个节点，描述某一时间发生的揽收、转运或签收事件。
 * <p>时间保留服务商原始字符串，当前模型不进行时区或日期格式转换。
 * @author bejson.com (原始模型生成来源)
 */
public class Traces {

    @JsonProperty("AcceptStation")
    /** 轨迹发生地点及事件说明。 */
    private String AcceptStation;
    @JsonProperty("AcceptTime")
    /** 服务商返回的事件时间字符串。 */
    private String AcceptTime;

    public String getAcceptStation() {
        return AcceptStation;
    }

    public void setAcceptStation(String AcceptStation) {
        this.AcceptStation = AcceptStation;
    }

    public String getAcceptTime() {
        return AcceptTime;
    }

    public void setAcceptTime(String AcceptTime) {
        this.AcceptTime = AcceptTime;
    }

}
