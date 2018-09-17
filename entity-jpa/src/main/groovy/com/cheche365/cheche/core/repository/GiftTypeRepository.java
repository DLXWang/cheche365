package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftTypeRepository extends JpaSpecificationExecutor<GiftType>, PagingAndSortingRepository<GiftType, Long> {

    GiftType findFirstById(Long id);

    GiftType findFirstByName(String name);

    @Query(value = "SELECT * FROM gift_type", nativeQuery = true)
    List<GiftType> findAll();

    //满额赠送礼品
    @Query(value = "select * from gift_type where use_type = 3 and category = 4", nativeQuery = true)
    List<GiftType> findMarketingRuleGift();

    //再送礼品
    @Query(value = "select * from gift_type where use_type = 3 and category = 6", nativeQuery = true)
    List<GiftType> findMarketingRuleExtraGift();

    @Query(value = "select gt.name from gift_type gt where gt.name like %?1% limit ?2", nativeQuery = true)
    List<String> findByNameLike(String paramWord, Integer pageSize);

    @Query(value = "select * from gift_type gt where gt.name = ?1 and gt.category = ?2 and gt.use_type = ?3", nativeQuery = true)
    List<GiftType> findByNameAndCategoryAndUseType(String giftName, String category, String useType);

    List<GiftType> findByCategoryAndDisable(Integer category, Boolean disable);
}
