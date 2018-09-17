package com.cheche365.cheche.ordercenter.service.wallet;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Bank;
import com.cheche365.cheche.core.model.BankCard;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.common.util.MobileUtil;
import com.cheche365.cheche.ordercenter.model.WalletQuery;
import com.cheche365.cheche.ordercenter.web.model.user.UserViewModel;
import com.cheche365.cheche.ordercenter.web.model.wallet.WalletViewModel;
import com.cheche365.cheche.wallet.model.Wallet;
import com.cheche365.cheche.wallet.model.WalletTrade;
import com.cheche365.cheche.wallet.repository.WalletRepository;
import com.cheche365.cheche.wallet.repository.WalletTradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wangshaobin on 2017/5/3.
 */
@Service
public class WalletManagerService extends BaseService {

    private static final BigDecimal DEFAULT_VALUE = new BigDecimal(0);

    private static final String DEFAULT_STR = "9999999999";

    private static final Long DEFAULT_LONG = 9999999999L;

    @Autowired
    private WalletTradeRepository walletTradeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WalletRepository walletRepository;

    /**
     * 钱包列表
     **/
    public Page<WalletViewModel> findWallets(WalletQuery walletQuery) {
        Page<Object[]> tradePage = findWalletMobileAmountWithLatestTrade(walletQuery);
        return setWalletViewData(tradePage, walletQuery);
    }

    /**
     * 获取钱包列表处的入账和提现金额
     **/
    public WalletViewModel InAndOutAmount(WalletQuery walletQuery) {
        WalletViewModel viewModel = new WalletViewModel();
        CriteriaQuery<Object[]> criteriaQuery = findWalletAmountQuery(walletQuery);
        Query query = entityManager.createQuery(criteriaQuery);
        List<Object[]> currentView = query.getResultList();
        if (!CollectionUtils.isEmpty(currentView)) {
            Object[] obj = currentView.get(0);
            viewModel.setInAmount(StringUtil.objToString(obj[0]));
            viewModel.setOutAmount(StringUtil.objToString(obj[1]));
        }
        return viewModel;
    }

    /**
     * 获取钱包列表处的总余额
     **/
    public String balance(WalletQuery walletQuery) {
        Map param = setWalletParamsFromQuery(walletQuery);
        List<BigDecimal> balanceList = walletRepository.findWalletBalance((Integer) param.get("isNullMobile"), param.get("mobile").toString(), (Integer) param.get("isNullTradeNo"), param.get("tradeNo").toString(), (Integer) param.get("isNullUpdateTime"), (Date) param.get("startTime"), (Date) param.get("endTime"), (Integer) param.get("isNullChannel"), (Long) param.get("channel"));
        Double balance = 0.0;
        if (!CollectionUtils.isEmpty(balanceList))
            for (BigDecimal obj : balanceList)
                balance += Double.parseDouble(obj != null ? obj.toString() : "0.0");
        return balance.toString();
    }

    /**
     * 获取用户信息
     **/
    public UserViewModel getWalletUserInfo(Long walletId) {
        UserViewModel model = new UserViewModel();
        List<Object[]> list = walletRepository.findUserInfoByWallet(walletId);
        if (!CollectionUtils.isEmpty(list)) {
            Object[] obj = list.get(0);
            model.setMobile(StringUtil.defaultNullStr(obj[0]));
            model.setBalance(StringUtil.objToString(obj[1]));
            model.setRegChannel(StringUtil.defaultNullStr(obj[2]));
            model.setOutAmount(StringUtil.objToString(obj[3]));
        }
        return model;
    }

    /**
     * 获取银行卡信息
     **/
    public Page<WalletViewModel> findBankCardList(WalletQuery walletQuery) {
        CriteriaQuery<Object[]> criteriaQuery = findBankCardListQuery(walletQuery);
        Query query = entityManager.createQuery(criteriaQuery);
        int totals = query.getResultList().size();
        List<Object[]> currentView = query.setFirstResult((walletQuery.getCurrentPage() - 1) * walletQuery.getPageSize())
                .setMaxResults(walletQuery.getPageSize()).getResultList();
        Page<Object[]> page = new PageImpl<Object[]>(currentView, new PageRequest(walletQuery.getCurrentPage() - 1, walletQuery.getPageSize()), totals);
        return setBankInfoToViewModel(page, walletQuery);
    }

    /**
     * 获取交易信息
     **/
    public Page<WalletViewModel> findTransactionList(WalletQuery walletQuery) {
        int isNullType = 1;
        int isNullSource = 1;
        int isNullStatus = 1;
        int isNullPlatform = 1;
        int isNullTime = 1;
        Date now = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        int type = 2;
        List sources = Arrays.asList(DEFAULT_LONG);
        List statuses = Arrays.asList(DEFAULT_LONG);
        List platforms = Arrays.asList(DEFAULT_LONG);
        Date startTime = now;
        Date endTime = now;
        if (!StringUtil.isNull(walletQuery.getType())) {
            isNullType = 0;
            type = Integer.parseInt(walletQuery.getType());
        }
        if (walletQuery.getSources() != null && walletQuery.getSources().length > 0) {
            isNullSource = 0;
            sources = Arrays.asList(walletQuery.getSources());
        }
        if (walletQuery.getStatuses() != null && walletQuery.getStatuses().length > 0) {
            isNullStatus = 0;
            statuses = Arrays.asList(walletQuery.getStatuses());
        }
        if (walletQuery.getPlatforms() != null && walletQuery.getPlatforms().length > 0) {
            isNullPlatform = 0;
            platforms = Arrays.asList(walletQuery.getPlatforms());
        }
        if (!StringUtil.isNull(walletQuery.getStartTime()) && !StringUtil.isNull(walletQuery.getEndTime())) {
            isNullTime = 0;
            Map<String, Date> timeMap = getTimeFromQuery(walletQuery);
            startTime = timeMap.get("startTime");
            endTime = timeMap.get("endTime");
        }
        Long walletId = walletQuery.getWalletId();
        //1、获取总数量
        Long count = walletTradeRepository.findTransactionCount(walletId, isNullType, type, isNullSource,
                sources, isNullStatus, statuses, isNullPlatform, platforms, isNullTime, startTime, endTime);
        int startIndex = (walletQuery.getCurrentPage() - 1) * walletQuery.getPageSize();
        int endIndex = walletQuery.getPageSize();
        //2、获取分页的数据集
        List<Object[]> list = walletTradeRepository.findTransactionList(walletId, isNullType, type, isNullSource,
                sources, isNullStatus, statuses, isNullPlatform, platforms, isNullTime, startTime, endTime, startIndex, endIndex);
        //3、进行数据的封装ViewModel
        List<WalletViewModel> modelList = setTransactionInfoToViewModel(list);
        return new PageImpl<WalletViewModel>(modelList, new PageRequest(walletQuery.getCurrentPage() - 1, walletQuery.getPageSize()), count);
    }

    /**
     * 获取交易明细处的提现和入账金额
     **/
    public WalletViewModel tradeAmount(WalletQuery walletQuery) {
        WalletViewModel viewModel = new WalletViewModel();
        CriteriaQuery<Object[]> criteriaQuery = findTradeAmountQuery(walletQuery);
        Query query = entityManager.createQuery(criteriaQuery);
        List<Object[]> currentView = query.getResultList();
        if (!CollectionUtils.isEmpty(currentView)) {
            Object[] obj = currentView.get(0);
            viewModel.setInAmount(StringUtil.objToString(obj[0]));
            viewModel.setOutAmount(StringUtil.objToString(obj[1]));
        }
        return viewModel;
    }

    /**
     * mix findWalletMobileAndAmount and findTradeLatestInfo in findWalletMobileAmountWithLatestTrade
     *
     * @author zhangtc
     */
    private Page<Object[]> findWalletMobileAmountWithLatestTrade(WalletQuery walletQuery) {
        Map param = setWalletParamsFromQuery(walletQuery);
        Long count = walletRepository.countWallet((Integer) param.get("isNullMobile"), param.get("mobile").toString(), (Integer) param.get("isNullTradeNo"), param.get("tradeNo").toString(), (Integer) param.get("isNullUpdateTime"), (Date) param.get("startTime"), (Date) param.get("endTime"), (Integer) param.get("isNullChannel"), (Long) param.get("channel"));
        int startIndex = (walletQuery.getCurrentPage() - 1) * walletQuery.getPageSize();
        int endIndex = walletQuery.getPageSize();
        //2、获取分页的数据集
        List<Object[]> list = walletRepository.findWalletAndLatestTrade((Integer) param.get("isNullMobile"), param.get("mobile").toString(), (Integer) param.get("isNullTradeNo"), param.get("tradeNo").toString(), (Integer) param.get("isNullUpdateTime"), (Date) param.get("startTime"), (Date) param.get("endTime"), endIndex, startIndex, (Integer) param.get("isNullChannel"), (Long) param.get("channel"));
        return new PageImpl<Object[]>(list, new PageRequest(walletQuery.getCurrentPage() - 1, walletQuery.getPageSize()), count);
    }

    private CriteriaQuery<Object[]> findWalletAmountQuery(WalletQuery walletQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class);
        Root<Wallet> root = criteriaQuery.from(Wallet.class);
        Root<WalletTrade> tradeRoot = criteriaQuery.from(WalletTrade.class);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get("id"), tradeRoot.get("walletId")));
        String word = walletQuery.getKeyword();
        if (!StringUtil.isNull(word)) {
            int keyType = walletQuery.getKeyType();
            if (keyType == 1)
                predicateList.add(cb.equal(root.get("mobile"), word));
            else if (keyType == 2)
                predicateList.add(cb.equal(tradeRoot.get("tradeNo"), word));
        }
        if (!StringUtil.isNull(walletQuery.getStartTime()) && !StringUtil.isNull(walletQuery.getEndTime())) {
            Map<String, Date> timeMap = getTimeFromQuery(walletQuery);
            predicateList.add(cb.between(tradeRoot.get("updateTime"), cb.literal(timeMap.get("startTime")), cb.literal(timeMap.get("endTime"))));
        }
        if (walletQuery.getChannel() != null) {
            Join<Wallet, Channel> channelJoin = root.join("channel");
            predicateList.add(cb.equal(channelJoin.get("id"), walletQuery.getChannel()));
        }
        List<Selection<?>> selectionList = new ArrayList<>();
        selectionList.add(cb.<BigDecimal>sum(cb.<BigDecimal>selectCase().when(cb.equal(tradeRoot.get("tradeFlag"), 1), tradeRoot.get("amount")).otherwise(DEFAULT_VALUE)));
        selectionList.add(cb.<BigDecimal>sum(cb.<BigDecimal>selectCase().when(cb.equal(tradeRoot.get("tradeFlag"), 0), tradeRoot.get("amount")).otherwise(DEFAULT_VALUE)));
        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        criteriaQuery.multiselect(selectionList).where(predicates);
        return criteriaQuery;
    }

    private CriteriaQuery<Object[]> findTradeAmountQuery(WalletQuery walletQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class);
        Root<WalletTrade> tradeRoot = criteriaQuery.from(WalletTrade.class);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(tradeRoot.get("walletId"), walletQuery.getWalletId()));
        if (!StringUtil.isNull(walletQuery.getType())) {
            predicateList.add(cb.equal(tradeRoot.get("tradeFlag"), walletQuery.getType()));
        }
        if (walletQuery.getSources() != null && walletQuery.getSources().length > 0) {
            Path<Integer> typePath = tradeRoot.get("tradeType");
            CriteriaBuilder.In<Integer> typeIn = cb.in(typePath);
            Arrays.asList(walletQuery.getSources()).forEach(source -> typeIn.value(Integer.valueOf(source)));
            predicateList.add(typeIn);
        }
        if (walletQuery.getStatuses() != null && walletQuery.getStatuses().length > 0) {
            Path<Integer> statusPath = tradeRoot.get("status");
            CriteriaBuilder.In<Integer> statusIn = cb.in(statusPath);
            Arrays.asList(walletQuery.getStatuses()).forEach(status -> statusIn.value(Integer.valueOf(status)));
            predicateList.add(statusIn);
        }
        if (walletQuery.getPlatforms() != null && walletQuery.getPlatforms().length > 0) {
            Path<Integer> channelPath = tradeRoot.get("channel");
            CriteriaBuilder.In<Integer> channelIn = cb.in(channelPath);
            Arrays.asList(walletQuery.getPlatforms()).forEach(channel -> channelIn.value(Integer.valueOf(channel)));
            predicateList.add(channelIn);
        }
        if (!StringUtil.isNull(walletQuery.getStartTime()) && !StringUtil.isNull(walletQuery.getEndTime())) {
            if (!StringUtil.isNull(walletQuery.getStartTime())) {
                Map<String, Date> timeMap = getTimeFromQuery(walletQuery);
                predicateList.add(cb.between(tradeRoot.get("updateTime"), cb.literal(timeMap.get("startTime")), cb.literal(timeMap.get("endTime"))));
            }
        }
        List<Selection<?>> selectionList = new ArrayList<>();
        selectionList.add(cb.<BigDecimal>sum(cb.<BigDecimal>selectCase().when(cb.equal(tradeRoot.get("tradeFlag"), 1), tradeRoot.get("amount")).otherwise(DEFAULT_VALUE)));
        selectionList.add(cb.<BigDecimal>sum(cb.<BigDecimal>selectCase().when(cb.and(cb.equal(tradeRoot.get("tradeFlag"), 0), cb.notEqual(tradeRoot.get("status"), 3)), tradeRoot.get("amount")).otherwise(DEFAULT_VALUE)));

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        criteriaQuery.multiselect(selectionList).where(predicates);
        return criteriaQuery;
    }

    private CriteriaQuery<Object[]> findBankCardListQuery(WalletQuery walletQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class);
        List<Predicate> predicateList = new ArrayList<>();
        Root<WalletTrade> root = criteriaQuery.from(WalletTrade.class);
        Root<BankCard> cardRoot = criteriaQuery.from(BankCard.class);
        Join<BankCard, Bank> bankJoin = cardRoot.join("bank", JoinType.LEFT);
        predicateList.add(cb.equal(root.get("bankcardId"), cardRoot.get("id")));
        predicateList.add(cb.equal(root.get("walletId"), walletQuery.getWalletId()));
        List<Selection<?>> selectionList = new ArrayList<>();
        selectionList.add(bankJoin.get("name"));
        selectionList.add(cardRoot.get("bankNo"));
        selectionList.add(cb.<BigDecimal>sum(cb.<BigDecimal>selectCase().when(cb.and(cb.equal(root.get("tradeFlag"), 0), cb.notEqual(root.get("status"), 3)), root.get("amount")).otherwise(DEFAULT_VALUE)));
        criteriaQuery.groupBy(cardRoot.get("id"));
        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        criteriaQuery.multiselect(selectionList).where(predicates);
        return criteriaQuery;
    }

    private Page<WalletViewModel> setWalletViewData(Page<Object[]> tradePage, WalletQuery walletQuery) {
        List<WalletViewModel> currentView = new ArrayList<WalletViewModel>();
        if (!CollectionUtils.isEmpty(tradePage.getContent())) {
            tradePage.getContent().forEach(trade -> {
                WalletViewModel viewModel = new WalletViewModel();

                String walletId = StringUtil.defaultNullStr(trade[0]);
                viewModel.setWalletId(Long.parseLong(walletId));
                String mobile = StringUtil.defaultNullStr(trade[1]);
                viewModel.setMobile(MobileUtil.getEncyptMobile(mobile));
                viewModel.setInAmount(StringUtil.objToString(trade[2]));
                viewModel.setOutAmount(StringUtil.objToString(trade[3]));
                String operatorTime = StringUtil.defaultNullStr(trade[4]);
                viewModel.setLastOperatorTime(StringUtil.isNull(operatorTime) ? "" : operatorTime.substring(0, 19));
                viewModel.setBalance(StringUtil.objToString(trade[5]));
                viewModel.setStatus(StringUtil.objToString(trade[6]));
                viewModel.setType(StringUtil.objToString(trade[7]));
                currentView.add(viewModel);
            });
        }
        return new PageImpl<WalletViewModel>(currentView, new PageRequest(walletQuery.getCurrentPage() - 1, walletQuery.getPageSize()), tradePage.getTotalElements());
    }

    private Page<WalletViewModel> setBankInfoToViewModel(Page<Object[]> bankPage, WalletQuery walletQuery) {
        List<WalletViewModel> currentView = new ArrayList<WalletViewModel>();
        if (!CollectionUtils.isEmpty(bankPage.getContent())) {
            bankPage.getContent().forEach(bank -> {
                WalletViewModel viewModel = new WalletViewModel();
                viewModel.setBankName(StringUtil.defaultNullStr(bank[0]));
                viewModel.setBankNo(StringUtil.defaultNullStr(bank[1]));
                viewModel.setOutAmount(StringUtil.objToString(bank[2]));
                currentView.add(viewModel);
            });
        }
        return new PageImpl<WalletViewModel>(currentView, new PageRequest(walletQuery.getCurrentPage() - 1, walletQuery.getPageSize()), bankPage.getTotalElements());
    }

    private List<WalletViewModel> setTransactionInfoToViewModel(List<Object[]> list) {
        List<WalletViewModel> reList = new ArrayList<WalletViewModel>();
        if (!CollectionUtils.isEmpty(list)) {
            for (Object[] obj : list) {
                WalletViewModel viewModel = new WalletViewModel();
                viewModel.setTradeNo(StringUtil.defaultNullStr(obj[0]));
                StringBuffer type = new StringBuffer();
                StringBuffer operateAmount = new StringBuffer();
                int tradeFlag = Integer.parseInt(StringUtil.defaultNullStr(obj[1]));
                if (tradeFlag == 1) {
                    type.append("入账-");
                    operateAmount.append(StringUtil.defaultNullStr(obj[5]));
                } else if (tradeFlag == 0) {
                    type.append("出账-");
                    operateAmount.append("-").append(StringUtil.defaultNullStr(obj[5]));
                }
                type.append(StringUtil.defaultNullStr(obj[2]));
                viewModel.setType(type.toString());
                viewModel.setBankName(StringUtil.defaultNullStr(obj[3]));
                viewModel.setStatus(StringUtil.defaultNullStr(obj[4]));
                viewModel.setOpeateAmount(operateAmount.toString());
                viewModel.setBalance(StringUtil.objToString(obj[6]));
                viewModel.setLastOperatorTime(obj[7] != null ? obj[7].toString().substring(0, 19) : "");
                viewModel.setPlatform(StringUtil.defaultNullStr(obj[8]));
                viewModel.setfailReason(StringUtil.defaultNullStr(obj[9]));
                reList.add(viewModel);
            }
        }
        return reList;
    }

    private Map<String, Object> setWalletParamsFromQuery(WalletQuery walletQuery) {
        int isNullMobile = 1;
        int isNullTradeNo = 1;
        int isNullUpdateTime = 1;
        int isNullChannel = 1;
        String mobile = DEFAULT_STR;
        String tradeNo = DEFAULT_STR;
        Long channel = DEFAULT_LONG;
        Date now = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date startTime = now;
        Date endTime = now;
        String keyWord = walletQuery.getKeyword();
        if (!StringUtil.isNull(walletQuery.getKeyword()))
            if (walletQuery.getKeyType() == 1) {
                isNullMobile = 0;
                mobile = keyWord;
            } else if (walletQuery.getKeyType() == 2) {
                isNullTradeNo = 0;
                tradeNo = keyWord;
            }
        if (!StringUtil.isNull(walletQuery.getStartTime()) && !StringUtil.isNull(walletQuery.getEndTime())) {
            isNullUpdateTime = 0;
            Map<String, Date> timeMap = getTimeFromQuery(walletQuery);
            startTime = timeMap.get("startTime");
            endTime = timeMap.get("endTime");
        }
        if (walletQuery.getChannel() != null) {
            isNullChannel = 0;
            channel = walletQuery.getChannel();
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("isNullMobile", isNullMobile);
        param.put("isNullTradeNo", isNullTradeNo);
        param.put("isNullUpdateTime", isNullUpdateTime);
        param.put("mobile", mobile);
        param.put("tradeNo", tradeNo);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("isNullChannel", isNullChannel);
        param.put("channel", channel);
        return param;
    }

    private Map<String, Date> getTimeFromQuery(WalletQuery walletQuery) {
        return new HashMap<String, Date>() {{
            put("startTime", DateUtils.getDate(walletQuery.getStartTime() + " 00:00:00", DateUtils.DATE_LONGTIME24_PATTERN));
            put("endTime", DateUtils.getDate(walletQuery.getEndTime() + " 23:59:59", DateUtils.DATE_LONGTIME24_PATTERN));
        }};
    }
}
