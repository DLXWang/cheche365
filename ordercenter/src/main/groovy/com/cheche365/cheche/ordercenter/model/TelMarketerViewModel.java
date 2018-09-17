package com.cheche365.cheche.ordercenter.model;

public class TelMarketerViewModel {
    private Long id;//internal user id
    private String name;//分配时间
    private String bindTel;//绑定座机号
    private String cno;//工号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBindTel() {
        return bindTel;
    }

    public void setBindTel(String bindTel) {
        this.bindTel = bindTel;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }
}
