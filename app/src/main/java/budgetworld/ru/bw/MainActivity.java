package budgetworld.ru.bw;

import android.app.Activity;
import android.os.Bundle;
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
    }

}
