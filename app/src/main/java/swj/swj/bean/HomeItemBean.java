package swj.swj.bean;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shw on 2015/9/15.
 */
public class HomeItemBean {
    private int homeImage;
    private String homeUser;
    private String homeContext;
    private String homeMessage;
    private String HomeseeNumber;

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

    public String getHomeseeNumber() {
        return HomeseeNumber;
    }

    public HomeItemBean(int homeImage, String homeUser, String homeContext, String homeMessage, String homeseeNumber) {
        this.homeImage = homeImage;
        this.homeUser = homeUser;
        this.homeContext = homeContext;
        this.homeMessage = homeMessage;
        HomeseeNumber = homeseeNumber;
    }
}
