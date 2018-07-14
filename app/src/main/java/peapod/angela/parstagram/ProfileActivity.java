package peapod.angela.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {
    private static final String KEY_PROFILE_IMAGE = "profile";


    public ProfileActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();

        setContentView(R.layout.activity_profile);

        ImageView image = findViewById(R.id.profileImage);
        TextView username = findViewById(R.id.profileUser);

        username.setText("@" + ParseUser.getCurrentUser().getUsername());
        ParseUser currUser = ParseUser.getCurrentUser();
        if (currUser.getParseFile(KEY_PROFILE_IMAGE) != null) {
            ParseFile profile = currUser.getParseFile(KEY_PROFILE_IMAGE);
            Glide.with(context).load(profile.getUrl()).into(image);
        }
    }
}
