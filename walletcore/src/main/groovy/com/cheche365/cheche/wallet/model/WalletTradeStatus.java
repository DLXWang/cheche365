package com.cheche365.cheche.wallet.model;

import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.wallet.repository.WalletTradeStatusRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mjg on 6/6/17.
 */
@Entity
public class WalletTradeStatus {
    private Long id;
    private String status;
    private String description;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum{
        public static WalletTradeStatus CREATE_1,FINISHED_2,FAIL_3,CANCELED_4,PROCESSING_5;
        public static List<WalletTradeStatus> ALL;


        static {
            ALL = (List<WalletTradeStatus>)RuntimeUtil.loadEnum(WalletTradeStatusRepository.class, WalletTradeStatus.class, Enum.class);
        }

        public static List<WalletTradeStatus> format(List<String> ids){
            List list = new ArrayList();
            for (int i = 0; i <ids.size(); i++ ) {
                list.add(ALL.get(Integer.valueOf(ids.get(i)) - 1));
            }
            return list;
        }

    }
}
