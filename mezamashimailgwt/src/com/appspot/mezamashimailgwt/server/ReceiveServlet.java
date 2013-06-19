package com.appspot.mezamashimailgwt.server;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ReceiveServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ReceiveServlet.class
            .getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        /* 受信したメールから送信元のアドレスを取得する */
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);
        String email = "";
        try {
            MimeMessage message = new MimeMessage(session, req.getInputStream());
            /* 受信したメールからローカル名@ドメインの部分のみ抽出する */
            InternetAddress address = new InternetAddress(
                    message.getFrom()[0].toString());
            email = address.getAddress().toLowerCase();
        } catch (MessagingException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(e.toString());
            }
        }

        /* 送信元アドレスをキーとして依頼情報を削除する */
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Alarm alarm = pm.getObjectById(Alarm.class, email);
            pm.deletePersistent(alarm);
        } catch (JDOObjectNotFoundException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(e.toString());
            }
        } finally {
            pm.close();
        }
    }
}
