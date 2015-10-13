package swj.swj.model;

import org.joda.time.DateTime;

/**
 * Created by cntoplolicon on 10/13/15.
 */
public class Notification {
    public String type;
    private DateTime publishTime;
    private DateTime receiveTime;
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
}
