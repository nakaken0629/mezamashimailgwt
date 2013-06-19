package com.appspot.mezamashimailgwt.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface RegisteringServiceAsync {
    void registerServer(String email, String nickname, Date wakeupDate,
            AsyncCallback<Void> callback) throws IllegalArgumentException;
}
