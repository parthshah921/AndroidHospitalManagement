package com.app.onetapmedico.cells;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onetapmedico.R;
import com.app.onetapmedico.databinding.CellAppointmentBinding;
import com.app.onetapmedico.models.AppointmentItem;

import java.util.Locale;


public class AppointmentCell extends RecyclerView.ViewHolder {

    private final CellAppointmentBinding binding;

    public static AppointmentCell instance(ViewGroup group) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.cell_appointment, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new AppointmentCell(view);
    }

    public AppointmentCell(@NonNull View itemView) {
        super(itemView);
        binding = CellAppointmentBinding.bind(itemView);
    }

    public void set(AppointmentItem appointmentItem) {
        binding.tvPatient.setText(appointmentItem.pname);
        binding.tvDoctor.setText(appointmentItem.dname);
        binding.tvStatus.setText(appointmentItem.astatus);
        binding.tvAppointmentDate.setText(String.format(Locale.getDefault(), "Appointment Date: %s", appointmentItem.adate));
    }

}
