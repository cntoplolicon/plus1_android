package swj.swj.common;

import android.view.View;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

/**
 * Created by silentgod on 15-10-8.
 */
public class ResetViewClickable<D, R> implements AlwaysCallback<D, R> {
    private View view;

    public ResetViewClickable(View view) {
        this.view = view;
    }

    @Override
    public void onAlways(Promise.State state, D resolved, R rejected) {
        view.setEnabled(true);
    }
}
