package swj.swj.model;

import java.io.File;
import java.util.Date;

/**
 * Created by cntoplolicon on 9/12/15.
 */
public class User {

    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    public static User current;

    private int userId;

    private String username;
    private String nickname;
    private int gender;
    private String password;
    private String biography;

    private int canInfect;
    private int infectionIndex;

    private Date createdAt;
    private Date updatedAt;

    private String avatar;
    private File avartarFile;

    private String accessToken;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public File getAvartarFile() {
        return avartarFile;
    }

    public void setAvartarFile(File avartarFile) {
        this.avartarFile = avartarFile;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
