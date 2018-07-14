package peapod.angela.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import org.parceler.Parcels;

import peapod.angela.parstagram.model.Post;

public class DetailsActivity extends AppCompatActivity {

    // the movie to display
    Post post;
    Context context;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setContentView(R.layout.activity_details);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        ImageView image = findViewById(R.id.detailsImage);
        TextView username = findViewById(R.id.detailsUser);
        TextView time = findViewById(R.id.detailsTime);
        TextView caption = findViewById(R.id.detailsCaption);
        ListView comments = findViewById(R.id.detailsComments);
        ImageView profile = findViewById(R.id.detailsProfile);

        // set the layout views
        Glide.with(context).load(post.getImage().getUrl()).into(image);
        if (ParseUser.getCurrentUser().getParseFile("profile") != null) {
            Glide.with(context).load(ParseUser.getCurrentUser().getParseFile("profile").getUrl()).into(profile);
        }

        username.setText("@" + post.getUser().getUsername());
        time.setText(post.getTime().toString());
        caption.setText(post.getDescription());
        //comments.setAdapter(commentsAdapter());

    }
}