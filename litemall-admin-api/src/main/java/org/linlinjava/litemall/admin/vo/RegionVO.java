package org.linlinjava.litemall.admin.vo;

import java.util.List;

public class RegionVO {
    private Integer id;
    private String name;
    private Byte type;
    private Integer code;

    private List<RegionVO> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<RegionVO> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setChildren(List<RegionVO> children) {
        this.children = children;
    }
}
