package peapod.angela.parstagram;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
    }

    public void signup(View view) {
        // Create the ParseUser
        ParseUser user = new ParseUser();

        EditText email = findViewById(R.id.emailEntry);
        EditText username = findViewById(R.id.usernameEntry);
        EditText password = findViewById(R.id.passwordEntry);
        EditText profileImage = findViewById(R.id.profileEntry);

        // Set core properties
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.setEmail(email.getText().toString());

//        try {
//            URL url = new URL(profileImage.getText().toString());
//            File f = FileUtils.toFile(url);
//            ParseFile parseFile = new ParseFile(f);
//            ParseUser.getCurrentUser().add("profile", parseFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Toast.makeText(SignupActivity.this, "Sign up successful! Please log in", Toast.LENGTH_LONG).show();
                    loginToApp();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this, "Sign up failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
