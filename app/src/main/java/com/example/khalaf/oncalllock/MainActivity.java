package com.example.khalaf.oncalllock;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int read_phone_rquest_code = 1;
    public static final int Draw_over = 2;
    private static final String TAG = "MAinActivity";

    // intialization
    // object of shared prefrence is to get 7aga
    private SharedPreferences prefrences;
    //to set values in shared prefrences
    private SharedPreferences.Editor editor;

    public static final String PREF_NAME = "OnCallPref";
    public static final String PASSWORD_KEY = "Password";


    private Button ChangePass, Save, Exit;
    private EditText SetPass, etOldpassword;
    private LinearLayout linearpassword;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // methods of views
        initview();

        // String 2sm el file we lw mal2oosh ha create it // mode private constant or put 0
        prefrences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        editor = prefrences.edit();


        CheckreadPhonePermission();
        // lw mala2ash el paswword hayb3tly null
        String Password = prefrences.getString(PASSWORD_KEY, null);

        if (Password != null) {
            ChangePass.setVisibility(View.VISIBLE);
            etOldpassword.setVisibility(View.VISIBLE);
            linearpassword.setVisibility(View.GONE);
        } else {
            ChangePass.setVisibility(View.GONE);
            etOldpassword.setVisibility(View.GONE);
            linearpassword.setVisibility(View.VISIBLE);

        }
    }

    private void initview() {

        ChangePass = (Button) findViewById(R.id.changepassword);
        Save = (Button) findViewById(R.id.savepassword);
        Exit = (Button) findViewById(R.id.exit);
        etOldpassword = (EditText) findViewById(R.id.oldpassword);

        SetPass = (EditText) findViewById(R.id.Password);

        linearpassword = (LinearLayout) findViewById(R.id.linear);

        ChangePass.setOnClickListener(this);
        Save.setOnClickListener(this);
        Exit.setOnClickListener(this);

    }

    // onclick of button change password
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.changepassword) {
            String oldpassword = prefrences.getString(PASSWORD_KEY, null);
            String password = etOldpassword.getText().toString();

            if (password.equals(oldpassword)) {
                etOldpassword.setText("");
                savePassword();
                Toast.makeText(this, "new password is saved", Toast.LENGTH_LONG).show();
            } else {
                etOldpassword.setError("Password doesn't match");
            }
        } else if (view.getId() == R.id.savepassword) {
            savePassword();
        } else if (view.getId() == R.id.exit) {
            finish();
        }


    }

    private void savePassword() {
        String Pass = SetPass.getText().toString();
        if (Pass.length() > 2) {
            // save paswword in shared prefrence
            editor.putString(PASSWORD_KEY, Pass);
            editor.apply();
            linearpassword.setVisibility(View.GONE);
            ChangePass.setVisibility(View.VISIBLE);
            etOldpassword.setVisibility(View.VISIBLE);

        } else {
            SetPass.setError("password lenght must be > 2");
        }
    }


///////////////////////////////////////////////<<<<<permisions and answers>>>>>>>////////////////////////////////////////////////

    // showing permission to user phonecall permission
    private void CheckreadPhonePermission() {

        // ana raye7 23mell el permission
        Log.d(TAG, "In check phone permission method");

        //context m3anaaah meeen elly 3awz y3ref el check dah// this is the curret ob that invoke the method
        //permission
        // ??????????????????????????????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {

            Log.d(TAG, "request read phone state permission");
            // requestpermision is method to check result
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, read_phone_rquest_code);

        } else {
            checkAdminPolicy();
            checkDrawoverAppspermission();
        }
    }

    // method elly betrag3ly el result of request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "in requset permission result method");
        if (requestCode == read_phone_rquest_code) {
            // if waf2 3la el permission
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "User Accepted read phone state permission");
                checkAdminPolicy();
                checkDrawoverAppspermission();

            }
            // if mawaf2sh ebn el wes5a ela el permission
            else {
                Log.d(TAG, "User Denied permision read phone state");
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // for showing permission of lockc phone to user and wait for result must user act as admin this fun make sure of that
    private void checkAdminPolicy() {
        // componentname howa elly mas2ool 3n permission
        // polcy manager de 7aga mawgoda fel os beta3ana
        ComponentName componentName = new ComponentName(this, AdminPolicyReceiver.class);
        DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!policyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "On Call App Will Lock Phone in Incoming Calls");
            startActivityForResult(intent, 1);
        }
    }


    // bet5aly el window over all apps
    private void checkDrawoverAppspermission() {

        // lw akber mn el marshmilo lazem ta5od 2zn el user eno hayzher fo2 kol el apps
        // ya3ny lazem el sdk 2kber mn el marshmelo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // lw mawaf2sh 3la el window yezher foo2 kol el apps
            if (!Settings.canDrawOverlays(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("Warrining")
                        .setCancelable(false)
                        .setMessage("you should accept this permission")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            //press ok button of wearning alert
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, Draw_over);
                            }
                        }).show();

            }

        }
    }

    // if click ok after showing the wearning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Draw_over) {
            if (resultCode != RESULT_OK) {

                checkDrawoverAppspermission();
            }
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}