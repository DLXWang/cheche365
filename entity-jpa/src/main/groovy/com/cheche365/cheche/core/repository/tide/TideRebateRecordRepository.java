package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TideRebateRecord;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The interface Tide rebate record repository.
 *
 * @author Lei
 * @date 2018 -05-07 20:51:21
 */
@Repository
public interface TideRebateRecordRepository extends PagingAndSortingRepository<TideRebateRecord, Long>, JpaSpecificationExecutor<TideRebateRecord> {

    /**
     * 根据有效合约记录点位信息.
     *
     * @return the int
     * @author Lei
     * @date 2018 -05-07 20:51:23
     */
    @Transactional
    @Modifying
    @Query(value = "INSERT IGNORE INTO tide_rebate_record " +
        "SELECT NULL," +
        "r.id," +
        "r.contract_rebate_code," +
        "r.tide_contract," +
        "r.support_area," +
        "r.insurance_type," +
        "r.car_type," +
        "r.choose_condition," +
        "r.original_commecial_rate," +
        "r.original_compulsory_rate," +
        "r.auto_tax_return_type," +
        "r.auto_tax_return_value," +
        "r.market_commercial_rate," +
        "r.market_compulsory_rate," +
        "r.market_auto_tax_return_type," +
        "r.market_auto_tax_return_value," +
        "r.effective_date," +
        "r.expire_date," +
        "r.status," +
        "curdate()," +
        "r.description," +
        "r.disable " +
        "FROM tide_contract_rebate r," +
        "tide_contract c " +
        "WHERE r.tide_contract=c.id " +
        "AND r.status <> 2 " +
        "AND c.disable = 0 " +
        "AND r.disable = 0 ", nativeQuery = true)
    int recordRebate();
}
