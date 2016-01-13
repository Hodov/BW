package budgetworld.ru.bw;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class UseRestClient {

    private String url1 = "http://demo.wp-api.org/wp-json/wp/v2/posts";
    private String url2 = "https://api.github.com/users/codepath";
   // private String urlBasic = "http://bardarbunga.info/wp-json/wp/v2/posts";
    private String url4 = "http://budgetworld.ru/wp-json/wp/v2/posts";
    private String urlBasic = "http://budgetworld.ru/wp-json/wp/v2/posts";
    Activity activity;
    ArrayList<Post> posts = new ArrayList<Post>();
    PostsAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;


    public UseRestClient(Activity _activity) {
        activity = _activity;
    }

    public void getRestClient(Integer page, final String action) {

        //=========================================
        //Готовим УРЛ
        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlBasic).newBuilder();
        urlBuilder.addQueryParameter("page", page.toString());
        String url = urlBuilder.build().toString();
        //=========================================
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("ОШИБКА");
                System.out.println(e);
                mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.activity_main_swipe_refresh_layout);
                if (mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity, R.string.connection_troubles, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    JSONArray jsonArr = new JSONArray(responseData);
                    //final Posts posts = new Posts(jsonArr);
                    if ((action == "load") || (posts.isEmpty())) {
                        this.mapPosts(jsonArr);
                    }
                    else if (action == "refresh") {
                        this.mapPostsRefresh(jsonArr);
                        System.out.println("Мапим новые данные");
                    }
                    //мы должны обновить UI===============
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO: update your UI
                            if (action == "refresh") {
                                mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.activity_main_swipe_refresh_layout);
                                mSwipeRefreshLayout.setRefreshing(false);
                                System.out.println("Отключаем рефреш адаптер");
                            }
                            updatePosts(activity);
                        }
                    });
                    //====================================

                } catch (JSONException e) {
                    //написать клиенту про траблы
                    System.out.println("Не вышло");
                    System.out.println(e);
                    Toast toast = Toast.makeText(activity, R.string.json_troubles, Toast.LENGTH_LONG);
                    toast.show();

                }
            }

            public void mapPosts(JSONArray resultsJS) throws JSONException {
                //мапим данные из Json
                for (int a = 0; a < resultsJS.length(); a++) {
                    JSONObject postJS = resultsJS.getJSONObject(a);
                    addPost(postJS.getInt("id"), postJS.getJSONObject("title").getString("rendered"), postJS.getJSONObject("excerpt").getString("rendered"), "end");
                }
            }

            public void mapPostsRefresh(JSONArray resultsJS) throws JSONException {
                //мапим данные из Json
                Integer a = 0;
                JSONObject postJS = resultsJS.getJSONObject(a);
                while ((posts.get(a).postID != postJS.getInt("id")) && (a<=resultsJS.length())) {
                    postJS = resultsJS.getJSONObject(a);
                    addPost(postJS.getInt("id"), postJS.getJSONObject("title").getString("rendered"), postJS.getJSONObject("excerpt").getString("rendered"), a.toString());
                    a++;
                }
            }


            private void addPost(Integer id, String title, String body, String position) {
                Post newPost = new Post();
                newPost.postID = id;
                newPost.postTitle = title;
                newPost.postBody = body;
                if (position == "end") {
                    posts.add(newPost);
                } else {
                    Integer i = Integer.valueOf(position);
                    posts.add(i,newPost);
                }
            }

            public void updatePosts(Activity _activity) {

                adapter.notifyDataSetChanged();
            }
        });
    }

    public void drawPosts(Activity _activity) {
        // Create the adapter to convert the array to views
       // PostsAdapter adapter = new PostsAdapter(_activity, posts);
        adapter = new PostsAdapter(_activity, posts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) _activity.findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
    }


}
