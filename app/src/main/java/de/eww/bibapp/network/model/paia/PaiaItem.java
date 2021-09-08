package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PaiaItem {

    @SerializedName("queue")
    private int queue;

    @SerializedName("canrenew")
    private boolean canRenew;

    @SerializedName("cancancel")
    private boolean canCancel;

    @SerializedName("storageid")
    private String storageId;

    @SerializedName("label")
    private String label;

    @SerializedName("reminder")
    private int reminder;

    @SerializedName("edition")
    private String edition;

    @SerializedName("endtime")
    private Date endTime;

    @SerializedName("status")
    private int status;

    @SerializedName("starttime")
    private Date startTime;

    @SerializedName("duedate")
    private Date dueDate;

    @SerializedName("storage")
    private String storage;

    @SerializedName("about")
    private String about;

    @SerializedName("renewals")
    private int renewals;

    @SerializedName("requested")
    private String requested;

    @SerializedName("item")
    private String item;

    @SerializedName("error")
    private String error;

    public int getQueue() {
        return queue;
    }

    public boolean isCanRenew() {
        return canRenew;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public String getStorageId() {
        return storageId;
    }

    public String getLabel() {
        return label;
    }

    public int getReminder() {
        return reminder;
    }

    public String getEdition() {
        return edition;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getStatus() {
        return status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getStorage() {
        return storage;
    }

    public String getAbout() {
        return about;
    }

    public int getRenewals() {
        return renewals;
    }

    public String getRequested() {
        return requested;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getError() {
        return error;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final PaiaItem compare = (PaiaItem) obj;
        return this.item.equals(compare.item);
    }
}
