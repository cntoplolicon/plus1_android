package swj.swj.bean;

/**
 * Created by shw on 2015/9/21.
 */
public class CardDetailsItemBean {
    private int imageView;
    private String userName;
    private String content;

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageView() {
        return imageView;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public CardDetailsItemBean(int imageView, String userName, String content) {
        this.imageView = imageView;
        this.userName = userName;
        this.content = content;
    }
}
