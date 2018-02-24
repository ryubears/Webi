package com.yehyunryu.android.webi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Copyright 2018 Yehyun Ryu, Nikhil Shahi, Issac Gullickson, Joshua Palm

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context mContext;
    private ArrayList<ChatMessage> mMessages;

    public ChatAdapter(Context context, ArrayList<ChatMessage> messages) {
        mContext = context;
        mMessages = messages;
    }

    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        int layoutId = R.layout.chat_item;
        boolean attachToParentImmediately = false;

        View view = layoutInflater.inflate(layoutId, parent, attachToParentImmediately);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ChatViewHolder holder, int position) {
        holder.bind(position);
    }

    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();
    }

    public void add(ChatMessage chatMessage) {
        mMessages.add(chatMessage);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mMessages == null) return 0;
        return mMessages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView mProfileImageView;
        private TextView mMessageTextView;

        public ChatViewHolder(View view) {
            super(view);
            mProfileImageView = (ImageView) view.findViewById(R.id.chat_profile);
            mMessageTextView = (TextView) view.findViewById(R.id.chat_text);
        }

        private void bind(int position) {
            ChatMessage chatMessage = mMessages.get(position);

            String profileUrl = chatMessage.getProfile();
            if(profileUrl != null) {
                displayProfilePicture(chatMessage.getProfile());
            } else {
                mProfileImageView.setImageResource(R.drawable.user_black);
            }

            mMessageTextView.setText(chatMessage.getText());
        }

        private void displayProfilePicture(String profileUrl) {
            Transformation profilePicTransformation = new RoundedTransformationBuilder()
                    .cornerRadius(100)
                    .oval(false)
                    .build();
            Picasso.with(itemView.getContext())
                    .load(profileUrl)
                    .transform(profilePicTransformation)
                    .into(mProfileImageView);
        }
    }
}
