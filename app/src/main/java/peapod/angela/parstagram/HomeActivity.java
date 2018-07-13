package peapod.angela.parstagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import peapod.angela.parstagram.model.Post;

public class HomeActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;

    private final int CREATE_CODE = 1;
    //TODO : context, make toolbar global
    private String imagePath;
    private String caption;

    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                // refresh adapter with new posts
                loadTopPosts();
                postAdapter.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadTopPosts();

        rvPosts = findViewById(R.id.postList);

        // initialize the arraylist (data source)
        posts = new ArrayList<>();

        // construct the adapter from this data source
        postAdapter = new PostAdapter(posts);

        // RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // set adapter
        rvPosts.setAdapter(postAdapter);
        }


    private void launchPost() {
        final Intent intent = new Intent(HomeActivity.this, PostActivity.class);
        startActivityForResult(intent, CREATE_CODE);
    }

    private void createPost(String captionText, ParseFile imageFile, ParseUser user, String path, Date date) {
        final Post newPost = new Post();
        newPost.setDescription(captionText);
        newPost.setImage(imageFile);
        newPost.setUser(user);
        newPost.setPath(path);
        newPost.setTime(date);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    posts.add(newPost);
                    Log.d("HomeActivity", "Create post success!");
                    Toast.makeText(HomeActivity.this, "Created post!", Toast.LENGTH_LONG).show();

                    // refresh adapter with new posts
                    loadTopPosts();
                    postAdapter.notifyDataSetChanged();

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTopPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();
        postsQuery.orderByDescending("time");

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.clear();
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("HomeActivity", "Post[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                        posts.addAll(objects);
                        postAdapter.notifyDataSetChanged();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // on some click or some loading we need to wait for...
        ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        switch (requestCode) {
            case CREATE_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    String image = data.getStringExtra("image");
                    String text = data.getStringExtra("caption");
                    imagePath = image;
                    caption = text;

                    final String captionText = caption;
                    final ParseUser user = ParseUser.getCurrentUser();
                    final File file = new File(imagePath);
                    final ParseFile parseFile = new ParseFile(file);
                    final String path = imagePath;

                    final Date date = new Date(file.lastModified());

                    createPost(captionText, parseFile, user, path, date);
                }
                break;
        }

        loadTopPosts();

        // run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    public void logout() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.ic_profile:
                // launchProfile();
                break;
            case R.id.ic_create:
                launchPost();
                break;
            case R.id.ic_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
