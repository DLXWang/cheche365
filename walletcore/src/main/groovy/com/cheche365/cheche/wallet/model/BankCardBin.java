package com.cheche365.cheche.wallet.model;

import javax.persistence.*;

/**
 * Created by mjg on 6/13/17.
 */
@Entity
public class BankCardBin {
    private Long id;
    private String cardbin;
    private String bankcode;
    private String bankname;
    private String cardname;
    private int cardkind;
    private int cardlength;
    private int status;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardbin() {
        return cardbin;
    }

    public void setCardbin(String cardbin) {
        this.cardbin = cardbin;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public int getCardkind() {
        return cardkind;
    }

    public void setCardkind(int cardkind) {
        this.cardkind = cardkind;
    }

    public int getCardlength() {
        return cardlength;
    }

    public void setCardlength(int cardlength) {
        this.cardlength = cardlength;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
