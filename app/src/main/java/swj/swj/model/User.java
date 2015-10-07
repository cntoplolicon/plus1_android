package swj.swj.model;

import org.joda.time.DateTime;

import swj.swj.common.CommonMethods;
import swj.swj.common.LocalUserInfo;

/**
 * Created by cntoplolicon on 9/12/15.
 */
public class User {

    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    public static final String CURRENT_USER_KEY = "user";

    public static volatile User current;

    private int id;

    private String username;
    private String nickname;
    private int gender;
    private String password;
    private String biography;

    private int canInfect;
    private int infectionIndex;

    private DateTime createdAt;
    private DateTime updatedAt;

    private String avatar;

    private String accessToken;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getCanInfect() {
        return canInfect;
    }

    public void setCanInfect(int canInfect) {
        this.canInfect = canInfect;
    }

    public int getInfectionIndex() {
        return infectionIndex;
    }

    public void setInfectionIndex(int infectionIndex) {
        this.infectionIndex = infectionIndex;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static User fromJson(String json) {
        return CommonMethods.createDefaultGson().fromJson(json, User.class);
    }

    public static void updateCurrentUser(String json) {
        current = User.fromJson(json);
        LocalUserInfo.getInstance().setUserInfo(CURRENT_USER_KEY, json);
    }

    public static void clearCurrentUser() {
        current = null;
        LocalUserInfo.getInstance().setUserInfo(CURRENT_USER_KEY, "");
    }
}
