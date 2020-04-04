package com.example.myapplog;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTxtUsrnm, editTxtPsswrd;
    private Button bttnLgn;
    private ProgressDialog progressDialog;
    private TextView txtViewRgstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(SharedPrefManage.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }
        initValues();
    }

    private void userLogin() {
        final String username = editTxtUsrnm.getText().toString().trim();
        final String password = editTxtPsswrd.getText().toString().trim();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_lOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                           JSONObject obj = new JSONObject(response);
                           if(!obj.getBoolean("error")) {
                                SharedPrefManage.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("id"),
                                                obj.getString("username"),
                                                obj.getString("email")
                                        );
                                 startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                 finish();
                           }else{
                               Toast.makeText(
                                       getApplicationContext(),
                                       obj.getString("message"),
                                       Toast.LENGTH_LONG
                               ).show();
                           }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void initValues() {
        editTxtUsrnm = (EditText) findViewById(R.id.editTextUsername);
        editTxtPsswrd = (EditText) findViewById(R.id.editTextPassword);
        bttnLgn = (Button) findViewById(R.id.buttonLogin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        bttnLgn.setOnClickListener(this);
        txtViewRgstr = (TextView) findViewById(R.id.textViewReg);
        txtViewRgstr.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == bttnLgn) {
            userLogin();
        }
        if(v == txtViewRgstr) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
