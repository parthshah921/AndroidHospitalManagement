package com.app.onetapmedico.cells;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onetapmedico.R;
import com.app.onetapmedico.databinding.CellReportBinding;
import com.app.onetapmedico.models.ReportItem;

import java.util.Locale;


public class ReportCell extends RecyclerView.ViewHolder {

    private final CellReportBinding binding;

    public static ReportCell instance(ViewGroup group) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.cell_report, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ReportCell(view);
    }

    public ReportCell(@NonNull View itemView) {
        super(itemView);
        binding = CellReportBinding.bind(itemView);
    }

    public void set(ReportItem reportItem) {
        binding.tvPatient.setText(reportItem.pname);
        binding.tvDoctor.setText(reportItem.dname);
        binding.tvAppointmentDate.setText(String.format(Locale.getDefault(), "Prescription Date: %s", reportItem.preDate));
        binding.tvPrescription.setText(reportItem.prescriptionDtls);
        binding.btnDownload.setVisibility(TextUtils.isEmpty(reportItem.preFile) ? View.GONE : View.VISIBLE);
    }


    public void download(View.OnClickListener onClickListener) {
        binding.btnDownload.setOnClickListener(onClickListener);
    }

}
