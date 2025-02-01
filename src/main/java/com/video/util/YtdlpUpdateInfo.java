package com.video.util;

public class YtdlpUpdateInfo {

    private boolean upToDate;
    private boolean updated;
    private boolean error;

    private String latestVersion;
    private String actualVersion;

    public YtdlpUpdateInfo(boolean upToDate, String latestVersion, String actualVersion) {
        this.upToDate = upToDate;
        this.latestVersion = latestVersion;
        this.actualVersion = actualVersion;
    }

    public YtdlpUpdateInfo() {
        this.upToDate = false;
        this.updated = false;
        this.error = false;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getActualVersion() {
        return actualVersion;
    }

    public void setActualVersion(String actualVersion) {
        this.actualVersion = actualVersion;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "YtDLPUpdate [upToDate=" + upToDate + ", updated=" + updated + ", error=" + error + ", latestVersion="
                + latestVersion + ", actualVersion=" + actualVersion + "]";
    }

}
