package com.example.edusubsidio.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edusubsidio.R;
import com.example.edusubsidio.model.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder>{
    private List<Student> studentList;
    private Context context;

    public StudentAdapter(List<Student> studentList, Context context){
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_student,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
        holder.txtCedula.setText(studentList.get(position).getCedula());
        holder.txtNombre.setText(studentList.get(position).getNombreCompleto());
        holder.txtIngreso.setText(studentList.get(position).getFechaIngreso());
        holder.txtSalida.setText(studentList.get(position).getFechaSalida());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCedula,txtNombre,txtIngreso,txtSalida;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCedula = itemView.findViewById(R.id.txtCedula);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtIngreso = itemView.findViewById(R.id.txtIngreso);
            txtSalida = itemView.findViewById(R.id.txtSalida);

        }
    }
}
