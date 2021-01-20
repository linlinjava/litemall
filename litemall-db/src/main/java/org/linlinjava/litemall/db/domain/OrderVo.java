package org.linlinjava.litemall.db.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderVo {
    private Integer id;
    private String orderSn;
    private Integer orderStatus;
    private BigDecimal actualPrice;
    private BigDecimal integralPrice;
    private BigDecimal freightPrice;
    private BigDecimal orderPrice;
    private LocalDateTime addTime;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private String consignee;
    private String address;
    private String mobile;
    private String shipChannel;
    private String shipSn;
    private String message;
    private LocalDateTime payTime;
    private List<OrderGoodsVo> goodsVoList;

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public BigDecimal getIntegralPrice() {
        return integralPrice;
    }

    public void setIntegralPrice(BigDecimal integralPrice) {
        this.integralPrice = integralPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShipChannel() {
        return shipChannel;
    }

    public void setShipChannel(String shipChannel) {
        this.shipChannel = shipChannel;
    }

    public String getShipSn() {
        return shipSn;
    }

    public void setShipSn(String shipSn) {
        this.shipSn = shipSn;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(BigDecimal freightPrice) {
        this.freightPrice = freightPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<OrderGoodsVo> getGoodsVoList() {
        return goodsVoList;
    }

    public void setGoodsVoList(List<OrderGoodsVo> goodsVoList) {
        this.goodsVoList = goodsVoList;
    }

    @Override
    public String toString() {
        return "OrderVo{" +
                "id=" + id +
                ", orderSn='" + orderSn + '\'' +
                ", orderStatus=" + orderStatus +
                ", actualPrice=" + actualPrice +
                ", integralPrice=" + integralPrice +
                ", freightPrice=" + freightPrice +
                ", orderPrice=" + orderPrice +
                ", addTime=" + addTime +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", consignee='" + consignee + '\'' +
                ", address='" + address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", shipChannel='" + shipChannel + '\'' +
                ", shipSn='" + shipSn + '\'' +
                ", message='" + message + '\'' +
                ", payTime=" + payTime +
                ", goodsVoList=" + goodsVoList +
                '}';
    }
}
