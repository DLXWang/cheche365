package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrderImageType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Repository
public interface PurchaseOrderImageTypeRepository extends PagingAndSortingRepository<PurchaseOrderImageType, Long> {


    List<PurchaseOrderImageType> findByParentId(Long parentId);

    List<PurchaseOrderImageType> findByReusable(boolean reusable);

    @Query(value = "select pt.* from purchase_order_image_type pt inner join purchase_order_image pi  on pt.id = pi.image_type where pt.parent_id = ?1 and pi.obj_id = ?2 and pi.image_scene = ?3 ", nativeQuery = true)
    List<PurchaseOrderImageType> findByParentIdAndObjId(Long parentId, Long objId, Long imageSceneId);

    @Query(value = "select count(distinct (pt.id)) from purchase_order_image_type pt left join purchase_order_image pi  on pt.id = pi.image_type " +
        " where  pi.obj_id = ?2 and pi.image_scene = ?1 or pt.parent_id in " +
        "(select pt.image_type  from purchase_order_image_scene_type pt where pt.image_scene = ?1) order by pt.id ", nativeQuery = true)
    Integer findByParentIdsAndOrderId(Long imageSceneId, Long objId);


    @Query(value = "select  pt.* from purchase_order_image_type pt where pt.id in( ?1 ) ", nativeQuery = true)
    List<PurchaseOrderImageType> findByIds(Collection<BigInteger> parentId);

    @Query(value = "select  pt.* from purchase_order_image_type pt where pt.parent_id in( ?1 ) ", nativeQuery = true)
    List<PurchaseOrderImageType> findByParentIds(List<BigInteger> parentId);

    PurchaseOrderImageType findByExternalTypeAndParentId(Integer externalType, Long parentId);

}
