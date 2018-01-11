package tv.vanriper.fconnect;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 *
 * The AlbumActivity displays a grid view of the photos
 * comprising the selected Facebook photo album.
 */
public class AlbumActivity extends AppCompatActivity
{
    private static final int REQUEST_IMAGE_GET = 1;

    private PhotosAdapter adapter;
    private RecyclerView recyclerView;
    private String albumId;
    private String albumName;
    private TextView noPhotosView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        toolbar = (Toolbar) findViewById(R.id.album_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView) findViewById(R.id.album_toolbar_title);

        noPhotosView = (TextView) findViewById(R.id.no_photos_view);
        recyclerView = (RecyclerView) findViewById(R.id.photos_list);
        int columns = Utility.calculateColumns(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        if(AccessToken.getCurrentAccessToken() == null)
        {
            finish();
            return;
        }

        Intent intent = getIntent();
        albumName = intent.getStringExtra("name");
        toolbarTitle.setText(albumName);
        albumId = intent.getStringExtra("id");

        fetchPhotos();
    }

    /**
     * A Facebook Graph call that fetches the user's photos from the selected
     * Facebook photo album.
     */
    private void fetchPhotos()
    {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "created_time,id,picture");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                        if (response.getError() != null)
                        {
                            Toast.makeText(AlbumActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<PhotosAdapter.PhotoItem> photosList = new ArrayList<>();
                        JSONObject jsonResponse = response.getJSONObject();
                        try
                        {
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++)
                            {
                                JSONObject jsonPhoto = jsonData.getJSONObject(i);
                                String date = jsonPhoto.getString("created_time");
                                String id = jsonPhoto.getString("id");
                                String url = jsonPhoto.getString("picture");

                                PhotosAdapter.PhotoItem photoItem = new PhotosAdapter.PhotoItem(date, id, url);
                                photosList.add(photoItem);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        adapter = new PhotosAdapter(photosList);
                        recyclerView.setAdapter(adapter);
                        if(photosList.size() == 0)
                            noPhotosView.setVisibility(View.VISIBLE);
                    }
                }
        ).executeAsync();
    }

    /**
     * On a photo being clicked, PhotoActivity is launched.
     * @param view
     */
    public void launchPhotoActivity(View view)
    {
        ImageView image = (ImageView) view;
        String id = (String) image.getTag();
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("albumId", albumId);
        intent.putExtra("albumName", albumName);
        intent.putExtra("id", id);
        startActivity(intent);

    }

    /**
     * On clicking the add icon, uploads a new photo to the Facebook photo
     * album.
     */
    private void launchUploadPhoto()
    {
        Set permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (!permissions.contains("publish_actions"))
            getPublishPermision();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    /**
     * On clicking the add icon, uploads a new photo to the Facebook photo
     * album.
     * @param view
     */
    public void launchUploadPhoto(View view)
    {
        launchUploadPhoto();
    }

    /**
     * A helper method that determines if the user has granted the app
     * permission to publish a picture to the user's Facebook photo album.
     */
    private void getPublishPermision()
    {
        // prompt user to grant permissions
        LoginManager loginManager = LoginManager.getInstance();
        CallbackManager callbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                launchUploadPhoto();
            }

            @Override
            public void onCancel()
            {
                // inform user that permission is required
                String permissionMsg = getResources().getString(R.string.permission_message);
                Toast.makeText(AlbumActivity.this, permissionMsg, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(FacebookException error)
            {
                Toast.makeText(AlbumActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        loginManager.logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    /**
     * A helper method to convert a photo file into a byte array
     * prior to uploading to the Facebook photo album.
     * @param inputStream
     * @return
     */
    public byte[] getBytes(InputStream inputStream)
    {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        try
        {
            while ((len = inputStream.read(buffer)) != -1)
            {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK)
        {
            Uri fullPhotoUri = data.getData();
            byte[] inputData = null;
            try
            {
                InputStream inputStream = getContentResolver().openInputStream(fullPhotoUri);
                inputData = getBytes(inputStream);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            Bundle parameters = new Bundle();
            parameters.putByteArray("source", inputData);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    albumId + "/photos",
                    parameters,
                    HttpMethod.POST,
                    new GraphRequest.Callback()
                    {
                        public void onCompleted(GraphResponse response)
                        {
                            if (response.getError() != null)
                            {
                                Toast.makeText(AlbumActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }

                            fetchPhotos();
                        }
                    }
            ).executeAsync();
        }
    }
}
