package com.foursquare.android.nativeoauth.task;


import android.os.AsyncTask;
import android.text.TextUtils;

import com.foursquare.android.nativeoauth.TokenExchangeActivity;
import com.foursquare.android.nativeoauth.exception.FoursquareInternalErrorException;
import com.foursquare.android.nativeoauth.exception.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenExchangeTask extends AsyncTask<String, Void, AccessTokenResponse> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String HTTP_BASE = "https://foursquare.com/oauth2/access_token?";

    private static final String ACCESS_TOKEN_URL = HTTP_BASE
            + "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s";

    private transient TokenExchangeActivity mActivity;

    public TokenExchangeTask(TokenExchangeActivity activity) {
        mActivity = activity;
    }

    public void setActivity(TokenExchangeActivity activity) {
        mActivity = activity;
    }

    @Override
    protected AccessTokenResponse doInBackground(String... params) {
        String accessTokenUrl = String.format(ACCESS_TOKEN_URL, params[0], params[1], params[2]);
        AccessTokenResponse result;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(accessTokenUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            result = parseAccessToken(json);

        } catch (MalformedURLException e) {
            result = createErrorResponse(e);
        } catch (IOException e) {
            result = createErrorResponse(e);
        } catch (JSONException e) {
            result = createErrorResponse(e);
        } catch (Exception e) {
            result = createErrorResponse(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AccessTokenResponse result) {
        mActivity.onTokenComplete(result);
    }

    private String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1;) {
                out.write(buffer, 0, count);
            }
            return new String(out.toByteArray(), "UTF-8");
        } finally {
            closeQuietly(out);
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // no-op
        }
    }

    private AccessTokenResponse parseAccessToken(String json) throws JSONException {
        AccessTokenResponse response = new AccessTokenResponse();
        JSONObject obj = new JSONObject(json);
        String errorCode = obj.optString("error");

        if (TextUtils.isEmpty(errorCode)) {
            response.setAccessToken(obj.optString("access_token"));
        } else {
            response.setException(new FoursquareOAuthException(errorCode));
        }

        return response;
    }

    private AccessTokenResponse createErrorResponse(Exception e) {
        AccessTokenResponse response = new AccessTokenResponse();
        response.setException(new FoursquareInternalErrorException(e));
        return response;
    }
}
