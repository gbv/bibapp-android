package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaItems;

public class ItemsResult {

    private PaiaItems success;

    private Integer error;

    public ItemsResult(Integer error) {
        this.error = error;
    }

    public ItemsResult(PaiaItems success) {
        this.success = success;
    }

    public PaiaItems getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
