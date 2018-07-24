/**
 * Copyright 2018 bejson.com
 */
package org.linlinjava.litemall.core.express.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auto-generated: 2018-07-19 22:27:22
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Traces {

    @JsonProperty("AcceptStation")
    private String AcceptStation;
    @JsonProperty("AcceptTime")
    private String AcceptTime;

    public void setAcceptStation(String AcceptStation) {
        this.AcceptStation = AcceptStation;
    }

    public String getAcceptStation() {
        return AcceptStation;
    }

    public void setAcceptTime(String AcceptTime) {
        this.AcceptTime = AcceptTime;
    }

    public String getAcceptTime() {
        return AcceptTime;
    }

}