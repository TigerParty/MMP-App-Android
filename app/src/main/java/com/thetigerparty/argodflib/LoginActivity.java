package com.thetigerparty.argodflib;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.HelperClass.PhoneUtil;
import com.thetigerparty.argodflib.Model.ArgoConfig;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.PermissionLevel;
import com.thetigerparty.argodflib.Model.Setting;
import com.thetigerparty.argodflib.Model.User;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.AutoDataSyncThread;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Service.CheckVersionTask;

import org.mindrot.jbcrypt.BCrypt;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ttpttp on 2015/2/27.
 */
public class LoginActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private final String TAG = this.getClass().getSimpleName();

    String DATA_SYNC_API = "";
    String CHECK_VERSION_API = "";
    public String SIMPLE_DATE_FORMAT = "";
    String PROGRESS_DIALOG_DATA_SYNC = "";
    static String TOAST_DATA_SYNC_FAIL = "";
    static String TOAST_DATA_SYNC_SUCCESS = "";
    String TOAST_LOGIN_FAIL = "";
    String TOAST_CHECK_INTERNET_FALSE = "";
    String TOAST_SYNC_DATA_FIRST = "";
    static String TOAST_DATA_SYNC_TIMEOUT = "";
    static String TOAST_DATA_SYNC_FIRST_TIMEOUT = "";

    final static int DATA_SYNC_FAILED = 1;
    final static int DATA_SYNC_SUCCEED = 2;
    final static int DATA_SYNC_TIMEOUT = 3;
    final static int DATA_SYNC_FIRST_TIMEOUT = 4;

    private final static int REQUEST_CODE_PERMISSION = 100;

    ViewGroup header;

    Button bt_login;
    Button bt_data_sync;
    Button bt_login_guest;

    EditText et_username;
    EditText et_password;

    TextView tv_last_synced_time;

    ProgressDialog pd;

    UserObject obj_user = new UserObject();

    private Handler handler = new MsgHandler(this);

    String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
    }

    static class MsgHandler extends Handler{
        WeakReference<LoginActivity> loginActivity;
        MsgHandler(LoginActivity activity){
            loginActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg){
            LoginActivity activity = loginActivity.get();
            switch(msg.what)
            {
                case DATA_SYNC_FAILED:
                    Toast.makeText(activity, TOAST_DATA_SYNC_FAIL, Toast.LENGTH_LONG).show();
                    break;
                case DATA_SYNC_SUCCEED:
                    Toast.makeText(activity, TOAST_DATA_SYNC_SUCCESS, Toast.LENGTH_LONG).show();
                    break;
                case DATA_SYNC_TIMEOUT:
                    Toast.makeText(activity, TOAST_DATA_SYNC_TIMEOUT, Toast.LENGTH_LONG).show();
                    break;
                case DATA_SYNC_FIRST_TIMEOUT:
                    Toast.makeText(activity, TOAST_DATA_SYNC_FIRST_TIMEOUT, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ActiveAndroid.initialize(this.getApplication());

        // new install app seeding (is not upgrade)
        if (!ArgoConfig.hasConfig()) {
            ActiveAndroid.execSQL("INSERT INTO argo_config (\"key\", \"value\") VALUES (\"remember_username\", \"\")");
            ActiveAndroid.execSQL("INSERT INTO argo_config (\"key\", \"value\") VALUES (\"remember_password\", \"\")");
            ActiveAndroid.execSQL("INSERT INTO argo_config (\"key\", \"value\") VALUES (\"remember_reporter_name\", \"\")");
            ActiveAndroid.execSQL("INSERT INTO argo_config (\"key\", \"value\") VALUES (\"remember_reporter_email\", \"\")");
        }

        getResourceValue();
        findView();

        CheckVersionTask checkVersionTask = new CheckVersionTask(this, new CheckVersionTask.CheckVersionTaskListener() {
            @Override
            public void onSuccess(boolean available, String link) {
                Log.d(TAG, "onSuccess: " + available);
                Log.d(TAG, "onSuccess: " + link);

                if (available) {
                    Dialog.needToUpdateDialog(LoginActivity.this, link);
                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: ");
            }
        });
        checkVersionTask.execute();

        if (!EasyPermissions.hasPermissions(LoginActivity.this, permissions)) {
            EasyPermissions.requestPermissions(LoginActivity.this, getString(R.string.permission_dialog_content), REQUEST_CODE_PERMISSION, permissions);
        }

        startService(new Intent(this, BackgroundRunnerService.class));
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(Config.checkInternet(this)) {
            this.download();
        } else {
            Toast.makeText(LoginActivity.this, TOAST_CHECK_INTERNET_FALSE, Toast.LENGTH_LONG).show();
        }
        et_username.setText(ArgoConfig.getUsername());
        et_password.setText(ArgoConfig.getPassword());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bt_login = null;
        bt_data_sync = null;
        et_username = null;
        et_password = null;
        tv_last_synced_time = null;
        pd = null;
        obj_user = null;
        handler = null;
        bt_login_guest = null;

        Runtime.getRuntime().gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Dialog.exitAppDialog(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    void getResourceValue(){
        DATA_SYNC_API = BuildConfig.DOWNLOAD_DATA_API;
        CHECK_VERSION_API = BuildConfig.CURRENT_VERSION_API;
        SIMPLE_DATE_FORMAT = getString(R.string.sdf_ymd_hms);
        TOAST_DATA_SYNC_FAIL = getString(R.string.data_sync_fail);
        TOAST_DATA_SYNC_SUCCESS = getString(R.string.data_sync_success);
        TOAST_CHECK_INTERNET_FALSE = getString(R.string.check_internet_false);
        TOAST_LOGIN_FAIL = getString(R.string.login_fail);
        TOAST_SYNC_DATA_FIRST = getString(R.string.sync_data_first);
        PROGRESS_DIALOG_DATA_SYNC = getString(R.string.data_sync);
        TOAST_DATA_SYNC_TIMEOUT = getString(R.string.data_sync_timeout);
        TOAST_DATA_SYNC_FIRST_TIMEOUT = getString(R.string.data_sync_first_and_timeout);
    }

    void findView() {
        header = (ViewGroup) findViewById(R.id.layout_header);
        header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                StringBuilder message = new StringBuilder();
                Map<String, String> map = PhoneUtil.getDeviceInfo(LoginActivity.this);
                for (Map.Entry entry : map.entrySet()){
                    message.append(entry.getKey() + ": " + entry.getValue() + "\n");
                }

                Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), message.toString(), Snackbar.LENGTH_LONG);
                TextView tv = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setMaxLines(map.size());
                snackBar.show();

                return true;
            }
        });

        et_username = (EditText) this.findViewById(R.id.et_username);
        et_password = (EditText) this.findViewById(R.id.et_password);

        bt_login = (Button) this.findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginByUserName();
            }
        });

        bt_data_sync = (Button) this.findViewById(R.id.bt_data_sync);
        bt_data_sync.setVisibility(View.GONE);

        tv_last_synced_time = (TextView) this.findViewById(R.id.tv_last_synced_time);

        bt_login_guest = (Button) this.findViewById(R.id.bt_login_guest);
        bt_login_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginByGuest();
            }
        });
        bt_login_guest.setVisibility(BuildConfig.GUEST_LOGIN_ENABLE ? View.VISIBLE : View.GONE);
    }

    private boolean checkAuth() {
        boolean bool_login = false;

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        if(!username.equals("") && !password.equals("")) {
            try{
                User user = User.select(username);
                if(BCrypt.checkpw(password, user.password)) {
                    int permissionPriority = PermissionLevel.getPriorityById(user.permission_level_id);
                    obj_user.setUserId(user.user_id);
                    obj_user.setName(user.name);
                    obj_user.setPassword(user.password);
                    obj_user.setPermissionPriority(permissionPriority);
                    obj_user.setEmail(user.email);
                    bool_login = true;

                    Logger.d("Login successfully!");
                }
            }
            catch (Exception e){
                Logger.e(e.getMessage());
            }
        }

        return bool_login;
    }

    private void download(){
        pd = ProgressDialog.show(this, "", PROGRESS_DIALOG_DATA_SYNC, true);
        final AutoDataSyncThread dataSyncThread = new AutoDataSyncThread(getApplicationContext());

        Thread downloadThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    dataSyncThread.download();
                    handler.sendEmptyMessage(DATA_SYNC_SUCCEED);
                } catch(SocketTimeoutException timeoutError) {
                    if (getLastSyncedTime().equals("")) {
                        handler.sendEmptyMessage(DATA_SYNC_FIRST_TIMEOUT);
                    } else {
                        handler.sendEmptyMessage(DATA_SYNC_TIMEOUT);
                    }
                    Logger.e(timeoutError.getMessage());
                } catch (Exception e) {
                    handler.sendEmptyMessage(DATA_SYNC_FAILED);
                    Logger.e(e.getMessage());
                } finally {
                    pd.dismiss();
                }
            }
        };
        downloadThread.setDaemon(true);
        downloadThread.start();
    }

    String getLastSyncedTime(){
        String last_synced_datetime = "";
        Setting setting = Setting.select();
        if(setting != null){
            last_synced_datetime = setting.last_synced_datetime;
        }

        return last_synced_datetime;
    }

    private void loginByUserName() {
        if (getLastSyncedTime().equals("")) {
            Toast.makeText(LoginActivity.this, TOAST_SYNC_DATA_FIRST, Toast.LENGTH_LONG).show();
        } else if (!checkAuth()) {
            Toast.makeText(LoginActivity.this, TOAST_LOGIN_FAIL, Toast.LENGTH_LONG).show();
        } else if (LocalProject.hasNotSubmitLocalProjectByOtherUser(obj_user.getUserId())) {
            Dialog.checkNotSubmitLocalprojectDialog(LoginActivity.this);
        } else {
            enterMainActivity();
        }
    }

    private void loginByGuest() {
        if (!getLastSyncedTime().equals("")) {
            ArgoConfig.updateUsernameAndPassword("", "");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, TOAST_SYNC_DATA_FIRST, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteLocalProjectByOtherUserId() {
        LocalProject.deleteLocalProjectByOtherUser(obj_user.getUserId());
    }

    public void enterMainActivity() {
        ArgoConfig.updateUsernameAndPassword(et_username.getText().toString(), et_password.getText().toString());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("obj_user", obj_user);
        startActivity(intent);
        finish();
    }
}
