package com.xumengqi.lclab;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private List<Record> recordList;

    RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOneRecordDate, tvOneRecordCalorie;
        ViewHolder(View view) {
            super(view);
            tvOneRecordDate = view.findViewById(R.id.tv_one_record_date);
            tvOneRecordCalorie = view.findViewById(R.id.tv_one_record_calorie);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* 绑定项 */
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_one_record, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Record record = recordList.get(i);
        DateFormat dateFormat = DateFormat.getDateInstance();
        viewHolder.tvOneRecordDate.setText(dateFormat.format(record.getDate()));
        viewHolder.tvOneRecordCalorie.setText((record.getCalorie() + "千卡"));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
