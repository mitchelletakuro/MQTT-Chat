package com.mitchelletakuro.mqttchat.presenter;


import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.mitchelletakuro.mqttchat.R;
import com.mitchelletakuro.mqttchat.model.ChatContentType;
import com.mitchelletakuro.mqttchat.model.ChatMsg;
import com.mitchelletakuro.mqttchat.model.ChatTopic;
import com.mitchelletakuro.mqttchat.view.MainView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPresenter extends BasePresenter<MainView> {

    private String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private String clientId = MqttClient.generateClientId();

    private MqttAndroidClient mttqClient;

    private List<ChatMsg> chatMsgList = new ArrayList<>();
    private List<ChatMsg> chatMsgWaitingList = new ArrayList<>();
    private List<ChatTopic> chatTopicList = new ArrayList<>();

    public MainPresenter(MainView view) {
        super(view);
    }

    private MqttAndroidClient getMttqClient() {
        return mttqClient;
    }

    private void setMttqClient() {
        this.mttqClient = null;
    }

    public void initialize() {
        if (this.getMttqClient() == null) {
            this.connect(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MainPresenter.this.setClientCallback(mttqClient);
                    view.hideProgress();
                    view.onSuccessConnection();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    view.hideProgress();
                    view.onFailedConnection(exception.getMessage());
                }
            });
        }
    }

    private void connect(IMqttActionListener listener) {
        view.showProgress(((AppCompatActivity)view).getString(R.string.alert_connection_loader_title));
        mttqClient = new MqttAndroidClient((Context) view, BROKER_URL, clientId);
        try {
            IMqttToken token = mttqClient.connect();
            token.setActionCallback(listener);
        } catch (MqttException e) {
            view.hideProgress();
            view.onFailedConnection(e.getMessage());
        }
    }

    private void setClientCallback(MqttAndroidClient client) {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                MainPresenter.this.setMttqClient();
            }

            @Override
            public void messageArrived(String topic, MqttMessage msg) {
                chatMsgList.add(new ChatMsg(topic, msg, ChatContentType.RECEIVED));
                view.updateTopicMessages(chatMsgList);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void handleSubscribeAction() {
        view.showSubscribeDialog(chatTopicList);
    }

    private boolean validateSubscriptionTopic(String topic) {
        boolean topicExists = false;
        if (topic == null || topic.trim().isEmpty()) {
            view.hideProgress();
            view.onFailedAddSubscriptionTopic(((AppCompatActivity)getView()).getString(R.string.subscription_topic_empty_message));
            return false;
        }
        for (ChatTopic chatTopic : chatTopicList)
        {
            if (chatTopic.getTopic().equals(topic))
            {
                topicExists = true;
                break;
            }
        }
        if(topicExists)
        {
            view.hideProgress();
            view.onFailedAddSubscriptionTopic(((AppCompatActivity)getView()).getString(R.string.subscription_exists_message));
            return false;
        }
        return true;
    }

    public void handleSubscribeToTopicAction(final String topic) {
        if (this.validateSubscriptionTopic(topic)) {
            if (this.getMttqClient() != null && this.getMttqClient().isConnected())
            {
                this.subscribeToTopic(topic);
            }
            else
            {
                this.connect(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        subscribeToTopic(topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        view.hideProgress();
                        view.onFailedConnection(exception.getMessage());
                    }
                });
            }
        }
    }

    private void subscribeToTopic(final String topic) {
        view.showProgress();
        int qos = 1;
        try
        {
            IMqttToken subToken = getMttqClient().subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    chatTopicList.add(new ChatTopic(topic));
                    view.hideProgress();
                    view.onSuccessAddSubscriptionTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    view.hideProgress();
                    view.onFailedAddSubscriptionTopic(exception.getMessage());
                }
            });
        }
        catch (MqttException e)
        {
            view.hideProgress();
            view.onFailedAddSubscriptionTopic(e.getMessage());
        }
    }

    private boolean validateTopicMessage(String topic, String message) {
        if (topic == null || topic.trim().isEmpty()) {
            view.hideProgress();
            view.onFailedSendTopicMessage("Topic is empty");
            return false;
        } else if (message == null || message.trim().isEmpty()) {
            view.hideProgress();
            view.onFailedSendTopicMessage("Message is empty");
            return false;
        }
        return true;
    }

    public void handleSendMessageAction(final String topic, final String message)
    {
        if (this.validateTopicMessage(topic, message))
        {
            if (this.getMttqClient() != null && this.getMttqClient().isConnected()) {
                this.sendTopicMessage(topic, message);
            } else {
                this.connect(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        sendTopicMessage(topic, message);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        view.hideProgress();
                        view.onFailedConnection(exception.getMessage());
                    }
                });
            }
        }
    }

    private void sendTopicMessage(final String topic, final String topicMessage) {
        byte[] encodedMessage;
        try {
            encodedMessage = topicMessage.getBytes("UTF-8");
            final MqttMessage message = new MqttMessage(encodedMessage);

            ChatMsg waitingMessage = new ChatMsg(topic, message, ChatContentType.PENDING);
            chatMsgList.add(waitingMessage);
            chatMsgWaitingList.add(waitingMessage);
            view.updateTopicMessages(chatMsgList);
            view.onSuccessSendTopicMessage();

            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            ChatMsg pendingTopicMessage = chatMsgWaitingList.get(0);
                            if(pendingTopicMessage.getType().equals(ChatContentType.PENDING))
                            {
                                try {
                                    IMqttToken messageToken = mttqClient.publish(topic, message);
                                    messageToken.setActionCallback(new IMqttActionListener() {
                                        @Override
                                        public void onSuccess(IMqttToken asyncActionToken) {
                                            chatMsgWaitingList.get(0).setType(ChatContentType.SENT);
                                            chatMsgWaitingList.remove(0);
                                            view.hideProgress();
                                            view.updateTopicMessages(chatMsgList);
                                        }

                                        @Override
                                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                            view.hideProgress();
                                            view.onFailedSendTopicMessage(exception.getMessage());
                                        }
                                    });
                                }
                                catch (MqttException e)
                                {
                                    view.hideProgress();
                                    view.onFailedAddSubscriptionTopic(e.getMessage());
                                }
                            }
                            else
                            {
                                chatMsgWaitingList.remove(0);
                            }
                        }
                    }, 5000);
        } catch (Exception e) {
            view.hideProgress();
            view.onFailedAddSubscriptionTopic(e.getMessage());
        }
    }

    public void handleUnsubscribeAction(final int position) {
        if (this.getMttqClient() != null && this.getMttqClient().isConnected()) {
            this.unsubscribe(position);
        } else {
            this.connect(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    unsubscribe(position);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    view.hideProgress();
                    view.onFailedConnection(exception.getMessage());
                }
            });
        }
    }

    private void unsubscribe(final int position) {
        String topic = chatTopicList.get(position).getTopic();
        try {
            IMqttToken unsubToken = mttqClient.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    chatTopicList.remove(position);
                    view.showSubscribedTopics(chatTopicList);
                    view.onSuccessUnsubscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    view.onFailedUnsubscribe(exception.getMessage());
                }
            });
        } catch (MqttException e) {
            view.onFailedUnsubscribe(e.getMessage());
        }
    }

    public void handleShowSubscribedTopicsAction() {
        view.showSubscribedTopics(chatTopicList);
    }

    public void handleDeleteTopicMessageAction(int position) {
        chatMsgList.get(position).setType(ChatContentType.DELETED);
        view.updateTopicMessages(chatMsgList);
    }

    public void handleTopicMessageClickAction(int position) {
        if(!chatMsgList.get(position).getType().equals(ChatContentType.PENDING))
        {
            view.showTopicMessageDeleteDialog(position);
        }
    }

    public void deleteTopicMessage(int position)
    {
        chatMsgList.remove(position);
        view.updateTopicMessages(chatMsgList);
    }
}
