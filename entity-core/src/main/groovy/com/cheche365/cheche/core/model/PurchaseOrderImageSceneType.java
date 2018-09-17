package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by xu.yelong on 2016/8/22.
 */
@Entity
public class PurchaseOrderImageSceneType {
    private Long id;
    private PurchaseOrderImageScene imageScene;
    private PurchaseOrderImageType imageType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "image_scene", foreignKey=@ForeignKey(name="FK_IMAGE_SCENE_TYPE_REF_PURCHASE_ORDER_IMAGE_SCENE", foreignKeyDefinition="FOREIGN KEY (image_scene) REFERENCES purchase_order_image_scene(id)"))
    public PurchaseOrderImageScene getImageScene() {
        return imageScene;
    }

    public void setImageScene(PurchaseOrderImageScene imageScene) {
        this.imageScene = imageScene;
    }

    @ManyToOne
    @JoinColumn(name = "image_type", foreignKey=@ForeignKey(name="FK_IMAGE_SCENE_TYPE_REF_PURCHASE_ORDER_IMAGE_TYPE", foreignKeyDefinition="FOREIGN KEY (image_type) REFERENCES purchase_order_image_type(id)"))
    public PurchaseOrderImageType getImageType() {
        return imageType;
    }

    public void setImageType(PurchaseOrderImageType imageType) {
        this.imageType = imageType;
    }
}
