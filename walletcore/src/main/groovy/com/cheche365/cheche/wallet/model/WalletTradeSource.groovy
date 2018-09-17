package com.cheche365.cheche.wallet.model

import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.wallet.repository.WalletTradeSourceRepository

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Created by mjg on 6/6/17.
 */
@Entity
public class WalletTradeSource {
    private Long id
    private String source
    private String description


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id
    }

    public void setId(Long id) {
        this.id = id
    }

    public String getSource() {
        return source
    }

    public void setSource(String source) {
        this.source = source
    }

    public String getDescription() {
        return description
    }

    public void setDescription(String description) {
        this.description = description
    }

    public static class Enum {
        public
        static WalletTradeSource DALIYBACK_1, ACTIVE_REDPACKET_2, WITHDRAW_3, REBATE_TOA_4, TAX_FEE_5, ORDER_REBATE_6,
                                 REGISTER_REBATE_7, INVITE_CA_REBATE_8, CA_FIRST_ORDER_REBATE_9, NEXT__LEVEL_FIRST_ORDER_REBATE_10

        public static List<WalletTradeSource> ALL


        static {
            ALL = (List<WalletTradeSource>) RuntimeUtil.loadEnum(WalletTradeSourceRepository.class, WalletTradeSource.class, Enum.class)
        }

        public static List<WalletTradeSource> format(List<String> ids) {
            List list = new ArrayList()
            for (int i = 0; i < ids.size(); i++) {
                list.add(ALL.get(Integer.valueOf(ids.get(i)) - 1))
            }
            return list
        }

    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (o == null || !getClass().is(o.getClass())) return false
        WalletTradeSource tradeSource = (WalletTradeSource) o
        return id == tradeSource.id

    }

    @Override
    int hashCode() {
        return id.hashCode()
    }

}
