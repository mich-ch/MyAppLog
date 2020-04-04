package com.example.myapplog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTxtUsrnm, editTxtEml, editTxtPsswrd;
    private Button bttnRgstr;
    private ProgressDialog progressDialog;
    private TextView txtViewLgn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPrefManage.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        initValues();
    }

    private void registerUser() {
        final String email = editTxtEml.getText().toString().trim();
        final String username = editTxtUsrnm.getText().toString().trim();
        final String password = editTxtPsswrd.getText().toString().trim();

        progressDialog.setMessage("Registering user..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            SharedPrefManage.getInstance(getApplicationContext())
                                    .userLogin(
                                            jsonObject.getInt("id"),
                                            jsonObject.getString("username"),
                                            jsonObject.getString("email")
                                    );
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void initValues () {
        editTxtEml = (EditText) findViewById(R.id.editTextEmail);
        editTxtUsrnm = (EditText) findViewById(R.id.editTextUsername);
        editTxtPsswrd = (EditText) findViewById(R.id.editTextPassword);
        bttnRgstr = (Button) findViewById(R.id.buttonRegister);
        bttnRgstr.setOnClickListener(this);
        txtViewLgn = (TextView) findViewById(R.id.textViewLogin);
        txtViewLgn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        if(v == bttnRgstr) {
            registerUser();
        }
        if(v == txtViewLgn) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
