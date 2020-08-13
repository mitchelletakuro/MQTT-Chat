package com.mitchelletakuro.mqttchat.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mitchelletakuro.mqttchat.R;
import com.mitchelletakuro.mqttchat.adapters.MqttSubscribedTopicListAdapter;
import com.mitchelletakuro.mqttchat.adapters.MqttTopicListAdapter;
import com.mitchelletakuro.mqttchat.listener.LVClickListener;
import com.mitchelletakuro.mqttchat.model.ChatMsg;
import com.mitchelletakuro.mqttchat.model.ChatTopic;
import com.mitchelletakuro.mqttchat.presenter.BasePresenter;
import com.mitchelletakuro.mqttchat.presenter.MainPresenter;
import com.mitchelletakuro.mqttchat.view.MainView;

import java.util.List;

public class MainActivity extends BaseActivity implements MainView {


    MainPresenter presenter;
    EditText msgEt, msgTopicEt;
    Button sendBtn;
    RecyclerView topicListRV;
    RecyclerView subscribedTopicListRV;
    MaterialDialog subscribedTopicsDialog;
    MqttTopicListAdapter mqttTopicListAdapter;
    MqttSubscribedTopicListAdapter mqttSubscribedTopicListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);
        msgTopicEt = findViewById(R.id.message_topic_edittext);
        msgEt = findViewById(R.id.message_edittext);
        sendBtn = findViewById(R.id.send_message_button);
        topicListRV = findViewById(R.id.message_list_view);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.handleSendMessageAction(msgTopicEt.getText().toString(), msgEt.getText().toString());
            }
        });

        presenter.initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_add_subscription:
                presenter.handleSubscribeAction();
                break;
            case R.id.menu_action_subscribed_topics:
                presenter.handleShowSubscribedTopicsAction();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onSuccessConnection() {
        this.onSuccess(getString(R.string.mqtt_connection_success_message));
    }

    @Override
    public void onSuccessAddSubscriptionTopic() {
        this.onSuccess(getString(R.string.topic_subscription_success_message));
    }

    @Override
    public void onFailedConnection(String message) {
        this.onFailed(message);
    }

    @Override
    public void onFailedAddSubscriptionTopic(String message) {
        this.onFailed(message);
    }

    @Override
    public void showSubscribeDialog(final List<ChatTopic> chatTopicList) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.subscribe_message)
                .customView(R.layout.add_topic_subscription, true)
                .positiveText(R.string.subscribe_label)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText topicEdittext;
                        if (dialog.getCustomView() != null) {
                            topicEdittext = dialog.getCustomView().findViewById(R.id.subscription_topic_edittext);
                            presenter.handleSubscribeToTopicAction(topicEdittext.getText().toString());
                        }
                    }
                }).build();
        dialog.show();
    }

    @Override
    public void onFailedSendTopicMessage(String message) {
        this.onFailed(message);
    }

    @Override
    public void onSuccessSendTopicMessage() {
        msgEt.setText("");

    }

    @Override
    public void updateSubscribedTopics(List<ChatTopic> chatTopicList) {
        mqttSubscribedTopicListAdapter = new MqttSubscribedTopicListAdapter(chatTopicList);
        mqttSubscribedTopicListAdapter.setDeleteItemClickListener(new LVClickListener() {
            @Override
            public void onClick(View view, int position) {
                presenter.handleUnsubscribeAction(position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        subscribedTopicListRV.setHasFixedSize(true);
        subscribedTopicListRV.setLayoutManager(mLayoutManager);
        subscribedTopicListRV.setItemAnimator(new DefaultItemAnimator());
        subscribedTopicListRV.setAdapter(mqttSubscribedTopicListAdapter);
    }

    @Override
    public void updateTopicMessages(List<ChatMsg> chatMsgList) {
        mqttTopicListAdapter = new MqttTopicListAdapter(chatMsgList);
        mqttTopicListAdapter.setDeleteItemClickListener(new LVClickListener() {
            @Override
            public void onClick(View view, int position) {
                presenter.handleDeleteTopicMessageAction(position);
            }
        });
        mqttTopicListAdapter.setItemClickListener(new LVClickListener() {
            @Override
            public void onClick(View view, int position) {
                presenter.handleTopicMessageClickAction(position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        topicListRV.setHasFixedSize(true);
        topicListRV.setLayoutManager(mLayoutManager);
        topicListRV.setItemAnimator(new DefaultItemAnimator());
        topicListRV.setAdapter(mqttTopicListAdapter);
    }

    @Override
    public void onSuccessUnsubscribe() {
        this.onSuccess(getString(R.string.unsubscribe_success_message));
    }

    @Override
    public void onFailedUnsubscribe(String message) {
        this.onFailed(message);
    }

    @Override
    public void showSubscribedTopics(List<ChatTopic> chatTopicList) {
        mqttSubscribedTopicListAdapter = new MqttSubscribedTopicListAdapter(chatTopicList);
        mqttSubscribedTopicListAdapter.setDeleteItemClickListener(new LVClickListener() {
            @Override
            public void onClick(View view, int position) {
                subscribedTopicsDialog.dismiss();
                presenter.handleUnsubscribeAction(position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        subscribedTopicsDialog = new MaterialDialog.Builder(this)
                .title(R.string.subscribed_topics_text)
                .negativeText(android.R.string.cancel)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                })
                .adapter(mqttSubscribedTopicListAdapter, mLayoutManager)
                .build();
        subscribedTopicsDialog.show();
    }

    @Override
    public void showTopicMessageDeleteDialog(final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_delete_message)
                .content("Are you sure you want to continue this action?")
                .positiveText(R.string.delete_text)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        presenter.deleteTopicMessage(position);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();
    }
}
