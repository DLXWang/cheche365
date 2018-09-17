package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderImageScene;
import com.cheche365.cheche.core.model.PurchaseOrderImageSceneType;
import com.cheche365.cheche.core.model.PurchaseOrderImageType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PurchaseOrderImageSceneTypeRepository extends PagingAndSortingRepository<PurchaseOrderImageSceneType, Long> {


    List<PurchaseOrderImageSceneType> findByImageSceneOrderByImageTypeAsc(PurchaseOrderImageScene purchaseOrderImageScene);


    @Query(value = "select pt.image_type from purchase_order_image_scene_type pt where pt.image_scene = ?1 ", nativeQuery = true)
    List<BigInteger> findImageTypeByImageScene(PurchaseOrderImageScene purchaseOrderImageScene);

    @Query(value = "select pt.parent_id from purchase_order_image_type where pt.id in( select pi.image_type from purchase_order_image pi where pi.obj_id = ?1 )", nativeQuery = true)
    List<BigInteger> findImageTypeByPurchaseOrder(PurchaseOrder purchaseOrder);


    @Query(value = "" +
        "select distinct (pt.parent_id) from purchase_order_image_type pt where pt.id in" +
        "   ( select pi.image_type from purchase_order_image pi where pi.obj_id = ?2 and pi.image_scene = ?1 ) " +
        "or pt.parent_id in " +
        "(select pt.image_type  from purchase_order_image_scene_type pt where pt.image_scene = ?1)  order by pt.parent_id ", nativeQuery = true)
    List<BigInteger> findBySceneAndPurchaseOrder(PurchaseOrderImageScene purchaseOrderImageScene, Long objId);



    @Query(value = "select pt.* from purchase_order_image_type pt inner join purchase_order_image_scene_type pst on  pst.image_type = pt.id where pst.image_scene = ?1 ", nativeQuery = true)
    List<PurchaseOrderImageType> getImageTypeByImageScene(Long  purchaseOrderImageSceneId);
}
