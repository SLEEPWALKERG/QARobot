package com.example.adam.qarobot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftmsg;
        TextView rightmsg;

        public ViewHolder(View view){
            super(view);
            leftlayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightlayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftmsg = (TextView) view.findViewById(R.id.left_msg);
            rightmsg = (TextView) view.findViewById(R.id.right_msg);
        }
    }

    public MsgAdapter(List<Msg> msgList){
        mMsgList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED){
            holder.leftlayout.setVisibility(View.VISIBLE);
            holder.rightlayout.setVisibility(View.GONE);
            holder.leftmsg.setText((msg.getContent()));
        }else if(msg.getType() == Msg.TYPE_SENT){
            holder.rightlayout.setVisibility(View.VISIBLE);
            holder.leftlayout.setVisibility(View.GONE);
            holder.rightmsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
