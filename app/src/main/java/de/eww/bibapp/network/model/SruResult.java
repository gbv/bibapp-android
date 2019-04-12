package de.eww.bibapp.network.model;

import java.util.HashMap;

public class SruResult {

    private HashMap<String, Object> result = new HashMap<>();

    public HashMap<String, Object> getResult()
    {
        return result;
    }

    public void setResult(HashMap<String, Object> result)
    {
        this.result = result;
    }
}
