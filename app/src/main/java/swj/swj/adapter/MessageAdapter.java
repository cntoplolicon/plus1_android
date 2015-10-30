package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.model.Comment;
import swj.swj.model.Notification;

/**
 * Created by shw on 2015/9/14.
 */
public class MessageAdapter extends ArrayAdapter<Notification> {

    private LayoutInflater mInflater;

    public MessageAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.message_list_item, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);
        Notification notification = getItem(position);
        view.setTag(notification);

        Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
        viewHolder.tvNickname.setText(comment.getUser().getNickname());
        CommonMethods.chooseNicknameColorViaGender(viewHolder.tvNickname, comment.getUser(), getContext());
        viewHolder.tvMessage.setText(comment.getReplyToId() == 0 ? R.string.message_card : R.string.message_comment);
        ImageLoader.getInstance().cancelDisplayTask(viewHolder.ivAvatar);
        if (comment.getUser().getAvatar() == null) {
            viewHolder.ivAvatar.setImageResource(R.drawable.default_useravatar);
        } else {
            ImageLoader.getInstance().displayImage(comment.getUser().getAvatar(), viewHolder.ivAvatar);
        }
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        @Bind(R.id.tv_nickname)
        TextView tvNickname;
        @Bind(R.id.tv_message)
        TextView tvMessage;
    }
}
