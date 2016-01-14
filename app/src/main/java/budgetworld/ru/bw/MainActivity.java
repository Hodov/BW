package budgetworld.ru.bw;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Поехали

        //ACTION BAR ==================================================================
        ActionBar actionBar = getSupportActionBar(); // or getActionBar(); getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setLogo(R.mipmap.ic_launcher);
        //actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_actionbar);
        //ACTION BAR ==================================================================

        // получаем первую порцию данных и заполняем адаптер
        final UseRestClient bwRest = new UseRestClient(this);
        bwRest.drawPosts(this);
        bwRest.getRestClient(1, "load");

        //находим лист вью
        ListView lvItems = (ListView) findViewById(R.id.lvItems);
        //находим рефреш адаптер
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        //слушаем конец списка
        lvItems.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                bwRest.getRestClient(page, "load");
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        //==================

        //слушаем клик
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("title", bwRest.posts.get(position).postTitle);
                startActivity(intent);

            }
        });
        //==================

        //слушаем рефреш адаптер
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Integer page = 1;
                bwRest.getRestClient(page, "refresh");
            }
        });
    }

}
