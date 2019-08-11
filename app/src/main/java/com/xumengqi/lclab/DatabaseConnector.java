package com.xumengqi.lclab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 数据库连接器：
 * 此类将保证从此类获取到的数据的有效性，一切数据加载异常都由此类处理，即外部类获取的数据是默认有效的，故不做异常处理
 * 使用须知：
 * 1.需要在 build.gradle 中添加 implementation 'mysql:mysql-connector-java:5.1.18'
 * 2.需要获取网络全权限 <uses-permission android:name="android.permission.INTERNET"/>，高版本手机需要设置额外网络权限;
 * 3.需要当前数据库登录账户是有相关权限的
 * 4.Logcat 用 Info 搜索 DatabaseConnector_xmq
 * Last Modified：2019/08/05 by徐梦旗 2663479778@qq.com
 * @author xumengqi
 * @date 2019/08/04
 */
class DatabaseConnector {
    /** 数据库连接配置信息 */
    private static final String HOSTNAME = "rm-wz9bmh868vzio5p8swo.mysql.rds.aliyuncs.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "lc_lab";
    private static final String USER = "root";
    private static final String PASSWORD = "Root1234";
    private static final String URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DATABASE;
    private Connection connection = null;
    private Context context;

    /** 传入上下文 */
    DatabaseConnector(Context context) {
        this.context = context;
    }

    /** 连接到数据库 */
    void connectToDatabase() {
        notifyState("正在连接数据库<" + DATABASE + ">...");
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            notifyState("连接数据库<" + DATABASE + ">成功");
        } else {
            notifyState("连接数据库<" + DATABASE + ">失败");
        }
    }

    /** 断开与数据库的连接 */
    void closeDatabase() {
        if (connection == null) {
            notifyState("没有连接上数据库，所以没必要关闭数据库<" + DATABASE + ")");
        } else {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            notifyState("关闭数据库<" + DATABASE + ">成功");
        }
    }

    /** 获取菜品列表 */
    List<Dish> getAllDish() {
        String table = "lc_lab_food_information_table";
        notifyState("正在表<" + table + ">中查找所有行，并返回List<Dish>...");
        List<Dish> dishList = new ArrayList<>();
        if (connection != null) {
            try {
                String queryAllInTableSql = "select * from " + table;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(queryAllInTableSql);
                while (resultSet.next()) {
                    Dish dish = new Dish(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            /* 获取图片 */
                            getPictureByIdOrUrl(context, resultSet.getInt("id"), resultSet.getString("name")),
                            resultSet.getDouble("price"),
                            resultSet.getDouble("calorie_k_calorie"),
                            resultSet.getString("category"),
                            resultSet.getString("ingredient"),
                            resultSet.getDouble("carbohydrate_g"),
                            resultSet.getDouble("fat_g"),
                            resultSet.getDouble("protein_g"),
                            resultSet.getDouble("dietary_fiber_g"),
                            resultSet.getString("notes")
                    );
                    dishList.add(dish);
                }
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (dishList.size() == 0) {
            notifyState("表<" + table + ">可能是个空表，或未连接到数据库");
        } else {
            notifyState("遍历<" + table + ">，并返回List<Dish>成功");
        }
        return dishList;
    }

    /** 根据菜品ID来找到相应菜品 */
    Dish getDishById(int id) {
        String table = "lc_lab_food_information_table";
        notifyState("正在表" + table + "中查找id为" + id + "的首次行，并返回Dish...");
        Dish dish = null;
        if (connection != null) {
            try {
                String queryOneInTableSql = "select * from " + table + " where id = " + id;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(queryOneInTableSql);

                if (resultSet.first()) {
                    dish = new Dish(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            /* 获取图片 */
                            getPictureByIdOrUrl(context, resultSet.getInt("id"), resultSet.getString("name")),
                            resultSet.getDouble("price"),
                            resultSet.getDouble("calorie_k_calorie"),
                            resultSet.getString("category"),
                            resultSet.getString("ingredient"),
                            resultSet.getDouble("carbohydrate_g"),
                            resultSet.getDouble("fat_g"),
                            resultSet.getDouble("protein_g"),
                            resultSet.getDouble("dietary_fiber_g"),
                            resultSet.getString("notes")
                    );
                    resultSet.close();
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (dish == null) {
            notifyState("在表" + table + "中没有找到id为" + id + "的行，或未连接到数据库");
        } else {
            notifyState("在表" + table + "中找到了id为" + id + "的行，返回Dish成功");
        }
        return dish;
    }

    /** 根据账户名来获取账户信息 */
    User getUserByAccount(String account, String password) {
        String table = "lc_lab_user_information_table";
        notifyState("正在表<" + table + ">中查找account为<" + account + ">的首次行，并返回User...");
        User user = null;
        if (connection != null) {
            try {
                String queryOneInTableSql = "select * from " + table + " where account = " + account + " and password = '" + password + "'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(queryOneInTableSql);
                if (resultSet.first()) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("account"),
                            resultSet.getString("password"),
                            null,
                            resultSet.getDouble("height_cm"),
                            resultSet.getDouble("weight_kg"),
                            resultSet.getDate("birthday"),
                            resultSet.getString("gender"),
                            resultSet.getDouble("waist_to_hip_ratio"),
                            resultSet.getString("exercise_volume"),
                            resultSet.getString("dietary_target"),
                            resultSet.getString("notes")
                    );
                }
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (user == null) {
            notifyState("在表<" + table + ">中没有找到account为<" + account + ">的行，或未连接到数据库，或密码不正确");
        } else {
            notifyState("在表<" + table + ">中找到了account为<" + account + ">的行，返回User成功");
        }
        return user;
    }

    /** 更新账户信息 */
    Boolean updateUser(
            String account,
            String password,
            String passwordNew,
            double heightCm,
            double weightKg,
            Date birthday,
            String gender,
            double waistToHipRatio,
            String exerciseVolume,
            String dietaryTarget) {
        String table = "lc_lab_user_information_table";
        notifyState("正在表<" + table + ">中查找account为<" + account + ">的首次行，并更新除id，account和notes外的其他信息...");
        try {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement("update " + table + " set password=?, height_cm=?, weight_kg=?, birthday=?, gender=?, waist_to_hip_ratio=?, exercise_volume=?, dietary_target=? where account = ? and password = ?");
                preparedStatement.setString(1, passwordNew);
                preparedStatement.setDouble(2, heightCm);
                preparedStatement.setDouble(3, weightKg);
                preparedStatement.setDate(4, birthday == null ? null : new java.sql.Date(birthday.getTime()));
                preparedStatement.setString(5, gender);
                preparedStatement.setDouble(6, waistToHipRatio);
                preparedStatement.setString(7, exerciseVolume);
                preparedStatement.setString(8, dietaryTarget);
                preparedStatement.setString(9, account);
                preparedStatement.setString(10, password);
                if (preparedStatement.executeUpdate() == 1) {
                    preparedStatement.close();
                    notifyState("在表<" + table + ">中更新account为<" + account + ">的首次行成功");
                    return true;
                } else {
                    preparedStatement.close();
                    notifyState("在表<" + table + ">中更新account为<" + account + ">的首次行失败，没有找到该账户");
                    return false;
                }
            }
            else {
                notifyState("在表<" + table + ">中更新account为<" + account + ">的首次行失败，没有连接到数据库");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notifyState("在表<" + table + ">中更新account为<" + account + ">的首次行失败，其他原因");
        return false;
    }

    /** 注册一个新账户 */
    Boolean registerNewAccount(String account, String password) {
        /* 连接数据库失败，则直接返回false */
        if (connection == null) {
            notifyState("注册账户<" + account + ">失败，未连接到数据库");
            return false;
        }
        int idMax = 0;
        String table = "lc_lab_user_information_table";
        String findMaxIdSql = "SELECT MAX(id) FROM " + table;
        String isExistTheAccountSqlRegister = "select * from " + table + " where account = " + account;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(findMaxIdSql);
            if (resultSet.first()) {
                idMax = resultSet.getInt(1);
            }
            resultSet = statement.executeQuery(isExistTheAccountSqlRegister);
            if (resultSet.first()) {
                notifyState("注册账户<" + account + ">失败，可能是因为该账户已存在");
                return false;
            } else {
                String registerSql = "INSERT INTO " + table + " (`id`, `account`, `password`) VALUES (" + (idMax + 1) + ", '" + account + "', '" + password + "')";
                statement.execute(registerSql);
                notifyState("注册账户<" + account + ">成功");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 登录一个账户 */
    Boolean loginAccount(String account, String password) {
        /* 连接数据库失败，则直接返回false */
        if (connection == null) {
            notifyState("登录账户<" + account + ">失败，未连接到数据库");
            return false;
        }
        String table = "lc_lab_user_information_table";
        String isExistTheAccountSqlLogin = "select * from " + table + " where account = " + account + " and password = '" + password + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isExistTheAccountSqlLogin);
            if (resultSet.first()) {
                notifyState("登录<" + account + ">成功");
                return true;
            } else {
                notifyState("登录<" + account + ">失败，密码错误或未注册");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 获取图片，首先在本地寻找，若没有找到，则从云端寻找 */
    private Bitmap getPictureByIdOrUrl(Context context, int id, String fileName) {
        final String url = "http://106.15.39.96/media/food_information_table_images/";
        String drawableName = "food_picture" + id;
        int resId = context.getResources().getIdentifier(drawableName , "drawable", Objects.requireNonNull(context).getPackageName());
        /* 加载过多或过大图片会引起OOM */
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        return bitmap == null ? getHttpBitmap(context, url + fileName + ".jpg") : bitmap;
    }

    /** 从网址获取图片，并配置没有找到时显示的图片 */
    private Bitmap getHttpBitmap(Context context, String url) {
        URL myFileUrl;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
            /* 获得连接 */
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            /* 设置超时时间为3000毫秒，conn.setConnectionTime(0);表示没有时间限制 */
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            /* 连接设置获得数据流 */
            conn.setDoInput(true);
            /* 不使用缓存 */
            conn.setUseCaches(false);
            /* 得到数据流 */
            InputStream is = conn.getInputStream();
            /* 解析得到图片 */
            bitmap = BitmapFactory.decodeStream(is);
            /* 关闭数据流 */
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.load_picture_failed);
        }
        else {
            return bitmap;
        }
    }

    /** 日志通知 */
    private void notifyState(String string) {
        Log.i("DatabaseConnector_xmq", string);
    }
}
