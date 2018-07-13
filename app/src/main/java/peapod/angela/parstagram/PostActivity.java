package peapod.angela.parstagram;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity{

    public final String APP_TAG = "PostActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public String photoFileName = "image.jpg";
    String photoPath;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        onLaunchCamera();
    }

    public void fixMediaDir() {
        // grant URI permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) { return; }
        File dcim = new File(sdcard, "DCIM");
        if (dcim == null) { return; }
        File camera = new File(dcim, "Camera");
        if (camera.exists()) { return; }
        camera.mkdir();
    }

    public void onLaunchCamera() {
        fixMediaDir();

        // create Intent to take a picture and return control to the calling application
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "peapod.angela.parstagram", photoFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            // Create the File where the photo should go
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
//                try {
//                    takenImage = BitmapFactory.decodeFile(resizeBitMap().getAbsolutePath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                setContentView(R.layout.activity_post);

                // Load the taken image into a preview
                ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
                ivImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File resizeBitMap() throws IOException {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Uri takenPhotoUri = FileProvider.getUriForFile(PostActivity.this, "peapod.angela.parstagram", getPhotoFileUri(photoFileName));
        // by this point we have the camera photo on disk
        Bitmap rawTakenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(rawTakenImage, width, height, false);
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        Uri resizedUri = FileProvider.getUriForFile(PostActivity.this, "peapod.angela.parstagram", getPhotoFileUri(photoFileName));
        File resizedFile = new File(resizedUri.getPath());
        resizedFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(resizedFile);
        // Write the bytes of the bitmap to file
        fos.write(bytes.toByteArray());
        fos.close();
        return resizedFile;
    }

    private File createImageFile() {
        File image = null;
        try {
            Uri takenPhotoUri = FileProvider.getUriForFile(PostActivity.this, "peapod.angela.parstagram", getPhotoFileUri(photoFileName));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isMediaDocument(takenPhotoUri)) {
                final String docId = DocumentsContract.getDocumentId(takenPhotoUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                String filePath = getDataColumn(this, contentUri, selection, selectionArgs);
                photoPath = filePath;
            } else {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                // Save a file: path for use with ACTION_VIEW intents
                photoPath = image.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public void createPost(View view) {
        // create image file to save
//        File file = createImageFile();
        // return the image path to home activity
        Intent resultIntent = new Intent();
        String absolutePath = photoFile.getAbsolutePath();
        resultIntent.putExtra("image", absolutePath);
        resultIntent.putExtra("caption", ((EditText) findViewById(R.id.caption)).getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
}
