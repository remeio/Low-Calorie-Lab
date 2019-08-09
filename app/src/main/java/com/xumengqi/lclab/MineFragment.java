package com.xumengqi.lclab;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private LinearLayout llMineNotLogin, llMineWarning;
    private ScrollView svMineLogin;
    private TextView tvMineAccount, tvMineHeight, tvMineWeight, tvMineSex, tvMineBirth, tvMineWhr, tvMineExercise, tvMineGoal;

    private final int NOT_LOGIN = 100, LOGGING = 101, LOGIN_SUCCESSFULLY = 102, LOGIN_FAILED_BY_SYSTEM = 103, LOGIN_FAILED_BY_NETWORK = 104, MODIFIED_FAILED_BY_NETWORK = 105, MODIFIED_FAILED_BY_NOT_LOGIN = 106, MODIFIED_FAILED_BY_INVALID = 107, MODIFIED_SUCCESSFULLY = 108;
    private User user;
    private String account, password;
    /** 使用官方的写法，防止内存泄漏 */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case NOT_LOGIN:
                    /* 没有登录 */
                    svMineLogin.setVisibility(View.GONE);
                    llMineNotLogin.setVisibility(View.VISIBLE);
                    llMineWarning.setVisibility(View.GONE);
                    break;
                case LOGGING:
                    /* 登录中 */
                    svMineLogin.setVisibility(View.VISIBLE);
                    llMineNotLogin.setVisibility(View.GONE);
                    llMineWarning.setVisibility(View.GONE);
                    tvMineAccount.setText(account);
                    break;
                case LOGIN_SUCCESSFULLY:
                    updateUserView();
                    LcLabToolkit.setUser(user);
                    break;
                case LOGIN_FAILED_BY_NETWORK:
                    llMineWarning.setVisibility(View.VISIBLE);
                    LcLabToolkit.showToastHint(getContext(), "登录失败，网络异常", R.drawable.error);
                    break;
                case LOGIN_FAILED_BY_SYSTEM:
                    LcLabToolkit.showToastHint(getContext(), "登录失败，系统异常", R.drawable.error);
                    break;
                case MODIFIED_SUCCESSFULLY:
                    updateUserView();
                    LcLabToolkit.setUser(user);
                    LcLabToolkit.setReadyToUpdate(true);
                    break;
                case MODIFIED_FAILED_BY_NETWORK:
                    LcLabToolkit.showToastHint(getContext(), "修改失败，网络异常", R.drawable.error);
                    break;
                case MODIFIED_FAILED_BY_NOT_LOGIN:
                    LcLabToolkit.showToastHint(getContext(), "修改失败，未登录", R.drawable.error);
                    break;
                case MODIFIED_FAILED_BY_INVALID:
                    LcLabToolkit.showToastHint(getContext(), "修改失败，无效的输入", R.drawable.plaint);
                    break;
                    default:
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 加载帧视图 */
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        llMineNotLogin = view.findViewById(R.id.ll_mine_not_login);
        svMineLogin = view.findViewById(R.id.sv_mine_login);
        tvMineAccount = view.findViewById(R.id.tv_mine_account);
        tvMineHeight = view.findViewById(R.id.tv_mine_height);
        tvMineWeight = view.findViewById(R.id.tv_mine_weight);
        tvMineSex = view.findViewById(R.id.tv_mine_sex);
        tvMineBirth = view.findViewById(R.id.tv_mine_birth);
        tvMineWhr = view.findViewById(R.id.tv_mine_whr);
        tvMineExercise = view.findViewById(R.id.tv_mine_exercise);
        tvMineGoal = view.findViewById(R.id.tv_mine_goal);
        llMineWarning = view.findViewById(R.id.ll_mine_warning);
        llMineWarning.setOnClickListener(this);
        LinearLayout llMineLogin = view.findViewById(R.id.ll_mine_login);
        llMineLogin.setOnClickListener(this);
        LinearLayout llMineHeight = view.findViewById(R.id.ll_mine_height);
        llMineHeight.setOnClickListener(this);
        LinearLayout llMineWeight = view.findViewById(R.id.ll_mine_weight);
        llMineWeight.setOnClickListener(this);
        LinearLayout llMineSex = view.findViewById(R.id.ll_mine_sex);
        llMineSex.setOnClickListener(this);
        LinearLayout llMineBirth = view.findViewById(R.id.ll_mine_birth);
        llMineBirth.setOnClickListener(this);
        LinearLayout llMineWhr = view.findViewById(R.id.ll_mine_whr);
        llMineWhr.setOnClickListener(this);
        LinearLayout llMineExercise = view.findViewById(R.id.ll_mine_exercise);
        llMineExercise.setOnClickListener(this);
        LinearLayout llMineGoal = view.findViewById(R.id.ll_mine_goal);
        llMineGoal.setOnClickListener(this);
        LinearLayout llMineOrderForm = view.findViewById(R.id.ll_mine_order_form);
        llMineOrderForm.setOnClickListener(this);
        LinearLayout llMineAddress = view.findViewById(R.id.ll_mine_address);
        llMineAddress.setOnClickListener(this);
        LinearLayout llMinePassword = view.findViewById(R.id.ll_mine_password);
        llMinePassword.setOnClickListener(this);
        LinearLayout llMineChangeAccount = view.findViewById(R.id.ll_mine_change_account);
        llMineChangeAccount.setOnClickListener(this);
        LinearLayout llMineLogout = view.findViewById(R.id.ll_mine_logout);
        llMineLogout.setOnClickListener(this);
        LinearLayout llMineAbout = view.findViewById(R.id.ll_mine_about);
        llMineAbout.setOnClickListener(this);

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("lc_lab_user_information", MODE_PRIVATE);
        account =  sharedPreferences.getString("account",null);
        password =  sharedPreferences.getString("password",null);
        if (account == null) {
            /* 未曾登录 */
            handler.sendMessage(theMessage(NOT_LOGIN));
        }
        else {
            /* 尝试登录 */
            login();
        }
        return view;
    }

    public void login() {
        Runnable runnable = new Runnable() {
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
                    if (LcLabToolkit.isConnectingForNetwork(Objects.requireNonNull(getContext()))) {
                        /* 系统故障 */
                        handler.sendMessage(theMessage(LOGIN_FAILED_BY_SYSTEM));
                    }
                    else {
                        /* 断网了 */
                        handler.sendMessage(theMessage(LOGIN_FAILED_BY_NETWORK));
                    }
                }
            }
        };
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                1,1,10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(runnable);
        threadPool.shutdown();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_mine_login:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
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
            case R.id.ll_mine_order_form:
                break;
            case R.id.ll_mine_address:
                break;
            case R.id.ll_mine_password:
                break;
            case R.id.ll_mine_change_account:
                Intent intentLogin = new Intent(getContext(), LoginActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.ll_mine_logout:
                showLogoutDialog();
                break;
            case R.id.ll_mine_about:
                break;
                default:
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
        builder.setView(editText).setTitle(" ");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    String string = editText.getText().toString();
                    double maximumOfHeight = 300;
                    if ("".equals(string) || Double.parseDouble(string) > maximumOfHeight) {
                        handler.sendMessage(theMessage(MODIFIED_FAILED_BY_INVALID));
                    }
                    else {
                        user.setHeight(Double.parseDouble(string));
                    }
                    modifyUser();
                }
            }
        });
        builder.setNegativeButton("取消", null);
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
                    double maximumOfWeight = 200;
                    if ("".equals(string) || Double.parseDouble(string) > maximumOfWeight) {
                        handler.sendMessage(theMessage(MODIFIED_FAILED_BY_INVALID));
                    }
                    else {
                        user.setWeight(Double.parseDouble(string));
                    }
                    modifyUser();
                }
            }
        });
        builder.setNegativeButton("取消", null);
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
                    double minimumOfWhr = 0.5, maximumOfWhr = 2;
                    if ("".equals(string) || Double.parseDouble(string) < minimumOfWhr || Double.parseDouble(string) > maximumOfWhr) {
                        handler.sendMessage(theMessage(MODIFIED_FAILED_BY_INVALID));
                    }
                    else {
                        user.setWaistToHipRatio(Double.parseDouble(string));
                    }
                    modifyUser();
                }

            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
    private void showExerciseDialog(){
        final String[] items = {"从不运动", "每周1~3次", "每周3~5次", "每周6~7次"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext());
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if (user == null) {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NOT_LOGIN));
                }
                else {
                    user.setExerciseVolume(items[which]);
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
                    user.setDietaryTarget(items[which]);
                    modifyUser();
                }
            }
        });
        listDialog.show();
    }
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("是否注销该账户");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("lc_lab_user_information", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("account");
                editor.remove("password");
                editor.remove("calorie");
                editor.remove("goal");
                editor.apply();
                LcLabToolkit.setUser(null);
                handler.sendMessage(theMessage(NOT_LOGIN));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void modifyUser() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
                databaseConnector.connectToDatabase();
                if (databaseConnector.updateUser(user.getAccount(), user.getPassword(), user.getPassword(), user.getHeight(), user.getWeight(), user.getBirthday(), user.getGender(), user.getWaistToHipRatio(), user.getExerciseVolume(), user.getDietaryTarget())) {
                    handler.sendMessage(theMessage(MODIFIED_SUCCESSFULLY));
                }
                else {
                    handler.sendMessage(theMessage(MODIFIED_FAILED_BY_NETWORK));
                }
                databaseConnector.closeDatabase();
            }
        };
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                1,1,10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(runnable);
        threadPool.shutdown();
    }

    public void updateUserView() {
        tvMineAccount.setText(account);
        if (user.getHeight() != 0) {
            tvMineHeight.setText((user.getHeight() + "cm"));
        }
        if (user.getWeight() != 0) {
            tvMineWeight.setText((user.getWeight() + "kg"));
        }
        if (user.getGender() != null) {
            tvMineSex.setText(user.getGender());
        }
        if (user.getBirthday() != null) {
            DateFormat dateFormat = SimpleDateFormat.getDateInstance();
            String dateString = dateFormat.format(user.getBirthday());
            tvMineBirth.setText(dateString);
        }
        if (user.getWaistToHipRatio() != 0) {
            tvMineWhr.setText((user.getWaistToHipRatio() + ""));
        }
        if (user.getExerciseVolume() != null) {
            tvMineExercise.setText(user.getExerciseVolume());
        }
        if (user.getDietaryTarget() != null) {
            tvMineGoal.setText(user.getDietaryTarget());
        }
    }
}
