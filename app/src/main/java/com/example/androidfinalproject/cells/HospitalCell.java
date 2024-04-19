package com.app.onetapmedico.cells;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onetapmedico.R;
import com.app.onetapmedico.databinding.CellHospitalBinding;
import com.app.onetapmedico.models.HospitalItem;

import java.util.Locale;


public class HospitalCell extends RecyclerView.ViewHolder {

    private final CellHospitalBinding binding;

    public static HospitalCell instance(ViewGroup group) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.cell_hospital, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new HospitalCell(view);
    }

    public HospitalCell(@NonNull View itemView) {
        super(itemView);
        binding = CellHospitalBinding.bind(itemView);
    }

    public void set(HospitalItem hospitalItem) {
        binding.tvName.setText(String.format(Locale.getDefault(), "%s, %s", hospitalItem.hname, hospitalItem.hspecialfor));
        binding.tvAddress.setText(hospitalItem.hcity);
        binding.tvOpenDays.setText(hospitalItem.hdays);
        binding.tvOpenTime.setText(hospitalItem.htime);
    }

    public void call(View.OnClickListener onClickListener) {
        binding.btnCall.setOnClickListener(onClickListener);
    }

}
