package com.example.edusubsidio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edusubsidio.dao.UserDao;
import com.example.edusubsidio.interfaces.DefaultControls;
import com.example.edusubsidio.model.User;
import com.example.edusubsidio.views.DashboardActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DefaultControls {
    private TextInputEditText txtInputUser, txtInputPass;
    private Button btnLogin;
    private RequestQueue requestQueue;
    private  Intent intent;
    private UserDao userDao;
    private User user;
    private CardView cardLogin,cardProgress;
    private String URL_API = "https://api-app.teranet.net.ec/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkControls();
        loginPrincipal();

        btnLogin.setOnClickListener(v -> {
            String user = getTextInput(txtInputUser);
            String pass = getTextInput(txtInputPass);
            if (validateEditText(user,pass)){
               loginForUser(user,pass,true);
            }
        });
    }

    @Override
    public void linkControls() {
        requestQueue = Volley.newRequestQueue(this);
        userDao = new UserDao(this);

        txtInputUser = findViewById(R.id.txtInputUser);
        txtInputPass = findViewById(R.id.txtInputPass);
        btnLogin = findViewById(R.id.btnLogin);
        cardLogin = findViewById(R.id.cardLogin);
        cardProgress = findViewById(R.id.cardProgress);
    }

    private Boolean validateEditText(String user,String pass){
        if (user.trim().equals("")){
            txtInputUser.setError("El usuario es requerido");
            return false;
        }
        if (pass.trim().equals("")){
            txtInputPass.setError("El password es requerido");
            return false;
        }
        return true;
    }

    private void loginPrincipal(){
        userDao.open();
        cardProgress.setVisibility(View.VISIBLE);
        Boolean auth = userDao.getVerify();
        if (auth){
            user = userDao.getVerifyOne();
            loginForUser(user.getUsu_usuario(),user.getUsu_contra(),false);
        }else {
            cardProgress.setVisibility(View.GONE);
            cardLogin.setVisibility(View.VISIBLE);
        }
        userDao.close();
    }

    private void saveStorage(String user,String pass){
        userDao.open();
        userDao.insertUser(new User(user,pass));
        userDao.close();
    }

    private String getTextInput(TextInputEditText ed){
        String text = ed.getEditableText().toString();
        return text;
    }

    public void loginForUser(String username,String password,Boolean storage){
        if (storage){
            cardLogin.setVisibility(View.GONE);
            cardProgress.setVisibility(View.VISIBLE);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_API + "/login-app", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String data = jsonObject.getString("data");
                if (status.equals("ok")){
                    if (storage){
                        saveStorage(username,password);
                    }
                    cardLogin.setVisibility(View.VISIBLE);
                    cardProgress.setVisibility(View.GONE);
                    customToast("Bienvenido");
                    intent = new Intent(this, DashboardActivity.class);
                    intent.putExtra("DATA",data);
                    startActivity(intent);
                    txtInputUser.setText("");
                    txtInputPass.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                cardLogin.setVisibility(View.VISIBLE);
                cardProgress.setVisibility(View.GONE);
            }
        },error -> {
            if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                try {
                    String errorResponse = new String(error.networkResponse.data, "UTF-8");
                    JSONObject errorJSON = new JSONObject(errorResponse);
                    String errorMessage = errorJSON.getString("message");
                    customToast(errorMessage);
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    customToast("Error desconocido");
                }
            } else {
                customToast("Error de conexión de red");
            }
            cardLogin.setVisibility(View.VISIBLE);
            cardProgress.setVisibility(View.GONE);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usu_usuario", username);
                params.put("usu_contra", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void customToast(String msg){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.toast,(ViewGroup) findViewById(R.id.custom_toast_layout));
        TextView txvMsg = view.findViewById(R.id.custom_toast_text);
        txvMsg.setText(msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM,0,200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}