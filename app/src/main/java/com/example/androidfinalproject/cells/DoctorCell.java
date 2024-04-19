package com.app.onetapmedico.cells;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onetapmedico.R;
import com.app.onetapmedico.databinding.CellDoctorBinding;
import com.app.onetapmedico.models.DoctorItem;

import java.util.Locale;


public class DoctorCell extends RecyclerView.ViewHolder {

    private final CellDoctorBinding binding;

    public static DoctorCell instance(ViewGroup group) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.cell_doctor, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new DoctorCell(view);
    }

    public DoctorCell(@NonNull View itemView) {
        super(itemView);
        binding = CellDoctorBinding.bind(itemView);
    }

    public void set(DoctorItem doctorItem) {
        binding.tvName.setText(String.format(Locale.getDefault(), "%s, %s", doctorItem.dname, doctorItem.dspeciality));
        binding.tvAddress.setText(doctorItem.haddress);
        binding.tvCity.setText(doctorItem.hcity);
    }

    public void call(View.OnClickListener onClickListener) {
        binding.btnCall.setOnClickListener(onClickListener);
    }

    public void email(View.OnClickListener onClickListener) {
        binding.btnEmail.setOnClickListener(onClickListener);
    }

    public void appointment(View.OnClickListener onClickListener) {
        binding.btnTakeAppointment.setOnClickListener(onClickListener);
    }

}
