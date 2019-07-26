package com.xumengqi.lclab;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MineFragment extends Fragment implements View.OnClickListener {
    private LinearLayout ll_mine_not_login, ll_mine_warning;
    private ScrollView sv_mine_login;
    private TextView tv_mine_account, tv_mine_height, tv_mine_weight, tv_mine_sex, tv_mine_birth, tv_mine_whr, tv_mine_exercise, tv_mine_goal;
    private LinearLayout ll_mine_login, ll_mine_height, ll_mine_weight, ll_mine_sex, ll_mine_birth, ll_mine_whr, ll_mine_exercise, ll_mine_goal, ll_mine_activity, ll_mine_order_form, ll_mine_address, ll_mine_password, ll_mine_logout, ll_mine_about;

    private final int NOT_LOGIN = 100, LOGGING = 101, LOGIN_SUCCESSFULLY = 102, LOGIN_FAILED_BY_SYSTEM = 103, LOGIN_FAILED_BY_NETWORK = 104, MODIFIED_FAILED_BY_NETWORK = 105, MODIFIED_FAILED_BY_NOT_LOGIN = 106, MODIFIED_FAILED_BY_INVALID = 107;
    private User user;
    private String account, password;
    /** 使用官方的写法，防止内存泄漏 */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case NOT_LOGIN:
                    /* 没有登录 */
                    sv_mine_login.setVisibility(View.GONE);
                    ll_mine_not_login.setVisibility(View.VISIBLE);
                    ll_mine_warning.setVisibility(View.GONE);
                    break;
                case LOGGING:
                    /* 登录中 */
                    sv_mine_login.setVisibility(View.VISIBLE);
                    ll_mine_not_login.setVisibility(View.GONE);
                    ll_mine_warning.setVisibility(View.GONE);
                    tv_mine_account.setText((account + "(登录中...)"));
                    break;
                case LOGIN_SUCCESSFULLY:
                    tv_mine_account.setText(account);
                    if (user.getHeight_cm() != 0) {
                        tv_mine_height.setText((user.getHeight_cm() + "cm"));
                    }
                    if (user.getWeight_kg() != 0) {
                        tv_mine_weight.setText((user.getWeight_kg() + "kg"));
                    }
                    if (user.getGender() != null) {
                        tv_mine_sex.setText(user.getGender());
                    }
                    if (user.getBirthday() != null) {
                        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
                        String dateString = dateFormat.format(user.getBirthday());
                        tv_mine_birth.setText(dateString);
                    }
                    if (user.getWaist_to_hip_ratio() != 0) {
                        tv_mine_whr.setText((user.getWaist_to_hip_ratio() + ""));
                    }
                    if (user.getExercise_volume() != null) {
                        tv_mine_exercise.setText(user.getExercise_volume());
                    }
                    if (user.getDietary_target() != null) {
                        tv_mine_goal.setText(user.getDietary_target());
                    }
                    break;
                case LOGIN_FAILED_BY_NETWORK:
                    ll_mine_warning.setVisibility(View.VISIBLE);
                    tv_mine_account.setText((account + "(登录失败)"));
                    break;
                case LOGIN_FAILED_BY_SYSTEM:
                    tv_mine_account.setText((account + "(系统异常)"));
                    break;
                case MODIFIED_FAILED_BY_NETWORK:
                    Toast.makeText(getContext(), "网络异常，修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case MODIFIED_FAILED_BY_NOT_LOGIN:
                    Toast.makeText(getContext(), "未登录，修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case MODIFIED_FAILED_BY_INVALID:
                    Toast.makeText(getContext(), "无效的输入，修改失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 加载帧视图 */
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        ll_mine_not_login = view.findViewById(R.id.ll_mine_not_login);
        sv_mine_login = view.findViewById(R.id.sv_mine_login);
        tv_mine_account = view.findViewById(R.id.tv_mine_account);
        tv_mine_height = view.findViewById(R.id.tv_mine_height);
        tv_mine_weight = view.findViewById(R.id.tv_mine_weight);
        tv_mine_sex = view.findViewById(R.id.tv_mine_sex);
        tv_mine_birth = view.findViewById(R.id.tv_mine_birth);
        tv_mine_whr = view.findViewById(R.id.tv_mine_whr);
        tv_mine_exercise = view.findViewById(R.id.tv_mine_exercise);
        tv_mine_goal = view.findViewById(R.id.tv_mine_goal);

        ll_mine_warning = view.findViewById(R.id.ll_mine_warning);
        ll_mine_warning.setOnClickListener(this);
        ll_mine_login = view.findViewById(R.id.ll_mine_login);
        ll_mine_login.setOnClickListener(this);
        ll_mine_height = view.findViewById(R.id.ll_mine_height);
        ll_mine_height.setOnClickListener(this);
        ll_mine_weight = view.findViewById(R.id.ll_mine_weight);
        ll_mine_weight.setOnClickListener(this);
        ll_mine_sex = view.findViewById(R.id.ll_mine_sex);
        ll_mine_sex.setOnClickListener(this);
        ll_mine_birth = view.findViewById(R.id.ll_mine_birth);
        ll_mine_birth.setOnClickListener(this);
        ll_mine_whr = view.findViewById(R.id.ll_mine_whr);
        ll_mine_whr.setOnClickListener(this);
        ll_mine_exercise = view.findViewById(R.id.ll_mine_exercise);
        ll_mine_exercise.setOnClickListener(this);
        ll_mine_goal = view.findViewById(R.id.ll_mine_goal);
        ll_mine_goal.setOnClickListener(this);
        ll_mine_activity = view.findViewById(R.id.ll_mine_activity);
        ll_mine_activity.setOnClickListener(this);
        ll_mine_order_form = view.findViewById(R.id.ll_mine_order_form);
        ll_mine_order_form.setOnClickListener(this);
        ll_mine_address = view.findViewById(R.id.ll_mine_address);
        ll_mine_address.setOnClickListener(this);
        ll_mine_password = view.findViewById(R.id.ll_mine_password);
        ll_mine_password.setOnClickListener(this);
        ll_mine_logout = view.findViewById(R.id.ll_mine_logout);
        ll_mine_logout.setOnClickListener(this);
        ll_mine_about = view.findViewById(R.id.ll_mine_about);
        ll_mine_about.setOnClickListener(this);

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("lc_lab_user", MODE_PRIVATE);
        //account =  sharedPreferences.getString("account",null);
        //password =  sharedPreferences.getString("password",null);
        account = "17839741231";
        password = "17839741231";
        if (account == null) {
            /* 没有登录 */
            handler.sendMessage(theMessage(NOT_LOGIN));
        }
        else {
            /* 尝试登录 */
            login();
        }
        return view;
    }

    public void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendMessage(theMessage(LOGGING));
                DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
                databaseConnector.connectToDatabase();
                user = databaseConnector.getUserByAccount(account, password);
                databaseConnector.closeDatabase();
                if (user != null) {
                    handler.sendMessage(theMessage(LOGIN_SUCCESSFULLY));
                }
                else {
                    NetworkChecker networkChecker = new NetworkChecker(getContext());
                    if (networkChecker.isConnecting()) {
                        /* 系统故障 */
                        handler.sendMessage(theMessage(LOGIN_FAILED_BY_SYSTEM));
                    }
                    else {
                        /* 断网了 */
                        handler.sendMessage(theMessage(LOGIN_FAILED_BY_NETWORK));
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_mine_login:
                break;
            case R.id.ll_mine_warning:
                login();
                break;
            case R.id.ll_mine_height:
                showHeightDialog();
                break;
            case R.id.ll_mine_weight:
                showWeightDialog();
                break;
            case R.id.ll_mine_sex:
                showSexDialog();
                break;
            case R.id.ll_mine_birth:
                showBirthDialog();
                break;
            case R.id.ll_mine_whr:
                showWhrDialog();
                break;
            case R.id.ll_mine_exercise:
                showExerciseDialog();
                break;
            case R.id.ll_mine_goal:
                showGoalDialog();
                break;
            case R.id.ll_mine_activity:
                break;
            case R.id.ll_mine_order_form:
                break;
            case R.id.ll_mine_address:
                break;
            case R.id.ll_mine_password:
                break;
            case R.id.ll_mine_logout:
                break;
            case R.id.ll_mine_about:
                break;
        }
    }

    private Message theMessage(int what) {
        Message message = new Message();
        message.what = what;
        return message;
    }
    private void showHeightDialog() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("在此输入您的身高（厘米）");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(editText).setTitle(" ").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (user == null) {
                            handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                        }
                        else {
                            String string = editText.getText().toString();
                            if (string.equals("")) {
                                handler.sendMessage(theMessage(MODIFIED_FAILED_BY_INVALID));
                            }
                            else {
                                user.setHeight_cm(Double.parseDouble(string));
                            }
                            modifyUser();
                        }
                    }
                });
        builder.create().show();
    }
    private void showWeightDialog() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("在此输入您的体重（公斤）");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(editText).setTitle(" ").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    String string = editText.getText().toString();
                    if (string.equals("")) {
                        handler.sendMessage(theMessage(MODIFIED_FAILED_BY_INVALID));
                    }
                    else {
                        user.setWeight_kg(Double.parseDouble(string));
                    }
                    modifyUser();
                }
            }
        });
        builder.create().show();
    }
    private void showSexDialog(){
        final String[] items = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    user.setGender(items[which]);
                    modifyUser();
                }
            }
        });
        builder.show();
    }
    private void showBirthDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        Date date = calendar.getTime();
                        if (user == null) {
                            handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                        }
                        else {
                            user.setBirthday(date);
                            modifyUser();
                        }
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void showWhrDialog() {
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setHint("在此输入您的腰臀比");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(editText).setTitle(" ").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    String string = editText.getText().toString();
                    if (string.equals("")) {
                        handler.sendMessage(theMessage(MODIFIED_FAILED_BY_INVALID));
                    }
                    else {
                        user.setWaist_to_hip_ratio(Double.parseDouble(string));
                    }
                    modifyUser();
                }

            }
        });
        builder.create().show();
    }
    private void showExerciseDialog(){
        final String[] items = {"从不运动", "每周1~3次", "每周3~5次"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext());
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    user.setExercise_volume(items[which]);
                    modifyUser();
                }
            }
        });
        listDialog.show();
    }
    private void showGoalDialog(){
        final String[] items = {"增肌", "减脂"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext());
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    user.setDietary_target(items[which]);
                    modifyUser();
                }
            }
        });
        listDialog.show();
    }

    public void modifyUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
                databaseConnector.connectToDatabase();
                if (databaseConnector.updateUser(user.getAccount(), user.getPassword(), user.getPassword(), user.getHeight_cm(), user.getWeight_kg(), user.getBirthday(), user.getGender(), user.getWaist_to_hip_ratio(), user.getExercise_volume(), user.getDietary_target())) {
                    handler.sendMessage(theMessage(LOGIN_SUCCESSFULLY));
                }
                else {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NETWORK));
                }
                databaseConnector.closeDatabase();
            }
        }).start();
    }
}
