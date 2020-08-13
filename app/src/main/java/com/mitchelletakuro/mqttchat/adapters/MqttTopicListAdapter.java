package com.mitchelletakuro.mqttchat.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mitchelletakuro.mqttchat.R;
import com.mitchelletakuro.mqttchat.listener.LVClickListener;
import com.mitchelletakuro.mqttchat.model.ChatContentType;
import com.mitchelletakuro.mqttchat.model.ChatMsg;

import java.util.List;

public class MqttTopicListAdapter extends
        RecyclerView.Adapter<MqttTopicListAdapter.MqttTopicMessageViewHolder>
{
    private List<ChatMsg> chatMsgList;
    private LVClickListener itemClickListener;
    private LVClickListener deleteItemClickListener;

    public void setItemClickListener(LVClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public void setDeleteItemClickListener(LVClickListener deleteItemClickListener)
    {
        this.deleteItemClickListener = deleteItemClickListener;
    }

    static class MqttTopicMessageViewHolder extends RecyclerView.ViewHolder{

        View topicMessageView;
        View messageDeleteButton;
        TextView messageTopic, messageStatus, messageDate, messageText;

        MqttTopicMessageViewHolder(View itemView) {
            super(itemView);
            topicMessageView = itemView.findViewById(R.id.topic_message_view);
            messageTopic = itemView.findViewById(R.id.topic_text);
            messageStatus = itemView.findViewById(R.id.status_text);
            messageDate = itemView.findViewById(R.id.date_text);
            messageText = itemView.findViewById(R.id.message_text);
            messageDeleteButton = itemView.findViewById(R.id.message_delete_action);
        }
    }

    public MqttTopicListAdapter(List<ChatMsg> chatMsgList)
    {
        this.setList(chatMsgList);
    }

    private void setList(List<ChatMsg> chatMsgList)
    {
        this.chatMsgList = chatMsgList;
    }

    private List<ChatMsg> getList() {
        return chatMsgList;
    }

    @NonNull
    @Override
    public MqttTopicMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_message_list,parent,false);
        return new MqttTopicMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MqttTopicMessageViewHolder holder, final int position) {
        ChatMsg chatMsg = this.getList().get(position);
        if(itemClickListener != null)
        {
            holder.topicMessageView.setOnClickListener(view -> itemClickListener.onClick(view,position));
        }

        if(deleteItemClickListener != null)
        {
            holder.messageDeleteButton.setOnClickListener(view -> deleteItemClickListener.onClick(view,position));
        }

        holder.messageDeleteButton.setVisibility(View.GONE);
        if(chatMsg.getType().equals(ChatContentType.RECEIVED))
        {
            holder.topicMessageView.setBackgroundResource(R.drawable.received_msg_bg);
        }
        else if(chatMsg.getType().equals(ChatContentType.SENT))
        {
            holder.topicMessageView.setBackgroundResource(R.drawable.sent_msg_bg);
        }
        else if(chatMsg.getType().equals(ChatContentType.PENDING))
        {
            holder.topicMessageView.setBackgroundResource(R.drawable.pending_bg);
            holder.messageDeleteButton.setVisibility(View.VISIBLE);
        }
        else if(chatMsg.getType().equals(ChatContentType.DELETED))
        {
            holder.topicMessageView.setBackgroundResource(R.drawable.deleted_message_background);
        }
        holder.messageTopic.setText(chatMsg.getTopic());
        holder.messageStatus.setText(chatMsg.getType().toString());
        holder.messageDate.setText(chatMsg.getDate());
        holder.messageText.setText(new String(chatMsg.getMessage().getPayload()));
    }

    @Override
    public int getItemCount() {
         return this.getList().size();
    }

}
