package org.linlinjava.litemall.admin.vo;

import java.util.List;

public class PermVo {
    private String id;
    private String label;
    private String api;
    private List<PermVo> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getApi() {
        return api;
    }

    public List<PermVo> getChildren() {
        return children;
    }

    public void setChildren(List<PermVo> children) {
        this.children = children;
    }

}
