package budgetworld.ru.bw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class PostsAdapter extends ArrayAdapter<Post> {

    private ArrayList<Post> posts;
    //Context context;

    public PostsAdapter(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size() + 1;
    }

    @Override
    public Post getItem(int position) {
        Post post = new Post();
        if (position < this.posts.size()) {
            post = posts.get(position);
        } else {
            post.postTitle = "Загрузка...";
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
        //TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        ImageView tvImage = (ImageView) convertView.findViewById(R.id.tvImage);
        // Populate the data into the template view using the data object
        tvTitle.setText(Html.fromHtml(post.postTitle));
        //tvBody.setText(Html.fromHtml(post.postBody));



/*
            Picasso
                    .with(getContext())
                    .cancelRequest(tvImage);
*/
            Picasso
                    .with(getContext())
                    .load(post.postImageURL)
                    .fit()
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(tvImage);

        // Return the completed view to render on screen
        return convertView;
    }
}

