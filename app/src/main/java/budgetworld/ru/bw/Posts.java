package budgetworld.ru.bw;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Posts {

    ArrayList<Post> posts = new ArrayList<Post>();

    public Posts(JSONArray resultsJS) throws JSONException {
        //мапим данные из Json
        for (int a = 0; a < resultsJS.length(); a++) {
            JSONObject postJS = resultsJS.getJSONObject(a);
            addPost(postJS.getInt("id"), postJS.getJSONObject("title").getString("rendered"),postJS.getJSONObject("excerpt").getString("rendered"));
        }
    }

    private void addPost (Integer id, String title, String body) {
        Post newPost = new Post();
        newPost.postID = id;
        newPost.postTitle = title;
        newPost.postBody = body;
        posts.add(newPost);
    }

    public void drawPosts(Activity _activity) {
        // Create the adapter to convert the array to views
        PostsAdapter adapter = new PostsAdapter(_activity, this.posts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) _activity.findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
    }
}
