package com.example.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalproject.R;
import com.example.finalproject.model.Notification;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notificationList;
    private OnNotificationClickListener onNotificationClickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notificationList, OnNotificationClickListener onNotificationClickListener) {
        this.notificationList = notificationList;
        this.onNotificationClickListener = onNotificationClickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewMessage;
        private TextView textViewTime;
        private TextView textViewType;
        private View viewUnreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewNotificationTitle);
            textViewMessage = itemView.findViewById(R.id.textViewNotificationMessage);
            textViewTime = itemView.findViewById(R.id.textViewNotificationTime);
            textViewType = itemView.findViewById(R.id.textViewNotificationType);
            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onNotificationClickListener != null) {
                    onNotificationClickListener.onNotificationClick(notificationList.get(position));
                }
            });
        }

        public void bind(Notification notification) {
            textViewTitle.setText(notification.getTitle());
            textViewMessage.setText(notification.getMessage());
            textViewType.setText(notification.getType());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            textViewTime.setText(sdf.format(notification.getTimestamp()));
            
            if (notification.isRead()) {
                viewUnreadIndicator.setVisibility(View.GONE);
                itemView.setAlpha(0.7f);
            } else {
                viewUnreadIndicator.setVisibility(View.VISIBLE);
                itemView.setAlpha(1.0f);
            }
        }
    }
} 