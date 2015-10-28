package swj.swj.model;

/**
 * Created by cntoplolicon on 10/16/15.
 */
public class AppInfo {
    private int versionCode;
    private String[] imageHosts;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String[] getImageHosts() {
        return imageHosts;
    }

    public void setImageHosts(String[] imageHosts) {
        this.imageHosts = imageHosts;
    }
}
