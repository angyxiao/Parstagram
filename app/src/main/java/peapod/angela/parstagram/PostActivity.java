package peapod.angela.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PostActivity extends AppCompatActivity{

    public final String APP_TAG = "PostActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "image";
    File photoFile;

    // getExternalFilesDir() + "/Pictures" should match the declaration in fileprovider.xml paths
    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

    // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
    Uri bmpUri = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", file);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) { return; }
        File dcim = new File(sdcard, "DCIM");
        if (dcim == null) { return; }
        File camera = new File(dcim, "Camera");
        if (camera.exists()) { return; }
        camera.mkdir();
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                try {
                    takenImage = BitmapFactory.decodeFile(resizeBitMap().getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the taken image into a preview
                ImageView ivPreview = (ImageView) findViewById(R.id.ivImage);
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File resizeBitMap() throws IOException {
        Uri takenPhotoUri = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", getPhotoFileUri(photoFileName));
        // by this point we have the camera photo on disk
        Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
        // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(rawTakenImage, rawTakenImage.getWidth()/2, rawTakenImage.getHeight()/2, false);
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        Uri resizedUri = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", getPhotoFileUri(photoFileName + "_resized"));
        File resizedFile = new File(resizedUri.getPath());
        resizedFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(resizedFile);
        // Write the bytes of the bitmap to file
        fos.write(bytes.toByteArray());
        fos.close();
        return resizedFile;
    }

    /* public void setParseFile() {
        ImageView image = findViewById(R.id.ivImage);
        Glide.with(view.context).load(post.getMedia().getUrl()).into(imageView);
     } */
}
