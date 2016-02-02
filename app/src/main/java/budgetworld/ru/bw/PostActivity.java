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


public class PostActivity extends AppCompatActivity {

    ProgressBar myProgressBar;

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;
    private String afterUrl = "?utm_source=app&utm_medium=android&utm_campaign=main";
    AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        appConfig  = new AppConfig();
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        if (appConfig.releaseBuild) {
            sendScreenName();
        }

        // [END shared_tracker]

        //TOOLBAR ==================================================================
        Toolbar post_toolbar = (Toolbar) findViewById(R.id.post_toolbar);
        setSupportActionBar(post_toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //TOOLBAR ==================================================================

        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                myProgressBar.setProgress(newProgress);
                //change your progress bar
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(Html.fromHtml(extras.getString("title")));
        webView.loadUrl(extras.getString("link")+afterUrl);

    }

    private void sendScreenName() {
        String name = "WebView_BW";
        // [START screen_view_hit]
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END screen_view_hit]
    }
}
