package swj.swj.common;

import android.view.View;

import com.android.volley.VolleyError;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

/**
 * Created by silentgod on 15-10-8.
 */
public class ResetViewClickable implements AlwaysCallback {
    private View view;

    public ResetViewClickable(View view) {
        this.view = view;
    }

    @Override
    public void onAlways(Promise.State state, Object resolved, Object rejected) {
        view.setEnabled(true);
    }
}
