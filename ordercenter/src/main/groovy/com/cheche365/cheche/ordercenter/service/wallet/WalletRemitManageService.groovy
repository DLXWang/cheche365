package com.cheche365.cheche.ordercenter.service.wallet

import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.FileUtil
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.WalletRemitUploadHistory
import com.cheche365.cheche.manage.common.repository.WalletRemitUploadHistoryRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.util.ExcelUtil
import com.cheche365.cheche.wallet.model.Wallet
import com.cheche365.cheche.wallet.model.WalletRemitRecord
import com.cheche365.cheche.wallet.model.WalletTrade
import com.cheche365.cheche.wallet.model.WalletTradeStatus
import com.cheche365.cheche.wallet.repository.WalletRemitRepository
import com.cheche365.cheche.wallet.repository.WalletRepository
import com.cheche365.cheche.wallet.repository.WalletTradeRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * Created by yinJianBin on 2018/4/5.
 */
@Service
@Slf4j
class WalletRemitManageService extends BaseService {

    @Autowired
    private ResourceService resourceService
    @Autowired
    private InternalUserManageService internalUserManageService
    @Autowired
    private WalletRemitUploadHistoryRepository walletRemitUploadHistoryRepository
    @Autowired
    private WalletRemitRepository walletRemitRepository
    @Autowired
    private WalletTradeRepository walletTradeRepository
    @Autowired
    private WalletRepository walletRepository

    @Transactional
    def uploadFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename()
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().walletRemitReport)
        basePath = basePath + LocalDate.now().toString("yyyyMMdd") + "/" + RandomStringUtils.randomAlphanumeric(4)
        if (!new File(basePath).exists()) {
            if (!new File(basePath).mkdirs()) throw new RuntimeException("创建存储文件目录失败")
        }
        String newFilePath = basePath + File.separator + originalFileName
        //保存文件
        FileUtil.writeFile(newFilePath, file.getBytes())
        //保存文件上传历史
        saveUploadHistory(basePath, originalFileName)
        //处理数据
        updateDate(file)
    }

    Object updateDate(MultipartFile file) {
        def workbook = ExcelUtil.uploadByFile(file)
        def sheet = workbook.getSheetAt(0)
        if (sheet.lastRowNum < 1) {
            throw new FileUploadException("该文件无待更新内容 !")
        }
        def rowIterator = sheet.rowIterator()
        rowIterator.next()
        def now = new Date()
        rowIterator.each { row ->
            if (StringUtils.isBlank(ExcelUtil.getCellValue(row.getCell(0)))) {
                throw new FileUploadException("商户订单号不能为空")
            }
            if (StringUtils.isBlank(ExcelUtil.getCellValue(row.getCell(7)))) {
                throw new FileUploadException("订单状态不能为空")
            }
            def merchantSeqNo = ExcelUtil.getCellValue(row.getCell(0))
            def orderStatus = ExcelUtil.getCellValue(row.getCell(7))
            List<WalletRemitRecord> recordList = walletRemitRepository.findByMerchantSeqNo(merchantSeqNo)
            recordList.each {
                WalletTrade walletTrade = walletTradeRepository.findByRequestNo(it.requestNo)
                if (it.getStatus().id == WalletTradeStatus.Enum.PROCESSING_5.id) {
                    Wallet wallet = walletRepository.findOne(walletTrade.walletId)
                    if ("已打款" == orderStatus) {
                        //更新remit结果及状态
                        it.setResponseCode("0000")
                        it.setResponseMsg("交易成功")
                        it.setStatus(WalletTradeStatus.Enum.FINISHED_2)

                        //更新wallet_trade状态
                        walletTrade.setStatus(WalletTradeStatus.Enum.FINISHED_2)

                        //更新wallet的金额
                        wallet.setUnbalance(wallet.getUnbalance().subtract(walletTrade.getAmount()))
                    } else {
                        //更新remit状态
                        it.setResponseMsg(orderStatus)
                        it.setStatus(WalletTradeStatus.Enum.FAIL_3)

                        //更新walletTrade状态
                        walletTrade.setStatus(WalletTradeStatus.Enum.FAIL_3)

                        //恢复钱包余额
                        wallet.setBalance(wallet.getBalance().add(walletTrade.getAmount()))
                        wallet.setUnbalance(wallet.getUnbalance().subtract(walletTrade.getAmount()))
                    }
                    it.setResponseTime(now)
                    it.setUpdateTime(now)
                    walletRemitRepository.save(it)
                    walletTrade.setUpdateTime(now)
                    walletTradeRepository.save(walletTrade)
                    wallet.setUpdateTime(now)
                    walletRepository.save(wallet)
                } else {
                    throw new FileUploadException("[${merchantSeqNo}]提现操作已经回传打款结果，不可重复上传打款状态")
                }
            }
        }


    }

    Object saveUploadHistory(String basePath, String fileName) {
        WalletRemitUploadHistory history = walletRemitUploadHistoryRepository.findFirstByFileName(fileName)
        if (history) {
            throw new FileUploadException("文件[${fileName}]已经上传过,请检查数据并重命名文件")
        }
        history = new WalletRemitUploadHistory(
                createTime: new Date(),
                updateTime: new Date(),
                fileName: fileName,
                filePath: basePath + File.separator + fileName,
                operator: internalUserManageService.getCurrentInternalUser(),
                status: 1
        )
        walletRemitUploadHistoryRepository.save(history)
    }

    Page<WalletRemitUploadHistory> getUploadHistory(int currentPage, int pageSize) {
        Pageable pageable = buildPageable(currentPage, pageSize, Sort.Direction.DESC, "id")
        return walletRemitUploadHistoryRepository.findAll(new Specification<WalletRemitUploadHistory>() {
            @Override
            Predicate toPredicate(Root<WalletRemitUploadHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return query.getRestriction()
            }
        }, pageable)
    }
}
