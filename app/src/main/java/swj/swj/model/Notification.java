package swj.swj.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by cntoplolicon on 10/13/15.
 */

@Table(name = "Notifications")
public class Notification extends Model {
    @Column
    @Expose
    private String type;
    @Column
    @Expose
    private DateTime publishTime;
    @Column
    @Expose
    private DateTime receiveTime;
    @Column
    @Expose
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(DateTime publishTime) {
        this.publishTime = publishTime;
    }

    public DateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(DateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static List<Notification> getAll() {
        return new Select().from(Notification.class).execute();
    }
}
