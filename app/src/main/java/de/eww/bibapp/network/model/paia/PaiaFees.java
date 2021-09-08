package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaiaFees {

    @SerializedName("amount")
    private String amount;

    @SerializedName("fee")
    private List<PaiaFee> fees;

    public String getAmount() {
        return amount;
    }

    public List<PaiaFee> getFees() {
        return fees;
    }
}
