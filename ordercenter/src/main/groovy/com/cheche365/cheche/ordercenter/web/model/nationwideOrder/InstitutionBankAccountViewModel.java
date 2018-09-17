package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.core.model.InstitutionBankAccount;
import com.cheche365.cheche.core.model.InstitutionBankAccountTemp;

/**
 * Created by sunhuazhong on 2015/11/16.
 */
public class InstitutionBankAccountViewModel {
    private Long id;
    private String bank;//开户行
    private String accountName;//开户名
    private String accountNo;//帐号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public static InstitutionBankAccountViewModel createViewModel(InstitutionBankAccount institutionBankAccount) {
        if (null == institutionBankAccount) {
            return null;
        }
        InstitutionBankAccountViewModel viewModel = new InstitutionBankAccountViewModel();
        viewModel.setId(institutionBankAccount.getId());
        viewModel.setBank(institutionBankAccount.getBank());
        viewModel.setAccountName(institutionBankAccount.getAccountName());
        viewModel.setAccountNo(institutionBankAccount.getAccountNo());
        return viewModel;
    }


    public static InstitutionBankAccountViewModel createViewModel(InstitutionBankAccountTemp institutionBankAccount) {
        if (null == institutionBankAccount) {
            return null;
        }
        InstitutionBankAccountViewModel viewModel = new InstitutionBankAccountViewModel();
        viewModel.setId(institutionBankAccount.getId());
        viewModel.setBank(institutionBankAccount.getBank());
        viewModel.setAccountName(institutionBankAccount.getAccountName());
        viewModel.setAccountNo(institutionBankAccount.getAccountNo());
        return viewModel;
    }
}
