package budgetworld.ru.bw;

import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

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


    public UseRestClient(Activity _activity) {
        activity = _activity;
    }

    public void getRestClient(Integer page) {

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
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    JSONArray jsonArr = new JSONArray(responseData);
                    //final Posts posts = new Posts(jsonArr);
                    this.mapPosts(jsonArr);
                    //мы должны обновить UI===============
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO: update your UI
                            updatePosts(activity);
                        }
                    });
                    //====================================

                } catch (JSONException e) {
                    //написать клиенту про траблы
                    System.out.println("Не вышло");
                    System.out.println(e);
                }
            }

            public void mapPosts(JSONArray resultsJS) throws JSONException {
                //мапим данные из Json
                for (int a = 0; a < resultsJS.length(); a++) {
                    JSONObject postJS = resultsJS.getJSONObject(a);
                    addPost(postJS.getInt("id"), postJS.getJSONObject("title").getString("rendered"), postJS.getJSONObject("excerpt").getString("rendered"));
                }
            }

            private void addPost(Integer id, String title, String body) {
                Post newPost = new Post();
                newPost.postID = id;
                newPost.postTitle = title;
                newPost.postBody = body;
                posts.add(newPost);
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
