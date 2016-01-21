package budgetworld.ru.bw;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.io.InputStream;
import java.util.ArrayList;

public class UseRestClient {

   // private String urlBasic = "http://bardarbunga.info/wp-json/wp/v2/posts";
    private String urlBasic = "http://budgetworld.ru/wp-json/wp/v2/posts";
    private String noImageURL = "http://bloggfiler.no/anniegetyourgun.blogg.no/images/1104500-9-1382626375921-n500.jpg";
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
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast toast = Toast.makeText(activity, R.string.connection_troubles, Toast.LENGTH_LONG);
                        //toast.show();
                        LinearLayout mRootLayout = (LinearLayout) activity.findViewById(R.id.rootLayout);
                        Snackbar
                                .make(mRootLayout, R.string.connection_troubles, Snackbar.LENGTH_LONG)
                                .show();

                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONArray jsonArr = new JSONArray(responseData);
                    //final Posts posts = new Posts(jsonArr);
                    if ((action == "load") || (posts.isEmpty())) {
                        this.mapPosts(jsonArr);
                    } else if (action == "refresh") {
                        this.mapPostsRefresh(jsonArr);
                    }
                    //мы должны обновить UI
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (action == "refresh") {
                                mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.activity_main_swipe_refresh_layout);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                            updatePosts(activity);
                        }
                    });
                } catch (JSONException e) {
                    //написать клиенту про траблы
                    System.out.println("Не вышло");
                    System.out.println(e);
                    //Toast toast = Toast.makeText(activity, R.string.json_troubles, Toast.LENGTH_LONG);
                    //toast.show();
                    LinearLayout mRootLayout = (LinearLayout) activity.findViewById(R.id.rootLayout);
                    Snackbar
                            .make(mRootLayout, R.string.json_troubles, Snackbar.LENGTH_LONG)
                            .show();

                }
            }

            public void mapPosts(JSONArray resultsJS) throws JSONException {
                //мапим данные из Json
                for (int a = 0; a < resultsJS.length(); a++) {
                    JSONObject postJS = resultsJS.getJSONObject(a);
                    mappingData(postJS, "end");
                }
            }

            public void mapPostsRefresh(JSONArray resultsJS) throws JSONException {
                //мапим данные из Json
                Integer a = 0;
                JSONObject postJS = resultsJS.getJSONObject(a);
                while ((posts.get(a).postID != postJS.getInt("id")) && (a < resultsJS.length())) {
                    postJS = resultsJS.getJSONObject(a);
                    mappingData(postJS, a.toString());
                    a++;
                }
            }

            private void mappingData(JSONObject postJSData, String place) throws JSONException {
                JSONObject postJS = postJSData;
                int id = postJS.getInt("id");
                String title = postJS.getJSONObject("title").getString("rendered");
                String body = postJS.getJSONObject("excerpt").getString("rendered");
                String link = postJS.getString("link");
                String url = "";
                try {
                    //url = postJS.getJSONObject("better_featured_image").getJSONObject("media_details").getJSONObject("sizes").getJSONObject("portfolio").getString("source_url");
                    url = postJS.getJSONObject("better_featured_image").getString("source_url");
                } catch (Exception e) {
                    url = noImageURL;
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                addPost(id, title, body, url, link, place);
            }


            private void addPost(Integer id, String title, String body, String url, String link, String position) {
                Post newPost = new Post();
                newPost.postID = id;
                newPost.postTitle = title;
                newPost.postBody = body;
                newPost.postImageURL = url;
                newPost.postLink = link;
                if (position == "end") {
                    posts.add(newPost);
                } else {
                    Integer i = Integer.valueOf(position);
                    posts.add(i, newPost);
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
        //getRestClient(1, "load");
        adapter = new PostsAdapter(_activity, posts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) _activity.findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
    }


}
