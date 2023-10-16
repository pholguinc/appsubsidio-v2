package com.example.edusubsidio.model;

public class User {
    private int id_user;
    private String usu_usuario;
    private String usu_contra;

    public User() {
    }

    public User(String usu_usuario, String usu_contra) {
        this.usu_usuario = usu_usuario;
        this.usu_contra = usu_contra;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUsu_usuario() {
        return usu_usuario;
    }

    public void setUsu_usuario(String usu_usuario) {
        this.usu_usuario = usu_usuario;
    }

    public String getUsu_contra() {
        return usu_contra;
    }

    public void setUsu_contra(String usu_contra) {
        this.usu_contra = usu_contra;
    }
}
