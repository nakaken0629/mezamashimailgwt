package com.appspot.mezamashimailgwt.server;

import java.util.Date;

import javax.jdo.PersistenceManager;

import com.appspot.mezamashimailgwt.client.RegisteringService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RegisteringServiceImpl extends RemoteServiceServlet implements
        RegisteringService {

    public void registerServer(String email, String nickname, Date wakeupDate)
            throws IllegalArgumentException {
        /* 登録処理 */
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Alarm alarm = new Alarm(email.toLowerCase(), nickname, wakeupDate);
            pm.makePersistent(alarm);
        } finally {
            pm.close();
        }
    }
}
