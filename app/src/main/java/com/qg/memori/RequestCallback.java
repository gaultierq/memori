package com.qg.memori;

/**
 * Created by q on 10/07/2016.
 */
public interface RequestCallback<T> {

    void onSuccess(T data);
}
