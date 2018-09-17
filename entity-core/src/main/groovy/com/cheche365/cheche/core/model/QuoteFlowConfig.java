package com.cheche365.cheche.core.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by shanxf on 2017/6/14.
 * 渠道，地区，保险公司，确定唯一配置值
 */
@Entity
@Table(name = "quote_flow_config")
public class QuoteFlowConfig implements Serializable{

    private static final long serialVersionUID = -8088724105005685305L;
    private Long id;
    private Channel channel;
    private InsuranceCompany insuranceCompany;
    private Area area;
    private Long configType;//1报价方式;2:车车支付
    private Long configValue;
    private Boolean enable=true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(foreignKey =@ForeignKey(name = "quote_flow_config_ibfk1",foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel (id)") )
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "quote_flow_config_ibfk2", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company (id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name="area",foreignKey = @ForeignKey(name = "quote_flow_config_ibfk3",foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Long getConfigType() {
        return configType;
    }

    public void setConfigType(Long configType) {
        this.configType = configType;
    }

    public Long getConfigValue() {
        return configValue;
    }

    public void setConfigValue(Long configValue) {
        this.configValue = configValue;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }


    @Override
    public boolean equals(Object obj) {
        QuoteFlowConfig config=(QuoteFlowConfig)obj;
        return obj instanceof QuoteFlowConfig
            && config.getArea().getId().equals(this.getArea().getId())
            && config.getInsuranceCompany().getId().equals(this.getInsuranceCompany().getId())
            && config.getChannel().getId().equals(this.getChannel().getId())
            && config.getConfigType().equals(this.getConfigType());
    }

    public enum ConfigValue {
        WEB_PARSER(2, "自有"), API(4, "接口"),FANHUA(6,"泛华"),REFERENCE(7,"参考"),BLURRY(8,"模糊"),AGENTPARSER(9,"太平洋UK"),JINDOUYUN(11,"金斗云");
        private Integer index;
        private String name;
        ConfigValue(Integer index, String name) {
            this.index = index;
            this.name = name;
        }
        public Integer getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }
        public static String getName(int index) {
            for (ConfigValue configValue : ConfigValue.values()) {
                if (configValue.getIndex() == index) {
                    return configValue.name;
                }
            }
            return null;
        }
        public static Integer getId(String name){
            for (ConfigValue configValue : ConfigValue.values()) {
                if (configValue.getName().equals(name)) {
                    return configValue.getIndex();
                }
            }
            return null;
        }
    }
    public enum PayValue {
        THIRD_PARTNER(0, "自有");
        private Integer index;
        private String name;
        PayValue(Integer index, String name) {
            this.index = index;
            this.name = name;
        }
        public Integer getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }

    }

    public enum ConfigType{
        QUOTATION(1, "报价方式"),
        CHECHE_PAY(2, "车车支付"),
        NOT_SUPPORT_PAY(3, "不允许支付");
        private Integer index;
        private String name;
        ConfigType(Integer index, String name) {
            this.index = index;
            this.name = name;
        }
        public Integer getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }
    }
}
