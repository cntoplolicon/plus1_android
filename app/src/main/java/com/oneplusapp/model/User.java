package com.oneplusapp.model;

import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.LocalUserInfo;

import org.joda.time.DateTime;

/**
 * Created by cntoplolicon on 9/12/15.
 */
public class User {

    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    public static final String CURRENT_USER_KEY = "user";

    public static volatile User current;
    private static UserChangedCallback userChangedCallback;

    private int id;

    private String username;
    private String nickname;
    private int gender;
    private String biography;
    private String avatar;
    private DateTime createdAt;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
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

    public static void updateCurrentUser(String json) {
        User oldUser = current;
        current = CommonMethods.createDefaultGson().fromJson(json, User.class);
        LocalUserInfo.getPreferences().edit().putString(CURRENT_USER_KEY, json).commit();
        tryCallUserChangedCallback(oldUser, current);
    }

    public static void clearCurrentUser() {
        User oldUser = current;
        current = null;
        LocalUserInfo.getPreferences().edit().remove(CURRENT_USER_KEY).commit();
        tryCallUserChangedCallback(oldUser, current);
    }

    private static void tryCallUserChangedCallback(User oldUser, User newUser) {
        if (oldUser == null && newUser == null || userChangedCallback == null) {
            return;
        }
        if (oldUser != null && newUser == null || oldUser == null && newUser != null ||
                oldUser.getId() != newUser.getId()) {
            userChangedCallback.onUserChanged(oldUser, newUser);
        }
    }

    public static void setUserChangedCallback(UserChangedCallback callback) {
        userChangedCallback = callback;
    }

    public interface UserChangedCallback {
        void onUserChanged(User oldUser, User newUser);
    }
}
