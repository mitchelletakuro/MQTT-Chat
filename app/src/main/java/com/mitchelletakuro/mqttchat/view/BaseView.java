package com.mitchelletakuro.mqttchat.view;

public interface BaseView {

    void showProgress();

    void showProgress(String title);

    void hideProgress();

    void onSuccess(String message);

    void onFailed(String message);
}

