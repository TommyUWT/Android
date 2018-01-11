package tv.vanriper.fconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Thomas Van Riper
 * 10 Jan 2018
 *
 * The LoginActivity implements Facebook login with read permissions for the user's
 * photos.
 */
public class LoginActivity extends AppCompatActivity
{
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        fbLoginButton.setReadPermissions("user_photos");
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                launchContactsActivity();
            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException error)
            {
                String toastMessage = error.getMessage();
                Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });

        com.facebook.AccessToken loginToken = com.facebook.AccessToken.getCurrentAccessToken();
        if (loginToken != null)
            launchContactsActivity();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void launchContactsActivity()
    {
        Intent intent = new Intent(this, AlbumsActivity.class);
        startActivity(intent);
        finish();
    }
}
