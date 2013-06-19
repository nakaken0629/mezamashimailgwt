package com.appspot.mezamashimailgwt.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("register")
public interface RegisteringService extends RemoteService {
    void registerServer(String email, String nickname, Date wakeupDate)
            throws IllegalArgumentException;
}
