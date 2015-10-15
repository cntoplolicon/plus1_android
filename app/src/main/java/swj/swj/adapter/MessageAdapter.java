package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.CommonMethods;
import swj.swj.common.PushNotificationService;
import swj.swj.model.Comment;
import swj.swj.model.Notification;

/**
 * Created by shw on 2015/9/14.
 */
public class MessageAdapter extends BaseAdapter {

    private int[] image = new int[]{R.drawable.open};
    private List<Notification> notifications;
    private LayoutInflater mInflater;

    public MessageAdapter(Context context, List<Notification> notifications) {
        mInflater = LayoutInflater.from(context);
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Notification getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Notification notification = notifications.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.message_list_item, null);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_nickname);
            viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.messageType = (TextView) convertView.findViewById(R.id.tv_message);
            viewHolder.open = (ImageView) convertView.findViewById(R.id.iv_open);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (notification != null && notification.getType().equals(PushNotificationService.TYPE_COMMENT)) {
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            viewHolder.userName.setText(comment.getUser().getNickname());
            viewHolder.messageType.setText(comment.getReplyToId() == 0 ? R.string.notification_card : R.string.notification_comment);
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + comment.getUser().getAvatar(), viewHolder.userAvatar);
        }
        viewHolder.open.setImageResource(image[0]);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView userAvatar;
        private TextView userName;
        private TextView messageType;
        private ImageView open;
    }
}
