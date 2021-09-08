package de.eww.bibapp.viewmodel;

public class ExportResult {

    private String success;

    private Integer error;

    public ExportResult(Integer error) {
        this.error = error;
    }

    public ExportResult(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
