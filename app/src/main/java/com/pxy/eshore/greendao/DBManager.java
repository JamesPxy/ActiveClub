//package com.pxy.eshore.greendao;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.widget.Toast;
//
//import com.pxy.greendao.gen.DaoMaster;
//import com.pxy.greendao.gen.DaoSession;
//import com.pxy.greendao.gen.UserDao;
//
//import org.greenrobot.greendao.database.Database;
//import org.greenrobot.greendao.query.QueryBuilder;
//
//import java.util.List;
//
//import mypractice.pxy.com.mypractice.entity.User;
//
//
///**
// *@author JamesPxy
// *@time   2017/12/1  15:58
// *@Description  greenDao数据库管理
// */
//public class DBManager {
//
//    /**
//     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
//     */
//    public static final boolean ENCRYPTED = false;
//
//    private DaoSession daoSession;
//
//    //数据库名称与 表名称不同  包含的关系
//    private final static String dbName = "test_db";
//    private static DBManager mInstance;
//    private DaoMaster.DevOpenHelper openHelper;
//    private Context context;
//
//    private UserDao userDao;
//
//    /**
//     * 获取单例引用
//     *
//     * @param context
//     * @return
//     */
//    public static DBManager getInstance(Context context) {
//        if (mInstance == null) {
//            synchronized (DBManager.class) {
//                if (mInstance == null) {
//                    mInstance = new DBManager(context);
//                }
//            }
//        }
//        return mInstance;
//    }
//
//
//    public DBManager(Context context) {
//        this.context = context;
////      openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        //初始化相关操作
//        init();
//    }
//
//    public void init() {
//        openHelper = new DaoMaster.DevOpenHelper(context, ENCRYPTED ? "notes-db-encrypted" : dbName);
//        Database db = ENCRYPTED ? openHelper.getEncryptedWritableDb("<your-secret-password>") : openHelper.getWritableDb();
//        daoSession = new DaoMaster(db).newSession();
//    }
//
//    /**
//     * 获取可读数据库
//     */
//    private SQLiteDatabase getReadableDatabase() {
//        if (openHelper == null) {
//            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        }
//        return openHelper.getReadableDatabase();
//    }
//
//    /**
//     * 获取可写数据库
//     */
//    private SQLiteDatabase getWritableDatabase() {
//        if (openHelper == null) {
//            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        }
//        SQLiteDatabase db = openHelper.getWritableDatabase();
//        return db;
//    }
//
//    public UserDao getUserDao() {
//        if (daoSession == null) {
//            init();
//        }
//        if (userDao == null) {
//            userDao = daoSession.getUserDao();
//        }
//        return userDao;
//    }
//
//    /**
//     * 插入一条记录
//     *
//     * @param user
//     */
//    public void insertUser(User user) {
//        getUserDao();
////      userDao.insert(user);
//        userDao.insertOrReplace(user);
//    }
//
//    /**
//     * 插入用户集合
//     *
//     * @param users
//     */
//    public void insertUserList(final List<User> users) {
//        if (users == null || users.isEmpty()) {
//            return;
//        }
//        getUserDao();
//        userDao.insertInTx(users);
//
////        userDao.getSession().runInTx(new Runnable() {
////            @Override
////            public void run() {
////               for(User data:users){
////                   userDao.insertOrReplace(data);
////               }
////            }
////        });
//    }
//
//    /**
//     * 更新数据  根据id来更新  因为id具有唯一性
//     *
//     * @param user
//     */
//    public void updateUser(User user) {
//        getUserDao();
//        userDao.update(user);
//    }
//
//    /**
//     * 查询用户列表
//     */
//    public List<User> queryAllUser() {
//        getUserDao();
////      userDao.load(id);
//        return userDao.loadAll();
//    }
//
//    /**
//     * 查询特定条件下 多条数据 并排序
//     *
//     * @param age
//     * @return
//     */
//    public List<User> queryUserByage(int age) {
//        getUserDao();
//        QueryBuilder<User> qb = userDao.queryBuilder();
////        List<User> userList = (List<User>) mUserDao.queryBuilder().where(UserDao.Properties.Id.le(10)).build().list();
//        //查询指定年龄所有用户数据 按照id从高到低排序
////        List<User> list = qb.where(UserDao.Properties.Age.eq(age)).orderDesc(UserDao.Properties.Id).list();
//        //查询某个年龄段的用户
//        List<User> list = qb.where(UserDao.Properties.Age.between(20,30)).orderDesc(UserDao.Properties.Id).list();
//
//        return list;
//    }
//
//    /**
//     * 查询一条  该名字数据
//     */
//    public User queryUserByName(String name) {
//        getUserDao();
//        QueryBuilder<User> qb = userDao.queryBuilder();
//
//        //返回数据可能不唯一
//        User user = qb.where(UserDao.Properties.Name.eq(name)).uniqueOrThrow();
//
//        return user;
//    }
//
//    /**
//     * 删除一条记录
//     *
//     * @param user
//     */
//    public void deleteUser(long id) {
//        getUserDao();
//
//        User mUser = userDao.queryBuilder().where(UserDao.Properties.Id.eq(id)).build().unique();
//        if (mUser == null) {
//            Toast.makeText(context, "用户不存在", Toast.LENGTH_SHORT).show();
//        } else {
//            //根据主键删除
//            userDao.deleteByKey(mUser.getId());
//        }
//    }
//
//    /**
//     * 删除所有数据
//     */
//    public void deleteAll() {
//        //删除所有数据
//        getUserDao().deleteAll();
//    }
//
//}
