package peapod.angela.parstagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject{
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_PATH = "path";
    private static final String KEY_CREATED = "time";
    private static final String KEY_PROFILE = "profile";


    public Post() {
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getPath() {return KEY_PATH;}

    public void setPath(String path) {put(KEY_PATH, path);}

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Date getTime() {
        return getDate(KEY_CREATED);
    }

    public void setTime(Date date) {
        put(KEY_CREATED, date);
    }

    public ParseFile getProfile() { return getUser().getParseFile(KEY_PROFILE);}

    //public void setProfile(String path) { getUser().put(KEY_PROFILE, path);}

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
