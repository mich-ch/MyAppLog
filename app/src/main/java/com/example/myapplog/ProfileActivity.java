package com.example.myapplog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private EditText txtViewUsrnm, txtViewUsrEml;
    private CircleImageView img;
    private String url_string = "";
    private int url_int;
    private String url_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(!SharedPrefManage.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        initVal();
    }

    private void initVal() {
        txtViewUsrnm = (EditText) findViewById(R.id.textViewUsername);
        txtViewUsrnm.setFocusableInTouchMode(false);
        txtViewUsrEml = (EditText) findViewById(R.id.textViewUseremail);
        txtViewUsrEml.setFocusableInTouchMode(false);
        txtViewUsrEml.setText(SharedPrefManage.getInstance(this).getUserEmail());
        txtViewUsrnm.setText(SharedPrefManage.getInstance(this).getUsername());
        url_int = (SharedPrefManage.getInstance(this).getUserId());
        url_id = Integer.toString(url_int);
        url_string = "http://197.164.3.223/dbuser/v1/profile_image/" + url_id + ".jpeg";
        img = (CircleImageView) findViewById(R.id.imgV);
        ImageRequest imageRequest = new ImageRequest(url_string,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        img.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Upload profile photo from settings menu!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        RequestHandler.getInstance(this).addToRequestQueue(imageRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                SharedPrefManage.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.menuSettings:
                Toast.makeText(this, "You clicked settings", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                finish();
                break;
        }
        return true;
    }


}
