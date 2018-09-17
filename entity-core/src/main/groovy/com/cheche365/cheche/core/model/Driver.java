package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.DateUtils;

public class Driver {

    private int gender;  //性别
    private int yearOfLicenseIssued; //获得驾照年份
    private boolean safeDrive; //去年是否有违章记录
    private int yearOfBirth;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isSafeDrive() {
        return safeDrive;
    }

    public void setSafeDrive(boolean safeDrive) {
        this.safeDrive = safeDrive;
    }

    public int getYearOfLicenseIssued() {
        return yearOfLicenseIssued;
    }

    public void setYearOfLicenseIssued(int yearOfLicenseIssued) {
        this.yearOfLicenseIssued = yearOfLicenseIssued;
    }

    public int getYearOfDriving() {
        //驾龄
        return DateUtils.currentYear() - this.yearOfLicenseIssued;
    }

    public int getYearOfBirth() {
        return this.yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public int getAge() {
        return DateUtils.currentYear() - this.yearOfBirth;
    }


}
