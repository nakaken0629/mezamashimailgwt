package com.appspot.mezamashimailgwt.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mezamashimailgwt implements EntryPoint {
    /**
     * Create a remote service proxy to talk to the server-side Greeting
     * service.
     */
    private final RegisteringServiceAsync registeringService = GWT
            .create(RegisteringService.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        /* email */
        final Label emailErrorLabel = new ErrorLabel();
        RootPanel.get("emailErrorLabelContainer").add(emailErrorLabel);
        final TextBox emailField = new TextBox();
        emailField.setStyleName("email");
        RootPanel.get("emailFieldContainer").add(emailField);

        /* nickname */
        final Label nicknameErrorLabel = new ErrorLabel();
        RootPanel.get("nicknameErrorLabelContainer").add(nicknameErrorLabel);
        final TextBox nicknameField = new TextBox();
        RootPanel.get("nicknameFieldContainer").add(nicknameField);

        /* wakeupDate */
        Date wakeupDate = new Date();
        /* 年のドロップダウンリストを作成するための必要な情報を保存する */
        int currentYear = wakeupDate.getYear() + 1900;

        /* 午前７時を超えていたら翌日にする */
        if (wakeupDate.getHours() >= 7) {
            wakeupDate = new Date(wakeupDate.getTime() + 1000 * 60 * 60 * 24);
        }
        final Label wakeupDateErrorLabel = new ErrorLabel();
        RootPanel.get("wakeupDateErrorLabelContainer")
                .add(wakeupDateErrorLabel);
        RootPanel wakeupDateFieldContainer = RootPanel
                .get("wakeupDateFieldContainer");
        final ListBox yearField = new ListBox();
        for (int i = 0; i <= 1; i++) {
            yearField.addItem((i + currentYear) + "", (i - 1900) + "");
        }
        yearField.setSelectedIndex(0);
        wakeupDateFieldContainer.add(yearField);
        wakeupDateFieldContainer.add(new InlineLabel("年"));
        final ListBox monthField = new ListBox();
        for (int i = 0; i <= 11; i++) {
            monthField.addItem((i + 1) + "", i + "");
        }
        monthField.setSelectedIndex(wakeupDate.getMonth());
        wakeupDateFieldContainer.add(monthField);
        wakeupDateFieldContainer.add(new InlineLabel("月"));
        final ListBox dayField = new ListBox();
        for (int i = 1; i <= 31; i++) {
            dayField.addItem(i + "");
        }
        dayField.setSelectedIndex(wakeupDate.getDate() - 1);
        wakeupDateFieldContainer.add(dayField);
        wakeupDateFieldContainer.add(new InlineLabel("日"));
        final ListBox hourOfDayField = new ListBox();
        for (int i = 0; i <= 23; i++) {
            hourOfDayField.addItem(i + "");
        }
        hourOfDayField.setSelectedIndex(7);
        wakeupDateFieldContainer.add(hourOfDayField);
        wakeupDateFieldContainer.add(new InlineLabel("時"));
        final ListBox minuteField = new ListBox();
        for (int i = 0; i < 59; i += 5) {
            minuteField.addItem(i + "");
        }
        minuteField.setSelectedIndex(0);
        wakeupDateFieldContainer.add(minuteField);
        wakeupDateFieldContainer.add(new InlineLabel("分"));

        /* submit */
        final Button submitButton = new Button();
        submitButton.setText("この時間に起こしてね");
        RootPanel.get("submitButtonContainer").add(submitButton);

        /* dialog */
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setStyleName("dialog");
        dialogBox.setTitle("お知らせだよ");
        dialogBox.setAnimationEnabled(true);
        final VerticalPanel panel = new VerticalPanel();
        dialogBox.setWidget(panel);

        /* close */
        final Button closeButton = new Button("わかった");

        /* event */
        submitButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                boolean isValid = true;
                final String email = emailField.getText();
                if ("".equals(email)) {
                    isValid = false;
                    emailErrorLabel.setText("メアドを教えてくれないと、起こせないよ");
                } else {
                    emailErrorLabel.setText("");
                }
                final String nickname = nicknameField.getText();
                if ("".equals(nickname)) {
                    isValid = false;
                    nicknameErrorLabel.setText("なんて呼べばいい？");
                } else {
                    nicknameErrorLabel.setText("");
                }
                final Date wakeupDate = new Date();
                int currentYear = wakeupDate.getYear() + 1900;
                wakeupDate.setYear(yearField.getSelectedIndex() + currentYear - 1900);
                wakeupDate.setMonth(monthField.getSelectedIndex());
                int day = dayField.getSelectedIndex() + 1;
                wakeupDate.setDate(day);
                wakeupDate.setHours(hourOfDayField.getSelectedIndex());
                wakeupDate.setMinutes(minuteField.getSelectedIndex() * 5);
                wakeupDate.setSeconds(0);
                if (wakeupDate.getDate() != day) {
                    isValid = false;
                    wakeupDateErrorLabel.setText("そんな日はないと思うな");
                } else {
                    wakeupDateErrorLabel.setText("");
                }
                if (!isValid) {
                    return;
                }

                /* 目覚ましメール登録 */
                registeringService.registerServer(email, nickname, wakeupDate,
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                panel.clear();
                                panel.add(new Label("「サーバとうまく通信できなかったの。"));
                                panel.add(new Label("もうちょっとしたら、もう一回試してみてね。」"));
                                panel.add(closeButton);
                                dialogBox.center();
                                submitButton.setEnabled(false);
                            }

                            @Override
                            public void onSuccess(Void result) {
                                panel.clear();
                                panel.add(new Label("「"
                                        + nicknameField.getText()
                                        + "、確かに目覚まし依頼を預かったよ。"));
                                DateTimeFormat format = DateTimeFormat
                                        .getFormat("d日のH時mm分");
                                panel.add(new Label(format.format(wakeupDate)
                                        + "に" + emailField.getText()
                                        + "あてに、目覚ましメールを送るね。"));
                                panel.add(new Label(
                                        "それじゃ私、これから寝るからね。メール出したら、絶対起きてよ！」"));
                                panel.add(closeButton);
                                dialogBox.center();
                                submitButton.setEnabled(false);
                            }
                        });
            }
        });

        closeButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                submitButton.setEnabled(true);
            }
        });
    }
}
