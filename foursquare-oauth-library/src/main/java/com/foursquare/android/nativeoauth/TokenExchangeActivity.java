/*
 * Copyright (C) 2013 Foursquare Labs, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foursquare.android.nativeoauth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.task.TokenExchangeTask;

/**
 * A utility {@link Activity} that converts a short-lived auth code into an
 * access token. Do not start this activity directly. Obtain an intent from
 * {@link FoursquareOAuth#getTokenExchangeIntent(android.content.Context, String, String, String)}
 * and start the intent for a result. <br>
 * <br>
 * Add this activity to your AndroidManifest.xml
 * 
 * <pre>
 * {@code
 * <activity android:name="com.foursquare.android.nativeoauth.TokenExchangeActivity"
 *           android:theme="@android:style/Theme.Dialog" />
 * }
 * </pre>
 * 
 * Since we strongly encourage developers to pass the code up to their server
 * and have the server do the code exchange, this {@link Activity} is an
 * optional part of the native Foursquare auth process. <br>
 * <br>
 * 
 * @see <a href="https://developer.foursquare.com/overview/auth#access"
 *      >https://developer.foursquare.com/overview/auth#access</a>
 *      
 * @date 2013-06-01
 */
public final class TokenExchangeActivity extends Activity {
    
    private static final String TAG = TokenExchangeActivity.class.getName();

    public static final String INTENT_EXTRA_CLIENT_ID = TAG + ".INTENT_EXTRA_CLIENT_ID";
    
    public static final String INTENT_EXTRA_CLIENT_SECRET = TAG + ".INTENT_EXTRA_CLIENT_SECRET";

    public static final String INTENT_EXTRA_AUTH_CODE = TAG + ".INTENT_EXTRA_AUTH_CODE";
    
    public static final String INTENT_RESULT_RESPONSE = TAG + ".INTENT_RESULT_RESPONSE";
    
    private static final String INTENT_EXTRA_TOKEN_EXCHANGE_TASK = TAG + ".INTENT_EXTRA_TOKEN_EXCHANGE_TASK";
    
    private TokenExchangeTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(getThemeRes());
        setContentView(R.layout.loading);
        
        String clientId = getIntent().getStringExtra(INTENT_EXTRA_CLIENT_ID);
        String clientSecret = getIntent().getStringExtra(INTENT_EXTRA_CLIENT_SECRET);
        String authCode = getIntent().getStringExtra(INTENT_EXTRA_AUTH_CODE);
        
        if (savedInstanceState == null) {
            mTask = new TokenExchangeTask(this);
            mTask.execute(clientId, clientSecret, authCode);
            
        } else {
            mTask = (TokenExchangeTask) savedInstanceState.getSerializable(INTENT_EXTRA_TOKEN_EXCHANGE_TASK);
            mTask.setActivity(this);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INTENT_EXTRA_TOKEN_EXCHANGE_TASK, mTask);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Consume all touch events to avoid activity being finished when touched
        return true;
    }
    
    @Override
    public void onBackPressed() {
        // no-op
    }
    
    public void onTokenComplete(AccessTokenResponse response) {
        Intent data = new Intent();
        data.putExtra(INTENT_RESULT_RESPONSE, response);
        
        setResult(RESULT_OK, data);
        finish();
    }
    
    @SuppressLint("InlinedApi")
    private int getThemeRes() {
        return android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth;
    }
}
