package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunhuazhong on 2015/8/4.
 */
@Entity
@Table(name = "qrcode_statistics")
public class QRCodeStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "qrcodeChannel", foreignKey=@ForeignKey(name="FK_QRCODE_STATISTICS_REF_QRCODE_CHANNEL", foreignKeyDefinition="FOREIGN KEY (qrcode_channel) REFERENCES qrcode_channel(id)"))
    private QRCodeChannel qrCodeChannel;

    @Column(columnDefinition = "DATETIME")
    private Date statisticsTime;

    @Column(columnDefinition = "int(8)")
    private Integer scanCount = 0;

    @Column(columnDefinition = "int(8)")
    private Integer subscribeCount = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QRCodeChannel getQrCodeChannel() {
        return qrCodeChannel;
    }

    public void setQrCodeChannel(QRCodeChannel qrCodeChannel) {
        this.qrCodeChannel = qrCodeChannel;
    }

    public Date getStatisticsTime() {
        return statisticsTime;
    }

    public void setStatisticsTime(Date statisticsTime) {
        this.statisticsTime = statisticsTime;
    }

    public Integer getScanCount() {
        return scanCount;
    }

    public void setScanCount(Integer scanCount) {
        this.scanCount = scanCount;
    }

    public Integer getSubscribeCount() {
        return subscribeCount;
    }

    public void setSubscribeCount(Integer subscribeCount) {
        this.subscribeCount = subscribeCount;
    }
}
