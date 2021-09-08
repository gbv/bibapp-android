package de.eww.bibapp.network.repository;

import de.eww.bibapp.network.Result;

public interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}
