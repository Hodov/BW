package budgetworld.ru.bw;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

        import android.app.IntentService;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Base64;
        import android.util.Log;

        import com.google.android.gms.gcm.GcmPubSub;
        import com.google.android.gms.gcm.GoogleCloudMessaging;
        import com.google.android.gms.iid.InstanceID;


        import java.io.IOException;
        import java.util.Date;

        import oauth.signpost.exception.OAuthCommunicationException;
        import oauth.signpost.exception.OAuthExpectationFailedException;
        import oauth.signpost.exception.OAuthMessageSignerException;
        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.FormBody;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;
        import okio.Buffer;
        import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
        import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);
            sendTokenNotify(token);//это отправка в вордпресс Скороходов

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    private void sendTokenNotify(String token) {
       // OkHttpClient client = new OkHttpClient();
        String token1 = "";
        String secret1 = "";

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer("ck_3373d66cd71fbac5241b0f9598bf65cc", "cs_47d376a78f18bfa906f76129b41c9b20");
        //consumer.setTokenWithSecret(token1, secret1);

        OkHttpClient client = new OkHttpClient.Builder()
                //.addInterceptor(new SigningInterceptor(consumer))
                .build();

        new Date().getTime();

        RequestBody formBody = new FormBody.Builder()
                .add("token", "123")
                .add("os", "Android")
                .add("oauth_consumer_key", "ck_3373d66cd71fbac5241b0f9598bf65cc")
                .add("oauth_token", "")
                .add("oauth_signature_method", "HMAC-SHA1")
                .add("oauth_timestamp", Long.toString(new Date().getTime()))
                .add("oauth_nonce", "nThTwC")
                .add("oauth_version", "1.0")
                .add("oauth_signature", "gFl3YRqwzFW1W58G689V7bZuAFw=")
                .build();

        Request request = new Request.Builder()
                .url("http://bardarbunga.info/pnfw/register/")
                .post(formBody)
                .build();

        System.out.println(bodyToString(request));


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Не удалось отправить запрос");
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });

    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


}

