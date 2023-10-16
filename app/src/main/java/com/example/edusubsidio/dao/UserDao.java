package com.example.edusubsidio.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.edusubsidio.database.DataBaseHelper;
import com.example.edusubsidio.model.User;

public class UserDao {
    private SQLiteDatabase database;
    private DataBaseHelper dbHelper;
    private Context context;
    public UserDao(Context context) {
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put("usu_usuario", user.getUsu_usuario());
        values.put("usu_contra", user.getUsu_contra());
        database.insert("usuario", null, values);
    }

    public void deleteUsers(){
        try {
            database.execSQL("DELETE FROM usuario");
        }catch (SQLException err){
            err.printStackTrace();
        }
    }

    public User getVerifyOne(){
        Cursor cursor = null;
        User user = null;
        try {

            cursor = database.query("usuario", null, null,null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Se encontró un estudiante con la cédula especificada
                user = new User();
                user.setUsu_usuario(cursor.getString(1));
                user.setUsu_contra(cursor.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    public Boolean getVerify(){
        Cursor cursor = null;
        User user = null;
        try {
            cursor = database.query("usuario", null, null,null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return  true;
            }else{
                return  false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
}
