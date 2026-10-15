/**
 * Copyright 2018 bejson.com
 */
package org.linlinjava.litemall.core.express.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 快递鸟物流查询响应，保留上游字段映射以兼容现有反序列化。
 * <p>Success 表示查询是否成功，Reason 提供失败原因；ShipperName 由本地配置补充。
 * @author bejson.com (原始模型生成来源)
 */
public class ExpressInfo {

    @JsonProperty("LogisticCode")
    /** 运单号，保留服务商返回的字符串。 */
    private String LogisticCode;
    @JsonProperty("ShipperCode")
    /** 服务商快递公司编码。 */
    private String ShipperCode;
    @JsonProperty("Traces")
    /** 物流轨迹节点；查询无结果时可能为空或 null。 */
    private List<Traces> Traces;
    @JsonProperty("State")
    /** 服务商物流状态原值，当前类不转换为本地枚举。 */
    private String State;
    @JsonProperty("EBusinessID")
    /** 响应关联的商户标识。 */
    private String EBusinessID;
    @JsonProperty("Success")
    /** 服务商业务查询结果，不能仅凭 HTTP 请求完成判断成功。 */
    private boolean Success;
    @JsonProperty("Reason")
    /** 服务商返回的失败或补充说明。 */
    private String Reason;

    /** 依据本地 vendors 配置补充的公司名称。 */

    private String ShipperName;

    public String getLogisticCode() {
        return LogisticCode;
    }

    public void setLogisticCode(String LogisticCode) {
        this.LogisticCode = LogisticCode;
    }

    public String getShipperCode() {
        return ShipperCode;
    }

    public void setShipperCode(String ShipperCode) {
        this.ShipperCode = ShipperCode;
    }

    public List<Traces> getTraces() {
        return Traces;
    }

    public void setTraces(List<Traces> Traces) {
        this.Traces = Traces;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
    }

    public boolean getSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String Reason) {
        this.Reason = Reason;
    }

    public String getShipperName() {
        return ShipperName;
    }

    public void setShipperName(String shipperName) {
        ShipperName = shipperName;
    }

    @Override
    public String toString() {
        return "ExpressInfo{" +
                "LogisticCode='" + LogisticCode + '\'' +
                ", ShipperCode='" + ShipperCode + '\'' +
                ", Traces=" + Traces +
                ", State='" + State + '\'' +
                ", EBusinessID='" + EBusinessID + '\'' +
                ", Success=" + Success +
                ", Reason=" + Reason +
                ", ShipperName='" + ShipperName + '\'' +
                '}';
    }
}