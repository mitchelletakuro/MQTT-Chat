package com.mitchelletakuro.mqttchat.view;


import com.mitchelletakuro.mqttchat.model.ChatMsg;
import com.mitchelletakuro.mqttchat.model.ChatTopic;

import java.util.List;

public interface MainView extends BaseView {

    void onSuccessConnection();

    void onSuccessAddSubscriptionTopic();

    void onFailedConnection(String message);

    void onFailedAddSubscriptionTopic(String message);

    void showSubscribeDialog(List<ChatTopic> chatTopicList);

    void onFailedSendTopicMessage(String message);

    void onSuccessSendTopicMessage();

    void updateSubscribedTopics(List<ChatTopic> chatTopicList);

    void updateTopicMessages(List<ChatMsg> chatMsgList);

    void onSuccessUnsubscribe();

    void onFailedUnsubscribe(String message);

    void showSubscribedTopics(List<ChatTopic> chatTopicList);

    void showTopicMessageDeleteDialog(int position);
}
