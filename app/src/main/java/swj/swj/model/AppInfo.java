package swj.swj.model;

/**
 * Created by cntoplolicon on 10/16/15.
 */
public class AppInfo {
    private String apiVersion;
    private String[] imageHosts;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String[] getImageHosts() {
        return imageHosts;
    }

    public void setImageHosts(String[] imageHosts) {
        this.imageHosts = imageHosts;
    }
}
