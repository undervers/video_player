package com.shitsuma.videomobileclient.http;


import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shitsuma.R;

import org.apache.http.Header;

public abstract class HttpResponseHandler extends AsyncHttpResponseHandler {
    private Context context;
    private ProgressDialog waitingDialog;

    public HttpResponseHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onStart(){
        waitingDialog = ProgressDialog.show(context, "", context.getString(R.string.waiting_please));
    }

    @Override
    public void onFinish(){
        waitingDialog.dismiss();
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        Toast.makeText(context, context.getString(R.string.download_failed), Toast.LENGTH_LONG).show();
    }
}