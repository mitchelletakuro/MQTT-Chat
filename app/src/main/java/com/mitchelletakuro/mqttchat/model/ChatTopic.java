package com.mitchelletakuro.mqttchat.model;

public class ChatTopic {

    private String topic;

    public ChatTopic(String topic)
    {
        this.setTopic(topic);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
