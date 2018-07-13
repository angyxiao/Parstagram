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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
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

    private void createPost(String captionText, ParseFile imageFile, ParseUser user, String path) {
        final Post newPost = new Post();
        newPost.setDescription(captionText);
        newPost.setImage(imageFile);
        newPost.setUser(user);
        newPost.setPath(path);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    posts.add(newPost);
                    Log.d("HomeActivity", "Create post success!");
                    Toast.makeText(HomeActivity.this, "Created post!", Toast.LENGTH_LONG);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTopPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
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

                    createPost(captionText, parseFile, user, path);
                }
                break;
        }
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
