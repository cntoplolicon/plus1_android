package com.oneplusapp.common;

import android.os.AsyncTask;

import org.jdeferred.Promise;

import java.util.concurrent.CancellationException;

/**
 * Created by cntoplolicon on 10/27/15.
 */
public abstract class ThrowableDeferredAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private ThrowableDeferredObject<Result, Throwable, Progress> deferred = new ThrowableDeferredObject<>();

    @Override
    protected final void onCancelled() {
        deferred.reject(new CancellationException());
    }

    protected final void onCancelled(Result result) {
        deferred.reject(new CancellationException());
    }

    @Override
    protected final void onPostExecute(Result result) {
        deferred.resolve(result);
    }

    @Override
    protected final void onProgressUpdate(Progress... values) {
        if (values == null || values.length == 0) {
            deferred.notify(null);
        } else if (values.length == 1) {
            deferred.notify(values[0]);
        } else {
            deferred.notify(values[0]);
        }
    }

    public Promise<Result, Throwable, Progress> promise() {
        return deferred.promise();
    }
}
