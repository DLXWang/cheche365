package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AgentTmp;
import com.cheche365.cheche.core.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@Profile("!test")
public interface AgentTmpRepository extends PagingAndSortingRepository<AgentTmp, Long>,JpaSpecificationExecutor<AgentTmp> {

    AgentTmp findByMobile(String mobile);

    AgentTmp findByIdentity(String identityNumber);

    @Query(value = "select count(1) from agent_tmp a where a.name like %?1% or a.mobile like %?2%",nativeQuery = true)
    long findCountByKeywod(String name, String mobile);

    AgentTmp findFirstByUser(User user);

    List<AgentTmp> findByUser(User user);

    @Query(value = "select avg(rebate), max(rebate), min(rebate) from agent_tmp", nativeQuery = true)
    List findAvgAndMaxAndMinRebate();

    List<AgentTmp> findByUserIsNotNull();

    @Query(value = "select distinct user from agent_tmp where user is not null", nativeQuery = true)
    List<BigInteger> listEnableAgentUser();

    @Query(value = "select * from agent_tmp  where enable=1 and name like %?1%",nativeQuery = true)
    List<AgentTmp> listByKeywod(String name);

//    @Query(value = "select f.agent from fanhua_auto f " +
//        " join insurance i on i.policy_no= f.insurance_no" +
//        " join purchase_order p on p.obj_id=i.quote_record" +
//        " where p.id=?1" +
//        " UNION" +
//        " select f.agent from fanhua_auto f " +
//        " join compulsory_insurance i on i.policy_no= f.insurance_no" +
//        " join purchase_order p on p.obj_id=i.quote_record" +
//        " where p.id=?1",nativeQuery = true)
//    String findFanHuaAutoAgentByOrder(Long orderId);
//
//    @Query(value = "select f.agent from fanhua_bocheng f " +
//        " join insurance i on i.policy_no= f.insurance_no" +
//        " join purchase_order p on p.obj_id=i.quote_record" +
//        " where p.id=?1" +
//        " UNION" +
//        " select f.agent from fanhua_bocheng f " +
//        " join compulsory_insurance i on i.policy_no= f.insurance_no" +
//        " join purchase_order p on p.obj_id=i.quote_record" +
//        " where p.id=?1",nativeQuery = true)
//    String findFanHuaBaoChengAgentByOrder(Long orderId);
//
//
//    @Query(value = "select agent from fanhua_non_auto where insurance_no =?1",nativeQuery = true)
//    String findFanHuaNonAutoAgentByOrder(String  insuranceNo);

    @Query(value = "select f.agent,f.commission,a.card_number,i.premium from fanhua_auto f " +
        " join insurance i on i.policy_no= f.insurance_no" +
        " join purchase_order p on p.obj_id=i.quote_record" +
        " join agent_tmp a on a.name=f.agent" +
        " where p.id=?1" +
        " UNION ALL" +
        " select f.agent,f.commission,a.card_number,i.compulsory_premium from fanhua_auto f " +
        " join compulsory_insurance i on i.policy_no= f.insurance_no" +
        " join purchase_order p on p.obj_id=i.quote_record" +
        " join agent_tmp a on a.name=f.agent" +
        " where p.id=?1",nativeQuery = true)
    List<Object[]> findFanHuaAutoAgentByOrder(Long orderId);

    @Query(value = "select f.agent,f.commission,a.card_number from fanhua_bocheng f " +
        " join insurance i on i.policy_no= f.insurance_no" +
        " join purchase_order p on p.obj_id=i.quote_record" +
        " join agent_tmp a on a.name=f.agent" +
        " where p.id=?1" +
        " UNION ALL" +
        " select f.agent,f.commission,a.card_number from fanhua_bocheng f " +
        " join compulsory_insurance i on i.policy_no= f.insurance_no" +
        " join purchase_order p on p.obj_id=i.quote_record" +
        " join agent_tmp a on a.name=f.agent" +
        " where p.id=?1",nativeQuery = true)
    List<Object[]> findFanHuaBaoChengAgentByOrder(Long orderId);


    @Query(value = "select f.agent,f.commission,a.card_number from fanhua_non_auto f" +
        " join agent_tmp a on a.name =f.agent" +
        " where f.insurance_no=?1",nativeQuery = true)
    List<Object[]> findFanHuaNonAutoAgentByOrder(String insuranceNo);

}
