package de.eww.bibapp.network.model;

public class StatefullData<T> {
    private T data;

    private boolean error;

    public StatefullData(T data, boolean error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public boolean getError() {
        return error;
    }
}
