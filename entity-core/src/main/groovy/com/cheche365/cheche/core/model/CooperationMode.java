package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.CooperationModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 合作方式
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class CooperationMode implements Serializable{

    private static final long serialVersionUID = 3800302632067873678L;
    private Long id;
    private String name;//合作方式，包括CPM，CPS，CPA，CPC，换量
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Component
    public static class Enum{
        //CPM
        public static CooperationMode CPM;
        //CPS
        public static CooperationMode CPS;
        //CPA
        public static CooperationMode CPA;
        //CPC
        public static CooperationMode CPC;
        //换量
        public static CooperationMode CHANGE_QUANTITY;
        //运营推广
        public static CooperationMode MARKETING;

        public static List<CooperationMode> BUSINESS_ACTIVITY_MODES;

        @Autowired
        public Enum(CooperationModeRepository cooperationModeRepository){
            CPM = cooperationModeRepository.findFirstByName("CPM");
            CPS = cooperationModeRepository.findFirstByName("CPS");
            CPA = cooperationModeRepository.findFirstByName("CPA");
            CPC = cooperationModeRepository.findFirstByName("CPC");
            CHANGE_QUANTITY = cooperationModeRepository.findFirstByName("换量");
            MARKETING = cooperationModeRepository.findFirstByName("MARKETING");
            BUSINESS_ACTIVITY_MODES = Arrays.asList(CPM,CPS,CPA,CPC,CHANGE_QUANTITY);
        }
    }
}
