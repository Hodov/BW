package budgetworld.ru.bw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by skorokhodov_a on 19.01.2016.
 */
public class AviasalesActivity extends AppCompatActivity{

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;
    ProgressBar myProgressBar;
    String searchURL = "http://search.budgetworld.ru/m/flights/ru/?marker=31347.tpcalwt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aviasales);

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenName();
        // [END shared_tracker]

        //TOOLBAR ==================================================================
        Toolbar post_toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(post_toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //TOOLBAR ==================================================================

        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                myProgressBar.setProgress(newProgress);
                //change your progress bar
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        webView.loadUrl(searchURL);
    }

    private void sendScreenName() {
        String name = "Search_BW";
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
