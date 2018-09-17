package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Bank
import com.cheche365.cheche.core.model.BankCard
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.BankCardRepository
import com.cheche365.cheche.core.repository.BankRepository
import com.cheche365.cheche.core.util.MaskUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2016/12/22 0022.
 */
@Service
class BankService {

    @Autowired
    private BankRepository bankRepository

    @Autowired
    private BankCardRepository bankCardRepository

    List<Bank> findAll() {
        return bankRepository.findAll()
    }

    List<BankCard> findByApplicantNameAndUser(Long quoteRecordId, User user) {
        List<BankCard> bankCard = bankCardRepository.findByApplicantNameAndUser(quoteRecordId, user)
//        if (!bankCard && bankCard.size() > 0) {
            for (BankCard bc : bankCard) {
                bc.setBankNo(MaskUtils.maskBankCardNo(bc.getBankNo()))
            }
//        }
        return bankCard
    }

    BankCard createBankCard(BankCard bankCard, User user,Channel channel) {
        if (!bankCard.bank || !bankCard.bank.id || !bankCard.bankNo || !bankCard.name) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "请输入完整的银行卡信息")
        }

        if(channel.isLevelAgent()&&user.name != bankCard.name){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID,"代理人用户名和银行卡姓名不一致")
        }

        bankCard.bank = bankRepository.findOne(bankCard.bank.id)
        BankCard existsBankCard = bankCardRepository.findFirstByBankAndBankNoAndNameAndUserAndDisable(bankCard.bank, bankCard.bankNo, bankCard.name, user, false)
        if (existsBankCard) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "当前用户下已有相同的银行卡")
        }
        bankCard.user = user
        return bankCardRepository.save(bankCard)
    }

    BankCard deleteBankCardById(Long id, User user) {
        BankCard bankCard = bankCardRepository.findFirstByIdAndUserAndDisable(id, user, false)
        if (!bankCard) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "银行卡不存在")
        }
        bankCard.setDisable(true)
        return bankCardRepository.save(bankCard)
    }

    BankCard findOrCreate(BankCard bankCard) {
        BankCard existsBankCard = bankCardRepository.findFirstByBankAndBankNoAndNameAndUserAndDisable(bankCard.bank, bankCard.bankNo, bankCard.name, bankCard.user, false)
        if (existsBankCard) {
            return existsBankCard
        }
        return bankCardRepository.save(bankCard)
    }

    BankCard findOne(Long id, User user) {
        BankCard bankCard = bankCardRepository.findFirstByIdAndUserAndDisable(id, user, false)
        if (!bankCard) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "银行卡不存在")
        }
        bankCard.bank = bankRepository.findOne(bankCard.bank.id)
        return bankCard
    }

    List<BankCard> findbyUser(User user, Channel channel) {
        List<BankCard> bankCard = bankCardRepository.findByUser(user)
        if (!bankCard && bankCard.size() <= 0) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "银行卡不存在")
        }
        for (BankCard bc : bankCard) {
            bc.setBankNo(MaskUtils.maskBankCardNo(bc.getBankNo()))
            bc.bank.assembleLogoUrl(channel)
        }
        // 将上一次提现银行卡排到第一位
        Long preBankCardId = bankCardRepository.findPreRemit(user, channel)
        if (preBankCardId) {
            List<Long> bankCardId = bankCard*.id
            for (int i = 0; i < bankCardId.size(); i++) {
                if (bankCardId.get(i) == preBankCardId) {
                    bankCard.swap(i, 0)
                    break
                }
            }
        }
        return bankCard
    }
}
