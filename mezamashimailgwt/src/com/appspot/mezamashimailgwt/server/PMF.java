package com.appspot.mezamashimailgwt.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
    private static final PersistenceManagerFactory pmfInstance = JDOHelper
            .getPersistenceManagerFactory("transactions-optional");

    private PMF() { /* 何もしない */ }

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}