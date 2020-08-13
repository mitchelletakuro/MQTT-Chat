package com.mitchelletakuro.mqttchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.mitchelletakuro.mqttchat.R;
import com.mitchelletakuro.mqttchat.presenter.BasePresenter;
import com.mitchelletakuro.mqttchat.view.BaseView;

import id.ionbit.ionalert.IonAlert;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    protected IonAlert alert;
    protected abstract BasePresenter getPresenter();

    @Override
    protected void onDestroy() {
        if(getPresenter() != null)
        {
            getPresenter().detachView();
        }
        hideProgress();
        super.onDestroy();
    }


    @Override
    public void showProgress() {

        alert =  new IonAlert (this, IonAlert.PROGRESS_TYPE)
                 .setTitleText(getString(R.string.alert_loading_title))
                 .setContentText(getString(R.string.alert_loading_description));
        alert.show();


    }


    @Override
    public void showProgress(String title) {
        alert =  new IonAlert (this, IonAlert.PROGRESS_TYPE)
                .setTitleText(getString(R.string.alert_loading_description))
                .setContentText(getString(R.string.alert_loading_description));
        alert.show();

    }

    @Override
    public void hideProgress() {
        if ((alert != null) && alert.isShowing()) {
            alert.dismiss();
        }
    }



    @Override
    public void onSuccess(String message) {

        alert =  new IonAlert (this, IonAlert.SUCCESS_TYPE)
                .setTitleText(getString(R.string.alert_success_title))
                .setContentText(message)
                .setConfirmClickListener(ionAlert -> alert.dismissWithAnimation());

        alert.show();

    }



    @Override
    public void onFailed(String message) {

        alert =  new IonAlert (this, IonAlert.ERROR_TYPE)
                .setTitleText(getString(R.string.alert_error_title))
                .setContentText(message)
                .setConfirmClickListener(ionAlert -> alert.dismissWithAnimation());

        alert.show();


    }
}
