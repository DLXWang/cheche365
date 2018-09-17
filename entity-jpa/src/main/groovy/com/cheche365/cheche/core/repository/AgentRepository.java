package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Agent;
import com.cheche365.cheche.core.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by liqiang on 3/31/15.
 */
@Profile("!test")
public interface AgentRepository extends PagingAndSortingRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    Agent findFirstByMobile(String mobile);

    Agent findByIdentity(String identityNumber);

    Agent findFirstByUser(User user);

    List<Agent> findByUser(User user);

    @Query(value = "select avg(rebate), max(rebate), min(rebate) from agent", nativeQuery = true)
    List findAvgAndMaxAndMinRebate();

    List<Agent> findByUserIsNotNull();

    @Query(value = "select * from agent  where enable=1 and name like %?1%", nativeQuery = true)
    List<Agent> listByKeywod(String name);

    @Modifying
    @Query("update AgentInternal ai set ai.agentId = (select a.id from Agent a where ai.identity = a.identity)")
    int updateAgentInternal();

}
