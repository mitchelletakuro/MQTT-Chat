package com.mitchelletakuro.mqttchat.presenter;

import com.mitchelletakuro.mqttchat.view.BaseView;

public class BasePresenter<E extends BaseView>
{
    E view;

    BasePresenter(E view){
        this.attachView(view);
    }

    private void attachView(E view) { this.view = view; }

    E getView() { return this.view; }

    public void detachView(){this.view = null;}
}