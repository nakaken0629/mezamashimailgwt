package com.appspot.mezamashimailgwt.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;

@SuppressWarnings("serial")
public class WakeupServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            /* 依頼時刻が過ぎた目覚まし依頼を、Queryインターフェイスを使って検索する */
            Query query = pm.newQuery(Alarm.class);
            query.setFilter("wakeupDate <= :currentDate");
            query.setOrdering("wakeupDate");
            List<Alarm> alarms = (List<Alarm>) query.execute(new Date());
            for (Alarm alarm : alarms) {
                /* タスクキューに送信サーブレットを登録する */
                Queue queue = QueueFactory.getQueue("wakeup-queue");
                queue.add(Builder.withUrl("/task/wakeuptask").param("email",
                        alarm.getEmail()));
            }
        } finally {
            pm.close();
        }
    }
}
