package com.air.update.transaction.bean;

public class CheckItem {

    private String appType;
    private String fileUuid;
    private int id;
    private String packageName;
    private String sourceUrl;
    private int versionCode;

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "CheckItem{" +
                "appType='" + appType + '\'' +
                ", fileUuid='" + fileUuid + '\'' +
                ", id=" + id +
                ", packageName='" + packageName + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
