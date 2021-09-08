package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PaiaFee {

    @SerializedName("feetypeid")
    private String feeTypeId;

    @SerializedName("amount")
    private String amount;

    @SerializedName("date")
    private Date date;

    @SerializedName("feetype")
    private String feeType;

    @SerializedName("about")
    private String about;

    @SerializedName("item")
    private String item;

    public String getFeeTypeId() {
        return feeTypeId;
    }

    public String getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getFeeType() {
        return feeType;
    }

    public String getAbout() {
        return about;
    }

    public String getItem() {
        return item;
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

        final PaiaFee compare = (PaiaFee) obj;
        return this.item.equals(compare.item);
    }
}
