package tv.vanriper.fconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 *
 * The AlbumsActivity lists the user's Facebook photo albums as clickable
 * buttons.
 */
public class AlbumsActivity extends AppCompatActivity
{
    private ImageView userPicture;
    private List<AlbumsAdapter.AlbumItem> albumList;
    private RecyclerView recyclerView;
    private TextView albumsTitleView;
    private TextView noAlbumsView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        albumsTitleView = (TextView) findViewById(R.id.albums_title);
        noAlbumsView = (TextView) findViewById(R.id.no_albums_view);
        recyclerView = (RecyclerView) findViewById(R.id.albums_list);
        userPicture = (ImageView) findViewById(R.id.user_picture);

        if(AccessToken.getCurrentAccessToken() == null)
        {
            finish();
            return;
        }

        fetchUser();
        fetchAlbums();
    }

    /**
     * Sets the user's profile picture in a circular view that doubles
     * as a logout button.
     * @param imageView
     * @param url
     */
    private void displayProfilePic(ImageView imageView, String url)
    {
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(imageView.getContext())
                .load(url)
                .transform(transformation)
                .into(imageView);
    }

    /**
     * A Facebook Graph call that fetches the user's
     * Facebook photo albums.
     */
    private void fetchAlbums()
    {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                        if (response.getError() != null)
                        {
                            Toast.makeText(AlbumsActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        albumList = new ArrayList<>();
                        JSONObject jsonResponse = response.getJSONObject();
                        try
                        {
                            JSONArray jsonData = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++)
                            {
                                JSONObject jsonAlbum = jsonData.getJSONObject(i);
                                String id = jsonAlbum.getString("id");
                                String name = jsonAlbum.getString("name");

                                AlbumsAdapter.AlbumItem album = new AlbumsAdapter.AlbumItem(id, name);
                                albumList.add(album);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        AlbumsAdapter adapter = new AlbumsAdapter(albumList);
                        recyclerView.setAdapter(adapter);

                        if(albumList.size() == 0)
                            noAlbumsView.setVisibility(View.VISIBLE);
                    }
                }
        ).executeAsync();
    }

    /**
     * A Facebook Graph call that fetches the user's name and profile picture.
     */
    private void fetchUser()
    {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,picture");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response)
                    {
                        if (response.getError() != null)
                        {
                            Toast.makeText(AlbumsActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONObject jsonResponse = response.getJSONObject();
                        try
                        {
                            String firstName = jsonResponse.getString("first_name");
                            JSONObject picture = jsonResponse.getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            String url = data.getString("url");

                            if (firstName.endsWith("s"))
                                firstName = firstName.concat("' ");
                            else
                                firstName = firstName.concat("'s ");

                            firstName = firstName.concat(getResources().getString(R.string.albums_label));
                            albumsTitleView.setText(firstName);
                            displayProfilePic(userPicture, url);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     * On clicking a Facebook photo album, the AlbumActivity is launched.
     * @param view
     */
    public void launchAlbumActivity(View view)
    {
        Button button = (Button) view;
        String name = button.getText().toString();
        String id = (String) button.getTag();
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    /**
     * Logs the user out of Facebook.
     */
    private void launchLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * On clicking the user's profile picture,
     * a popup menu appears with the option to logout of Facebook.
     * @param view
     */
    public void onLogout(View view)
    {
        PopupMenu menu = new PopupMenu(AlbumsActivity.this, userPicture);
        menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                LoginManager.getInstance().logOut();
                launchLoginActivity();
                return true;
            }
        });

        menu.show();
    }
}
