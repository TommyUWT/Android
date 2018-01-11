package tv.vanriper.fconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 *
 * The PhotoActivity displays the selected photo with the photo's comments
 * listed below.
 */
public class PhotoActivity extends AppCompatActivity
{
    private ImageView photoView;
    private List<CommentsAdapter.CommentItem> commentsList;
    private RecyclerView recyclerView;
    private String photoId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoView = (ImageView) findViewById(R.id.photo_view);
        recyclerView = (RecyclerView) findViewById(R.id.comments);

        if(AccessToken.getCurrentAccessToken() == null)
        {
            finish();
            return;
        }

        Intent intent = getIntent();
        photoId = intent.getStringExtra("id");

        fetchPhoto();
        fetchComments();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("albumName"));
        intent.putExtra("id", getIntent().getStringExtra("albumId"));
        startActivity(intent);
        finish();
        return true;
    }

    /**
     * A Facebook Graph call to fetch the photo's comments.
     */
    private void fetchComments()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                photoId + "/comments",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {
                        if (response.getError() != null)
                        {
                            Toast.makeText(PhotoActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        commentsList = new ArrayList<>();
                        JSONObject jsonResponse = response.getJSONObject();
                        try
                        {
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++)
                            {
                                JSONObject commentItem = jsonData.getJSONObject(i);
                                String comment = commentItem.getString("message");
                                String date = commentItem.getString("created_time");
                                JSONObject from = commentItem.getJSONObject("from");
                                String name = from.getString("name");

                                CommentsAdapter.CommentItem item = new CommentsAdapter.CommentItem(comment, date, name);
                                commentsList.add(item);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        CommentsAdapter adapter = new CommentsAdapter(commentsList);
                        recyclerView.setAdapter(adapter);
                    }
                }
        ).executeAsync();
    }

    /**
     * A Facebook Graph call to get the photo, the photo's height, photo's width, and
     * the date the photo was added to Facebook.
     */
    private void fetchPhoto()
    {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture,width,height,created_time");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                photoId,
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                        if (response.getError() != null)
                        {
                            Toast.makeText(PhotoActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONObject jsonPhoto = response.getJSONObject();
                        try
                        {
                            String url = jsonPhoto.getString("picture");
                            int height = jsonPhoto.getInt("height") / 2;
                            int width = jsonPhoto.getInt("width") / 2;
                            String date = jsonPhoto.getString("created_time");
                            setTitle(Utility.formatDate(date));
                            Utility.displayPicture(photoView, url);
                            photoView.setMinimumHeight(height);
                            photoView.setMinimumWidth(width);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}
