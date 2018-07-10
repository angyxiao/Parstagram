package peapod.angela.parstagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("angela.peapod")
                .clientKey("fbu@mpk4summer2018")
                .server("http://angyxiao-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }

}
