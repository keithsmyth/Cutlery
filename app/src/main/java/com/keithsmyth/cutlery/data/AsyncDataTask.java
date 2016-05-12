package com.keithsmyth.cutlery.data;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

public abstract class AsyncDataTask<OUT> {

    private final boolean allowNullReturn;

    @Nullable private AsyncDataTaskListener<OUT> listener;

    public AsyncDataTask(boolean allowNullReturn) {
        this.allowNullReturn = allowNullReturn;
    }

    public abstract OUT task();

    public AsyncDataTask<OUT> setListener(@Nullable AsyncDataTaskListener<OUT> listener) {
        this.listener = listener;
        return this;
    }

    public AsyncDataTask<OUT> execute() {
        new Async().execute();
        return this;
    }

    public static void clear(AsyncDataTask<?>... params) {
        for (AsyncDataTask<?> asyncDataTask : params) {
            if (asyncDataTask != null) {
                asyncDataTask.setListener(null);
            }
        }
    }

    private class Async extends AsyncTask<Void, Void, OUT> {

        private Exception e;

        @Override
        protected OUT doInBackground(Void... params) {
            try {
                return task();
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(OUT out) {
            if (listener == null) {
                return;
            }
            if (e != null) {
                listener.onError(e);
            } else if (allowNullReturn || out != null) {
                listener.onSuccess(out);
            } else {
                listener.onError(new Exception("Unknown error"));
            }
        }
    }
}
