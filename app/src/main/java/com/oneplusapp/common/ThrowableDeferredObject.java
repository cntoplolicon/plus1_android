package com.oneplusapp.common;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.impl.DeferredObject;

public class ThrowableDeferredObject<D, F, P> extends DeferredObject<D, F, P> {

    @Override
    protected void triggerDone(D resolved) {
        for (DoneCallback<D> callback : doneCallbacks) {
            triggerDone(callback, resolved);
        }
        doneCallbacks.clear();
    }

    @Override
    protected void triggerFail(F rejected) {
        for (FailCallback<F> callback : failCallbacks) {
            triggerFail(callback, rejected);
        }
        failCallbacks.clear();
    }

    @Override
    protected void triggerProgress(P progress) {
        for (ProgressCallback<P> callback : progressCallbacks) {
            triggerProgress(callback, progress);
        }
    }

    @Override
    protected void triggerAlways(State state, D resolve, F reject) {
        for (AlwaysCallback<D, F> callback : alwaysCallbacks) {
            triggerAlways(callback, state, resolve, reject);
        }
        alwaysCallbacks.clear();

        synchronized (this) {
            this.notifyAll();
        }
    }
}
