package budgetworld.ru.bw;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
                    JSONArray jsonArr = new JSONArray(responseData);
                    //final Posts posts = new Posts(jsonArr);
                    if ((action == "load") || (posts.isEmpty())) {
                        this.mapPosts(jsonArr);
                    }
                    else if (action == "refresh") {
                        this.mapPostsRefresh(jsonArr);
                    }
                    //мы должны обновить UI===============
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO: update your UI
                            if (action == "refresh") {
                                mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.activity_main_swipe_refresh_layout);
                                mSwipeRefreshLayout.setRefreshing(false);
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
                    int id = postJS.getInt("id");
                    String title = postJS.getJSONObject("title").getString("rendered");
                    String body = postJS.getJSONObject("excerpt").getString("rendered");
                    String url = postJS.getJSONObject("better_featured_image").getJSONObject("media_details").getJSONObject("sizes").getJSONObject("large").getString("source_url");
                    Bitmap image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    //new DownloadImageTask(image).execute(url);
                    image = getimage(url);
                    addPost(id, title, body, url, image, "end");
                }
            }

            public void mapPostsRefresh(JSONArray resultsJS) throws JSONException {
                //мапим данные из Json
                Integer a = 0;
                JSONObject postJS = resultsJS.getJSONObject(a);
                while ((posts.get(a).postID != postJS.getInt("id")) && (a<resultsJS.length())) {
                    postJS = resultsJS.getJSONObject(a);
                    int id = postJS.getInt("id");
                    String title = postJS.getJSONObject("title").getString("rendered");
                    String body = postJS.getJSONObject("excerpt").getString("rendered");
                    String url = postJS.getJSONObject("better_featured_image").getJSONObject("media_details").getJSONObject("sizes").getJSONObject("large").getString("source_url");
                    
                    Bitmap image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    //new DownloadImageTask(image).execute(url);
                    image = getimage(url);
                    addPost(id, title, body, url, image, a.toString());
                    a++;
                }
            }


            private void addPost(Integer id, String title, String body, String url, Bitmap image, String position) {
                Post newPost = new Post();
                newPost.postID = id;
                newPost.postTitle = title;
                newPost.postBody = body;
                newPost.postImageURL = url;
                newPost.postImage = image;
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

    private Bitmap getimage(String url) {
        String urldisplay = url;
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
            System.out.println("Зашли в даунлоад имаджтаск");
        }

        protected Bitmap doInBackground(String... urls) {
            System.out.println("Выполняем в бекграунде");
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage = result;
        }

    }

}
