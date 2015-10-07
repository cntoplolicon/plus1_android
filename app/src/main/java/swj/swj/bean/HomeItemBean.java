package swj.swj.bean;

/**
 * Created by shw on 2015/9/15.
 */
public class HomeItemBean {
    private int homeImage;
    private String homeUser;
    private String homeContext;
    private String homeMessage;
    private String homeViews;

    public int getHomeImage() {
        return homeImage;
    }

    public String getHomeUser() {
        return homeUser;
    }

    public String getHomeContext() {
        return homeContext;
    }

    public String getHomeMessage() {
        return homeMessage;
    }

    public String gethomeViews() {
        return homeViews;
    }

    public HomeItemBean(int homeImage, String homeUser, String homeContext, String homeMessage, String homeViews) {
        this.homeImage = homeImage;
        this.homeUser = homeUser;
        this.homeContext = homeContext;
        this.homeMessage = homeMessage;
        this.homeViews = homeViews;
    }
}
