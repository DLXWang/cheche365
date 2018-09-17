package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*

/**
 * Created by mahong on 17/02/2017.
 */
@Entity
@JsonIgnoreProperties(["user", "disable"])
class BankCard {

    private Long id;
    private User user;
    private Bank bank;                     // 银行
    private String bankNo;                 // 银行卡号
    private String name;                   // 银行卡姓名
    private boolean disable;               //是否禁用

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_BANK_CARD_REF_USER", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    User getUser() {
        return user
    }

    void setUser(User user) {
        this.user = user
    }

    @ManyToOne
    @JoinColumn(name = "bank", foreignKey = @ForeignKey(name = "FK_BANK_CARD_REF_BANK", foreignKeyDefinition = "FOREIGN KEY (bank) REFERENCES bank(id)"))
    Bank getBank() {
        return bank
    }

    void setBank(Bank bank) {
        this.bank = bank
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getBankNo() {
        return bankNo
    }

    void setBankNo(String bankNo) {
        this.bankNo = bankNo
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean getDisable() {
        return disable
    }

    void setDisable(boolean disable) {
        this.disable = disable
    }

}
