package budgetworld.ru.bw;


import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ShareActionProvider;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;
    private String shareText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Поехали
        shareText = getResources().getString(R.string.share_Text);
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        sendScreenName();
        // [END shared_tracker]

        //TOOLBAR ==================================================================
        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);
        getSupportActionBar().setTitle(null);
        //TOOLBAR ==================================================================

        // получаем первую порцию данных и заполняем адаптер
        final UseRestClient bwRest = new UseRestClient(this);
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
                bwRest.getRestClient(page - 1, "load");
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
                bwRest.getRestClient(page, "refresh");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendScreenName();
    }

    private void sendScreenName() {
        String name = "MainActivity_BW";
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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

}
