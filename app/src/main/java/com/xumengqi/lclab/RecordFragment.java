package com.xumengqi.lclab;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class RecordFragment extends Fragment {
    private List<Record> recordList;
    private RecordAdapter recordAdapter;
    private int numberOfPoints;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 加载帧视图 */
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        recordList = getRecordList(view.getContext());
        RecyclerView rvRecord = view.findViewById(R.id.rv_record);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
        rvRecord.setLayoutManager(gridLayoutManager);
        recordAdapter = new RecordAdapter(recordList);
        rvRecord.setAdapter(recordAdapter);
        numberOfPoints = 7;
        drawRecord(view, recordList, numberOfPoints);

        FloatingActionButton fabRecord = view.findViewById(R.id.fab_record);
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        /* 切换状态 */
        final Button btnRecordWeek = view.findViewById(R.id.btn_record_week);
        final Button btnRecordMonth = view.findViewById(R.id.btn_record_month);
        btnRecordWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfPoints = 7;
                drawRecord(getView(), recordList, numberOfPoints);
                btnRecordWeek.setBackgroundColor(Color.parseColor("#0ABFFF"));
                btnRecordWeek.setTextColor(Color.parseColor("#FFFFFF"));
                btnRecordMonth.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnRecordMonth.setTextColor(Color.parseColor("#333333"));
            }
        });
        btnRecordMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfPoints = 30;
                drawRecord(getView(), recordList, numberOfPoints);
                btnRecordMonth.setBackgroundColor(Color.parseColor("#0ABFFF"));
                btnRecordMonth.setTextColor(Color.parseColor("#FFFFFF"));
                btnRecordWeek.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnRecordWeek.setTextColor(Color.parseColor("#333333"));
            }
        });

        ImageButton ibRecordCleanAll = view.findViewById(R.id.ib_record_clean_all);
        ibRecordCleanAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("是否清空所有记录");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (recordList.size() != 0) {
                            cleanAllRecord(getContext());
                            recordList.clear();
                            recordAdapter.notifyDataSetChanged();
                            drawRecord(getView(), recordList, numberOfPoints);
                            LcLabToolkit.showToastHint(getContext(), "清除成功", R.drawable.ok);
                        }
                        else {
                            LcLabToolkit.showToastHint(getContext(), "无可清除记录", R.drawable.plaint);
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
        return view;
    }

    /** 来添加一条记录 */
    private void showAddDialog() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("请输入您今日摄入的卡路里（千卡）");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(editText).setTitle(" ").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String string = editText.getText().toString();
                int maximumOfCalorie = 5000;
                if ("".equals(string) || Integer.parseInt(string) > maximumOfCalorie) {
                    LcLabToolkit.showToastHint(getContext(), "添加失败，无效的输入", R.drawable.plaint);
                }
                else {
                    Record record = new Record(new Date(System.currentTimeMillis()), Integer.parseInt(string));
                    /* 数据库添加一条记录 */
                    invertRecord(getContext(), record);
                    /* 前端更新一条记录 */
                    recordList.add(0, record);
                    recordAdapter.notifyDataSetChanged();
                    /* 刷新图 */
                    drawRecord(getView(), recordList, numberOfPoints);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /** 画图 */
    public void drawRecord(View view, List<Record> recordList, int numberOfPoints) {
        /* 设置低卡记录 */
        initializeRecordManager(view);
        /* 设置空数据时显示提示界面 */
        LinearLayout llRecordMain = view.findViewById(R.id.ll_record_main);
        llRecordMain.setVisibility(recordList.size() == 0 ? View.GONE : View.VISIBLE);
        /* 清除布局 */
        LineChart lcRecordCalorie = view.findViewById(R.id.lc_record_calorie);
        lcRecordCalorie.clear();
        /* 设置数据 */
        List<Entry> entryList = new ArrayList<>();
        for (int i = 0; i < recordList.size() && i < numberOfPoints; i++) {
            int index = numberOfPoints > recordList.size() ? recordList.size() - i - 1 : numberOfPoints - i - 1;
            Record record = recordList.get(index);
            /* Entry的x必须递增 */
            entryList.add(new Entry(i + 1, record.getCalorie()));
        }

        LineDataSet lineDataSet = new LineDataSet(entryList, "千卡");
        LineData lineData = new LineData(lineDataSet);
        lcRecordCalorie.setData(lineData);

        /* 设置样式 */
        lcRecordCalorie.getDescription().setEnabled(false);
        lcRecordCalorie.setScaleEnabled(false);
        lcRecordCalorie.getAxisRight().setEnabled(false);

        int colorNumber = Color.parseColor("#0ABFFF");
        lineDataSet.setColor(colorNumber);
        lineDataSet.setCircleColor(colorNumber);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setHighLightColor(colorNumber);
        lineDataSet.setHighlightEnabled(true);

        XAxis xAxis = lcRecordCalorie.getXAxis();
        xAxis.setAxisMinimum(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = lcRecordCalorie.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setGranularityEnabled(true);
        yAxis.setGranularity(500f);
    }

    /** 从本地数据库获取记录列表 */
    public List<Record> getRecordList(Context context) {
        List<Record> recordList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("lc_lab_record_table", null, null, null, null, null, "date desc");
        if (cursor.moveToFirst()) {
            do {
                recordList.add(new Record(new Date(cursor.getLong(0)), cursor.getInt(1)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        sqLiteDatabase.close();
        databaseHelper.close();
        return recordList;
    }

    /** 向本地数据库中插入一条记录 */
    public void invertRecord(Context context, Record record) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", record.getDate().getTime());
        values.put("calorie_k_calorie", record.getCalorie());
        sqLiteDatabase.insert("lc_lab_record_table", null, values);
        sqLiteDatabase.close();
        databaseHelper.close();
    }

    public void cleanAllRecord(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from lc_lab_record_table");
        sqLiteDatabase.close();
        databaseHelper.close();
    }

    /** 设置个人健康管理 */
    public void initializeRecordManager(View view) {
        User user = LcLabToolkit.getUser();
        int calorieRecommend =  computeCalorieForUser(user);
        int week = 7;
        int calorieWeek = 0;
        for (int i = 0; i < recordList.size() && i < week; i++) {
            Record record = recordList.get(i);
            calorieWeek += calorieRecommend - record.getCalorie();
        }
        int month = 30;
        int calorieMonth = 0;
        for (int i = 0; i < recordList.size() && i < month; i++) {
            Record record = recordList.get(i);
            calorieMonth += calorieRecommend - record.getCalorie();
        }
        int calorieAll = 0;
        for (int i = 0; i < recordList.size(); i++) {
            Record record = recordList.get(i);
            calorieAll += calorieRecommend - record.getCalorie();
        }
        if (recordList.size() != 0) {
            TextView tvRecordRecommend = view.findViewById(R.id.tv_record_recommend);
            TextView tvRecordFact = view.findViewById(R.id.tv_record_fact);
            TextView tvRecordToday = view.findViewById(R.id.tv_record_today);
            TextView tvRecordWeek = view.findViewById(R.id.tv_record_week);
            TextView tvRecordMonth = view.findViewById(R.id.tv_record_month);
            TextView tvRecordAll = view.findViewById(R.id.tv_record_all);
            int checkNumber = -1;
            String checkString = "未登录";
            tvRecordRecommend.setText(calorieRecommend == checkNumber ? checkString : (calorieRecommend + "千卡"));
            tvRecordFact.setText((recordList.get(0).getCalorie() + "千卡"));
            tvRecordToday.setText(calorieRecommend == checkNumber ? checkString : (calorieRecommend - recordList.get(0).getCalorie() + "千卡"));
            tvRecordWeek.setText(calorieRecommend == checkNumber ? checkString : (calorieWeek + "千卡"));
            tvRecordMonth.setText(calorieRecommend == checkNumber? checkString : (calorieMonth + "千卡" ));
            tvRecordAll.setText(calorieRecommend == checkNumber ? checkString : (calorieAll + "千卡"));
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && LcLabToolkit.isReadyToUpdateRecord()) {
            initializeRecordManager(getView());
            LcLabToolkit.setReadyToUpdateRecord(false);
        }
    }

    /**
     * 计算基本代谢与运动卡路里
     * @param user 已登录用户
     * @return 该用户一天生命活动所消耗的卡路里
     */
    public int computeCalorieForUser(User user) {
        /* Men 10 x weight (kg) + 6.25 x height (cm) – 5 x age (y) + 5 */
        /* Women10 x weight (kg) + 6.25 x height (cm) – 5 x age (y) – 161 */
        if (user == null) {
            return -1;
        }
        double coefficientOfWeight = 10;
        double coefficientOfHeight = 6.25;
        double coefficientOfAge = -5;
        double coefficientOfMan = 5;
        double coefficientOfWoman = -161;
        double coefficientOfSportNo = 1.2;
        double coefficientOfSportLight = 1.375;
        double coefficientOfSportModerate = 1.55;
        double coefficientOfSportHard = 1.725;
        double ans = 0;
        if (user.getHeight() != 0 && user.getWeight() != 0) {
            ans += coefficientOfHeight * user.getHeight() + coefficientOfWeight * user.getWeight();
            if (user.getBirthday() != null) {
                int age = (int) ((System.currentTimeMillis() - user.getBirthday().getTime()) / 1000 / (365 * 24 * 60 * 60));
                ans += coefficientOfAge * age;
            }
            if (user.getGender() != null) {
                String gender = user.getGender();
                String[] strings = {"男", "女"};
                if (strings[0].equals(gender)) {
                    ans += coefficientOfMan;
                }
                else if (strings[1].equals(gender)){
                    ans += coefficientOfWoman;
                }
            }
            if (user.getExerciseVolume() != null) {
                String exerciseVolume = user.getExerciseVolume();
                String[] strings = {"从不运动", "每周1~3次", "每周3~5次", "每周6~7次"};
                if (strings[0].equals(exerciseVolume)) {
                    ans *= coefficientOfSportNo;
                }
                else if (strings[1].equals(exerciseVolume)) {
                    ans *= coefficientOfSportLight;
                }
                else if (strings[2].equals(exerciseVolume)) {
                    ans *= coefficientOfSportModerate;
                }
                else if (strings[3].equals(exerciseVolume)) {
                    ans *= coefficientOfSportHard;
                }
            }
        }
        return (int)ans;
    }
}
