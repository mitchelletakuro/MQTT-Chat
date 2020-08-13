package com.mitchelletakuro.mqttchat.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChatMsg {

    private String topic;

    private MqttMessage message;

    private ChatContentType type;

    private String date;

    public ChatMsg(String topic, MqttMessage message, ChatContentType type)
    {
        this.setTopic(topic);
        this.setMessage(message);
        this.setType(type);
        this.setDate();
    }

    public String getDate() {
        return date;
    }

    private void setDate() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.UK);
        this.date = dateFormat.format(Calendar.getInstance().getTime());
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttMessage getMessage() {
        return message;
    }

    public void setMessage(MqttMessage message) {
        this.message = message;
    }

    public ChatContentType getType() {
        return type;
    }

    public void setType(ChatContentType type) {
        this.type = type;
    }
}
