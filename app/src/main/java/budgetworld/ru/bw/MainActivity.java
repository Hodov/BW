package budgetworld.ru.bw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //понеслась
        final UseRestClient bwRest = new UseRestClient(this);
        bwRest.drawPosts(this);
        bwRest.getRestClient(1);
       // bwRest.getRestClient(2);

        ListView lvItems = (ListView) findViewById(R.id.lvItems);
        //слушаем конец списка
        lvItems.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                System.out.println("Грузим еще одну страницу");
                bwRest.getRestClient(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

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


    }

}
