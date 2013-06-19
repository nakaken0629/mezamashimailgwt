package com.appspot.mezamashimailgwt.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;

@SuppressWarnings("serial")
public class WakeupTaskServlet extends HttpServlet {
    private static final Logger logger = Logger
            .getLogger(WakeupTaskServlet.class.getName());

    /* 制御サーブレットで登録された送信サーブレットが、タスクキューから呼び出される */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        /* 処理全体をtry ～ catch句で囲い、例外を呼び出し元に返さないようにする */
        try {
            updateAlarm(req);
        } catch (Exception e) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.severe(e.toString());
            }
        }
    }

    /* Alarmエンティティを更新してメール送信処理を呼び出す */
    private void updateAlarm(HttpServletRequest req) throws IOException {
        String email = req.getParameter("email");
        String nickname = "";
        int count = 0;
        /* 依頼情報を更新する */
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Alarm alarm = pm.getObjectById(Alarm.class, email);
            nickname = alarm.getNickname();
            count = alarm.getCount();
            /* Alarmエンティティを更新する */
            if (count == 0) {
                /* 初回であれば5分後に再送する */
                alarm.setCount(count + 1);
                Calendar calendar = Calendar.getInstance(TimeZone
                        .getTimeZone("Asia/Tokyo"));
                calendar.setTime(alarm.getWakeupDate());
                calendar.add(Calendar.MINUTE, 5);
                alarm.setWakeupDate(calendar.getTime());
                pm.makePersistent(alarm);
            } else {
                /* 2回目以降であれば依頼を削除する */
                pm.deletePersistent(alarm);
            }
            /* Alarmエンティティが更新できたらメールを送信する */
            sendMail(email, nickname, count);
        } finally {
            pm.close();
        }
    }

    /* メールを送信する */
    private void sendMail(String email, String nickname, int count)
            throws IOException {
        Message message = new Message();
        message.setSender("eri@mezamashimailgwt.appspotmail.com");
        message.setTo(email);
        if (count == 0) {
            message.setSubject("時間だよー");
            message.setTextBody(nickname + "\n" + "頼まれてた時間だよー。\n"
                    + "予定があるんでしょ？　早く準備してね。");
        } else {
            message.setSubject("大変ー！");
            message.setTextBody(nickname + " 大変大変ー！\n" + "時間過ぎてるよ！\n"
                    + "早く、早く！！");
        }
        MailService mailService = MailServiceFactory.getMailService();
        mailService.send(message);
    }
}
