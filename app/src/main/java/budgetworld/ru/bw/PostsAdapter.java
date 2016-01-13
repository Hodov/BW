package budgetworld.ru.bw;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 26.12.2015.
 */
public class PostsAdapter extends ArrayAdapter<Post> {

    private ArrayList<Post> posts;
    //Context context;


    public PostsAdapter(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size()+1;
    }

    @Override
    public Post getItem(int position) {
        Post post = new Post();
        //System.out.println(position);
        //System.out.println(this.posts.size());
        if (position < this.posts.size()) {
            post = posts.get(position);
        }
        else {
            //System.out.println("Попытка отобразить ячейку с загрузкой");
            post.postTitle = "Загрузка...";
            post.postBody = " ";
        }
        return post;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            Post post = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
            }
            // Lookup view for data population
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            // Populate the data into the template view using the data object
            tvTitle.setText(Html.fromHtml(post.postTitle));
            tvBody.setText(Html.fromHtml(post.postBody));
            // Return the completed view to render on screen
        return convertView;
    }
}

