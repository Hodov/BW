package budgetworld.ru.bw;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import android.support.v4.content.LocalBroadcastManager;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;
    private String shareText;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private UseRestClient bwRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Поехали
        // Server API Key help AIzaSyAlICMX1bjMbh42r58EMTwy8tckX38TgiA

        shareText = getResources().getString(R.string.share_Text);
        startGoogleAnalytics();
        startNotifications();
        startToolbar();

        // если юзер пришел из нотификаций, пишем аналитику
        if (getIntent().hasExtra("from notify")) {
            sendGoogleAction("Notification", getIntent().getExtras().getString("from notify"));
        }

        // получаем первую порцию данных и заполняем адаптер
        bwRest = new UseRestClient(this);
        bwRest.drawPosts(this);

        //находим лист вью
        ListView lvItems = (ListView) findViewById(R.id.lvItems);
        //находим рефреш адаптер
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        //СЛУШАЕМ КОНЕЦ СПИСКА
        lvItems.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                try {
                    bwRest.getRestClient(page - 1, "load");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendGoogleAction("Scroll", "Page " + page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        //==================

        //СЛУШАЕМ КЛИК ПО ЛИСТВЬЮ
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("title", bwRest.posts.get(position).postTitle);
                intent.putExtra("link", bwRest.posts.get(position).postLink);
                startActivity(intent);
                sendGoogleAction("Move",String.valueOf(Html.fromHtml(bwRest.posts.get(position).postTitle)));
            }
        });
        //==================

        //СЛУШАЕМ РЕФРЕШ АДАПТЕР
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendGoogleAction("Action", "Refresh");
                Integer page = 1;
                try {
                    bwRest.getRestClient(page, "refresh");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void startGoogleAnalytics() {
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenName();
    }

    private void startToolbar() {
        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);
        getSupportActionBar().setTitle(null);
    }

    private void startNotifications() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    System.out.println(getString(R.string.gcm_send_message));
                } else {
                    System.out.println(getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onSearchAction(MenuItem mi) {
        // handle click here
        Intent intent = new Intent(MainActivity.this, AviasalesActivity.class);
        startActivity(intent);
    }

    public void onShareAction(MenuItem mi) {
        //GOOGLE ANALYTICS
        sendGoogleAction("Action", "Share");
        // handle click here
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void sendGoogleAction(String category, String description) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(description)
                .build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendScreenName();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void sendScreenName() {
        String name = "MainActivity_BW";
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


}
