package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface PurchaseOrderImageRepository extends PagingAndSortingRepository<PurchaseOrderImage, Long> {


    List<PurchaseOrderImage> findAllByObjIdAndImageScene(Long objId, PurchaseOrderImageScene imageScene);

    PurchaseOrderImage findFirstByObjIdAndImageSceneAndImageType(Long objId, PurchaseOrderImageScene imageScene, PurchaseOrderImageType purchaseOrderImageType);

    PurchaseOrderImage findFirstByObjIdAndImageSceneOrderByIdDesc(Long objId, PurchaseOrderImageScene imageScene);

    List<PurchaseOrderImage> findByObjIdAndImageSceneAndStatusOrderByImageTypeAsc(Long objId, PurchaseOrderImageScene imageScene, Integer status);

    @Query(value = "select count(1) from purchase_order_image_type where  id in(SELECT image_type from purchase_order_image where obj_id=?1 and image_scene=?2 )  and parent_id!=7", nativeQuery = true)
    Integer findByObjIdAndImageScene(Long objId, PurchaseOrderImageScene imageScene);

    @Query(value = "select * from purchase_order_image where obj_id=?1 and image_scene=?2 and status=0", nativeQuery = true)
    List<PurchaseOrderImage> findExistImagesByObjIdAndImageScene(Long objId, PurchaseOrderImageScene imageScene);

    @Query(value = "select * from purchase_order_image where obj_id=?1 and image_scene=?2 and (status=?3 or status=?4)", nativeQuery = true)
    List<PurchaseOrderImage> findByByObjIdAndImageSceneAndStatusAndOther(Long objId, PurchaseOrderImageScene imageScene, Integer status, Integer other);

    @Query(value = "select url from purchase_order_image p where p.url like ?1%   and p.status=?2 and (p.expire_date>CURDATE() or p.expire_date is null) ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    String findSecondImgUseUrl(String findUserUrl, Integer status);

    @Query(value = "select url from purchase_order_image p where p.url like ?1%   and p.status=?2  and (p.expire_date>CURDATE() or p.expire_date is null) ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    String findSecondImgCarUrl(String findCarUrl, Integer status);

    @Query(value = "select url from purchase_order_image p where p.url like ?1%   and p.status=?2    and datediff(?3,p.audit_time)<275 and 0<datediff(?3,p.audit_time) ORDER BY create_time DESC LIMIT 1", nativeQuery = true)
    String findSecondImgCarUrlAndAuditTime(String findCarUrl, Integer status, Date auditTime);

    @Query(value = "SELECT * from purchase_order_image p where p.image_type in (SELECT id from purchase_order_image_type where parent_id=?3) and p.obj_id=?1 and p.image_scene=?2", nativeQuery = true)
    List<PurchaseOrderImage> findOneselfDefineImgByObjIdAndImageScene(Long objId, PurchaseOrderImageScene imageScene, Long parentId);

    @Query(value = "select * from (select * from purchase_order_image pi  where pi.obj_id = ?1 and pi.image_scene = ?2 order by pi.id ) pt group by pt.image_type ", nativeQuery = true)
    List<PurchaseOrderImage> findByObjIdGroupByImageType(Long objId, PurchaseOrderImageScene purchaseOrderImageScene);

    List<PurchaseOrderImage> findByObjIdAndImageTypeAndImageScene(Long orderId, PurchaseOrderImageType subType, PurchaseOrderImageScene purchaseOrderImageScene);

    @Query(value = "select poi.* from purchase_order_image poi,purchase_order_image_type poit where poi.image_type = poit.id and poi.obj_id = ?1 and poit.parent_id = ?2 ",nativeQuery = true)
    List<PurchaseOrderImage> findAllByObjIdAndParentImageType(long objId, PurchaseOrderImageType parentImageType);

    @Query(value = "select poi.* from purchase_order_image poi,daily_restart_insurance dri,payment p  where poi.image_scene = 6 and poi.obj_id = dri.id and p.id = dri.payment and p.status = 2 and dri.daily_insurance = ?1 ",nativeQuery = true)
    List<PurchaseOrderImage> findByLastDailyInsurance(DailyInsurance dailyInsurance);

    @Query(value = "select hint from purchase_order_image where id=?1", nativeQuery = true)
    String findHintById(Long Id);

}
