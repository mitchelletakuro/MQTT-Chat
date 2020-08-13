package com.mitchelletakuro.mqttchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mitchelletakuro.mqttchat.R;
import com.mitchelletakuro.mqttchat.listener.LVClickListener;
import com.mitchelletakuro.mqttchat.model.ChatTopic;

import java.util.List;

public class MqttSubscribedTopicListAdapter extends RecyclerView.Adapter<MqttSubscribedTopicListAdapter.MqttSubscribedTopicViewHolder>
{
    private List<ChatTopic> chatTopicList;
    private LVClickListener lvClickListener;

    public void setLvClickListener(LVClickListener lvClickListener)
    {
        this.lvClickListener = lvClickListener;
    }

    static class MqttSubscribedTopicViewHolder extends RecyclerView.ViewHolder
    {

        TextView topicText;
        View topicDeleteButton;

        MqttSubscribedTopicViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.subscribed_topic_view);
            topicText = itemView.findViewById(R.id.subscribed_topic_text);
            topicDeleteButton = itemView.findViewById(R.id.subscribed_topic_delete_action);
        }
    }

    public MqttSubscribedTopicListAdapter(List<ChatTopic> chatTopicList)
    {
        this.setList(chatTopicList);
    }

    private void setList(List<ChatTopic> chatTopicList)
    {
        this.chatTopicList = chatTopicList;
    }

    private List<ChatTopic> getList() {
        return chatTopicList;
    }

    @NonNull
    @Override
    public MqttSubscribedTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribed_topic_list,parent,false);
        return new MqttSubscribedTopicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MqttSubscribedTopicViewHolder holder, final int position) {
        ChatTopic chatTopic = this.getList().get(position);
        if(lvClickListener != null)
        {
            holder.topicDeleteButton.setOnClickListener(view -> lvClickListener.onClick(view,position));
        }
        holder.topicText.setText(chatTopic.getTopic());
    }

    @Override
    public int getItemCount() {
         return this.getList().size();
    }

}
