package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Infection;

/**
 * Created by shw on 2015/9/15.
 */
public class HomePageListItemViewsAdapter {

    public static final int STATE_CLEARED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_NORMAL = 2;

    private static HomePageListItemViewsAdapter instance;
    private Context context;

    private Map<Infection, View> infections2views = new HashMap<>();
    private int state = STATE_CLEARED;
    private boolean loading = false;
    private Callback callback;
    private int maxId;

    public static HomePageListItemViewsAdapter getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new HomePageListItemViewsAdapter(context);
    }

    private HomePageListItemViewsAdapter(Context context) {
        this.context = context;
    }

    private void updateState() {
        int oldState = state;
        if (infections2views.isEmpty()) {
            state = loading ? STATE_LOADING : STATE_CLEARED;
        } else {
            state = STATE_NORMAL;
        }
        if (oldState != state && callback != null) {
            callback.onStateChanged(oldState, state);
        }
    }

    private View createView(Infection infection) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_list_item, null);
        return view;
    }

    public View getViewAt(int index) {
        Iterator<Map.Entry<Infection, View>> iterator = infections2views.entrySet().iterator();
        Map.Entry<Infection, View> entry = null;
        while (iterator.hasNext() && index-- >= 0) {
            entry = iterator.next();
        }
        if (index >= 0) {
            loadInfections();
            return null;
        }
        if (entry.getValue() == null) {
            entry.setValue(createView(entry.getKey()));
        }
        return entry.getValue();
    }

    public Infection getInfectionByView(View view) {
        for (Map.Entry<Infection, View> entry : infections2views.entrySet()) {
            if (entry.getValue() == view) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void removeView(View view) {
        Infection infection = getInfectionByView(view);
        if (infection != null) {
            infections2views.remove(infection);
            updateState();
        }
    }

    public void loadInfections() {
        if (loading) {
            return;
        }
        RestClient.getInstance().getActiveInfections(maxId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Infection[] infections = CommonMethods.createDefaultGson().fromJson(response.toString(), Infection[].class);
                for (Infection infection : infections) {
                    maxId = Math.max(maxId, infection.getId());
                    infections2views.put(infection, null);
                }
                loading = false;
                updateState();
            }
        }, new JsonErrorListener(context, null) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                loading = false;
                updateState();
            }
        });
        loading = true;
        updateState();
    }

    public int getState() {
        return state;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onStateChanged(int oldState, int newState);
    }
}
