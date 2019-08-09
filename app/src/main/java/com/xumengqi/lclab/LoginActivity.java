package com.xumengqi.lclab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xumengqi
 * @date 2019/08/04
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText etLoginUsername, etLoginPassword;
    private ProgressDialog progressDialogLogin, progressDialogRegister;

    private final int LOGGING = 101, LOGIN_FAILED_BY_NETWORK = 102, LOGIN_FAILED_BY_NORMAL = 103, LOGIN_SUCCESSFULLY = 104, REGISTERING = 105, REGISTER_SUCCESSFULLY = 106, REGISTER_FAILED_BY_NETWORK = 107, REGISTERING_FAILED_BY_EXIST = 108;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOGGING:
                    progressDialogLogin.show();
                    break;
                case LOGIN_SUCCESSFULLY:
                    progressDialogLogin.dismiss();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    break;
                case LOGIN_FAILED_BY_NORMAL:
                    progressDialogLogin.dismiss();
                    LcLabToolkit.showToastHint(LoginActivity.this, "登录失败，账户或密码不正确", R.drawable.error);
                    break;
                case LOGIN_FAILED_BY_NETWORK:
                    progressDialogLogin.dismiss();
                    LcLabToolkit.showToastHint(LoginActivity.this, "登录失败，已断开网络连接", R.drawable.error);
                    break;
                case REGISTERING:
                    progressDialogRegister.show();
                    break;
                case REGISTER_SUCCESSFULLY:
                    progressDialogRegister.dismiss();
                    Toast.makeText(LoginActivity.this, "注册并登录成功", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                    Intent intentRegister = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intentRegister);
                    break;
                case REGISTERING_FAILED_BY_EXIST:
                    progressDialogRegister.dismiss();
                    LcLabToolkit.showToastHint(LoginActivity.this, "注册失败，该用户已被注册", R.drawable.error);
                    break;
                case REGISTER_FAILED_BY_NETWORK:
                    progressDialogRegister.dismiss();
                    LcLabToolkit.showToastHint(LoginActivity.this, "注册失败，已断开网络连接", R.drawable.error);
                    break;
                    default:
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLoginUsername = findViewById(R.id.et_login_username);
        etLoginPassword = findViewById(R.id.et_login_password);
        Button btnLoginLogin = findViewById(R.id.btn_login_login);
        btnLoginLogin.setOnClickListener(this);
        Button btnLoginRegister = findViewById(R.id.btn_login_register);
        btnLoginRegister.setOnClickListener(this);
        ImageButton ibLoginBack = findViewById(R.id.ib_login_back);
        ibLoginBack.setOnClickListener(this);

        initializeProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login:
                final String account = etLoginUsername.getText().toString();
                final String password = etLoginPassword.getText().toString();
                final int accountLength = 11;
                if (account.length() == accountLength) {
                    login(account, password);
                }
                else {
                    LcLabToolkit.showToastHint(LoginActivity.this, "请输入正确的手机号", R.drawable.plaint);
                }
                break;
            case R.id.btn_login_register:
                final String accountRegister = etLoginUsername.getText().toString();
                final String passwordRegister = etLoginPassword.getText().toString();
                if (isValidInputForRegistering(accountRegister, passwordRegister)) {
                    register(accountRegister, passwordRegister);
                }
                break;
            case R.id.ib_login_back:
                ActivityCollector.finishActivity(LoginActivity.this);
                break;
                default:
        }
    }
    public void login(final String account, final String password) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /* 正在登录 */
                handler.sendMessage(theMessage(LOGGING));
                DatabaseConnector databaseConnector = new DatabaseConnector(LoginActivity.this);
                databaseConnector.connectToDatabase();
                Boolean isLoginSuccessfully = databaseConnector.loginAccount(account, password);
                databaseConnector.closeDatabase();
                if (isLoginSuccessfully) {
                    SharedPreferences sharedPreferences = getSharedPreferences("lc_lab_user_information", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.apply();
                    /* 登录成功 */
                    handler.sendMessage(theMessage(LOGIN_SUCCESSFULLY));
                }
                else {
                    if (LcLabToolkit.isConnectingForNetwork(LoginActivity.this)) {
                        /* 连接网络时登录失败，可能是密码不对，或账户不存在，或者是系统问题 */
                        handler.sendMessage(theMessage(LOGIN_FAILED_BY_NORMAL));
                    }
                    else {
                        /* 登录失败，因为没有连接到网络 */
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

    public void register(final String accountRegister, final String passwordRegister) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /* 正在注册 */
                handler.sendMessage(theMessage(REGISTERING));
                DatabaseConnector databaseConnector = new DatabaseConnector(LoginActivity.this);
                databaseConnector.connectToDatabase();
                Boolean isRegisterSuccessfully = databaseConnector.registerNewAccount(accountRegister, passwordRegister);
                databaseConnector.closeDatabase();
                if (isRegisterSuccessfully) {
                    SharedPreferences sharedPreferences = getSharedPreferences("lc_lab_user_information", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("account", accountRegister);
                    editor.putString("password", passwordRegister);
                    editor.apply();
                    /* 注册成功 */
                    handler.sendMessage(theMessage(REGISTER_SUCCESSFULLY));
                }
                else {
                    if (LcLabToolkit.isConnectingForNetwork(LoginActivity.this)) {
                        /* 连接网络时注册失败，可能是账户已被注册，或者是系统问题 */
                        handler.sendMessage(theMessage(REGISTERING_FAILED_BY_EXIST));
                    }
                    else {
                        /* 登录失败，因为没有连接到网络 */
                        handler.sendMessage(theMessage(REGISTER_FAILED_BY_NETWORK));
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

    public boolean isValidInputForRegistering(String account, String password) {
        final int accountLength = 11, minLengthOfPassword = 6;
        if (account.length() != accountLength) {
            LcLabToolkit.showToastHint(LoginActivity.this, "请输入正确的手机号", R.drawable.plaint);
            return false;
        }
        if (password == null || password.length() < minLengthOfPassword) {
            LcLabToolkit.showToastHint(LoginActivity.this, "密码过短", R.drawable.plaint);
            return false;
        }
        return true;
    }

    private Message theMessage(int what) {
        Message message = new Message();
        message.what = what;
        return message;
    }

    private void initializeProgressDialog() {
        progressDialogLogin = new ProgressDialog(this);
        progressDialogLogin.setMessage("正在登录");
        progressDialogLogin.setCancelable(false);
        progressDialogRegister = new ProgressDialog(this);
        progressDialogRegister.setMessage("正在注册");
        progressDialogRegister.setCancelable(false);
    }
}
