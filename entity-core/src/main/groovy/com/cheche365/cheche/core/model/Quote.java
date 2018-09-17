package com.cheche365.cheche.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 车险报价
 *
 * @author liqiang
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote implements Serializable {

    private static final long serialVersionUID = -1038317466634180160L;
    private Long id;
    private Auto auto; //被保汽车
    private User applicant;//申请人
    private Date quoteTime;//报价时间
    private QuoteStatus status;//报价状态：	1. queued,2. processing,3. completed,4. invalid data
    private Set<QuoteRecord> quoteRecord = new HashSet<QuoteRecord>();
    private Channel sourceChannel; //报价来源渠道，如微信，IOS_4，第三方
//	private List<Driver> drivers = new ArrayList<Driver>(); //指定的司机



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="auto", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_REF_AUTO", foreignKeyDefinition="FOREIGN KEY (auto) REFERENCES auto(id)"))
    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name="applicant", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_REF_USER", foreignKeyDefinition="FOREIGN KEY (applicant) REFERENCES user(id)"))
    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(Date quoteTime) {
        this.quoteTime = quoteTime;
    }

    @ManyToOne
    @JoinColumn(name="status", foreignKey=@ForeignKey(name="FK_PURCHASE_ORDER_REF_QUOTE_STATUS", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES quote_status(id)"))
    public QuoteStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "quote", orphanRemoval=true)
    public Set<QuoteRecord> getQuoteRecord() {
        return quoteRecord;
    }

    public void setQuoteRecord(Set<QuoteRecord> quoteRecord) {
        this.quoteRecord = quoteRecord;
    }

    @ManyToOne
    @JoinColumn(name="source_channel", foreignKey=@ForeignKey(name="FK_QUOTE_REF_CHANNEL", foreignKeyDefinition="FOREIGN KEY (source_channel) REFERENCES channel(id)"))
    public Channel getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

}
