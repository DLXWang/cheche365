package com.cheche365.cheche.scheduletask.model

/**
 * Created by zhangtc on 2018/3/9.
 */
class OpenSignupReportModel extends AttachmentData{

    String developerType

    String developerBusinessType

    String company

    String mobile

    String contactName

    String email

    String address

    String getDeveloperType() {
        return developerType
    }

    void setDeveloperType(String developerType) {
        this.developerType = developerType
    }
}
