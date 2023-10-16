package com.example.edusubsidio.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edusubsidio.R;
import com.example.edusubsidio.dao.UserDao;
import com.example.edusubsidio.interfaces.DefaultControls;
import com.example.edusubsidio.model.Student;
import com.example.edusubsidio.ui.StudentAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardActivity extends AppCompatActivity implements DefaultControls {
    private AlertDialog.Builder builder;
    private UserDao userDao;
    private Button btnLogout;
    private Button fabScanner;
    private RequestQueue requestQueue;
    private String user_id;
    private RecyclerView rcvStudents;
    private StudentAdapter studentAdapter;
    private TextView txtTotales;
    private String URL_API = "https://api-app.teranet.net.ec/api";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        linkControls();

        Intent intent = getIntent();

        user_id = intent.getStringExtra("DATA");
        getRegisters(user_id);



        btnLogout.setOnClickListener(v -> {
            logout();
        });

        fabScanner.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Coloca el código QR del estudiante dentro del visor para escanear.");
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        String textData = intentResult.getContents();

        uploadRegister(textData,user_id);

    }

    private void uploadRegister(String cardId,String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_API + "/escaneo-qr", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String data = jsonObject.getString("data");
                if (status.equals("ok")){
                    customToast(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("estu_cedula", cardId);
                params.put("usuario_id", userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getRegisters(String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_API + "/lista-escaneos-qr", response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("ok")) {
                    String total = jsonObject.getString("total");
                    txtTotales.setText("Escaneos totales: "+total);
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<Student> estudianteList = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject estudianteObject = data.getJSONObject(i);
                        Student estudiante = new Student();
                        estudiante.setId(estudianteObject.getInt("id"));
                        estudiante.setEstudianteId(estudianteObject.getString("estudiante_id"));
                        estudiante.setCedula(estudianteObject.getString("cedula"));
                        estudiante.setNombreCompleto(estudianteObject.getString("nombre_completo"));
                        estudiante.setFechaIngreso(estudianteObject.getString("fecha_ingreso"));
                        estudiante.setFechaSalida(estudianteObject.getString("fecha_salida"));
                        estudianteList.add(estudiante);
                    }
                    studentAdapter = new StudentAdapter(estudianteList,this);
                    rcvStudents.setAdapter(studentAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                try {
                    String errorResponse = new String(error.networkResponse.data, "UTF-8");
                    JSONObject errorJSON = new JSONObject(errorResponse);
                    String errorMessage = errorJSON.getString("message");
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    customToast("Error desconocido");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario_id", id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public void linkControls() {
        userDao = new UserDao(this);
        builder = new AlertDialog.Builder(this);
        requestQueue = Volley.newRequestQueue(this);

        btnLogout = findViewById(R.id.btnLogout);
        fabScanner = findViewById(R.id.fabScanner);
        rcvStudents = findViewById(R.id.rcvStudents);
        txtTotales = findViewById(R.id.txtTotales);

        rcvStudents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void trashLocal(){
        userDao.open();
        userDao.deleteUsers();
        userDao.close();
    }

    private void logout(){
        builder.setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Si", (dialogInterface, i) -> {
                    trashLocal();
                    finish();
                })
                .setNegativeButton("Cancelar",(dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRegisters(user_id);
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