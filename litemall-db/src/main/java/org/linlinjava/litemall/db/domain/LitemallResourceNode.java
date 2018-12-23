package org.linlinjava.litemall.db.domain;

import java.util.List;

public class LitemallResourceNode {
    private Integer id;
    private String routerText;
    private String routerUrl;
    private String pid;
    private String description;

    /**
     * 下一个节点
     */
    private List<LitemallResourceNode> next;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRouterText() {
        return routerText;
    }

    public void setRouterText(String routerText) {
        this.routerText = routerText;
    }

    public String getRouterUrl() {
        return routerUrl;
    }

    public void setRouterUrl(String routerUrl) {
        this.routerUrl = routerUrl;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LitemallResourceNode> getNext() {
        return next;
    }

    public void setNext(List<LitemallResourceNode> next) {
        this.next = next;
    }
}