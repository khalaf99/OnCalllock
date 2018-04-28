package com.example.khalaf.oncalllock;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OnCallReceiver extends BroadcastReceiver implements View.OnClickListener {

    private static final String TAG = "oncallreceiver";

    private static String prevstate = TelephonyManager.EXTRA_STATE_IDLE;

    private static View passwordview;
    private static WindowManager windowManager;
    private static EditText enterdpassword;
    private static TextView tvWrongPassword;
    private static SharedPreferences preferences;
    public static boolean iscalllocked;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "on Call Recevier Method");


        String Currentstate = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        // lw el current state ringing we elly 2bleha idle
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(Currentstate)
                && TelephonyManager.EXTRA_STATE_IDLE.equals(prevstate)) {

            prevstate = TelephonyManager.EXTRA_STATE_RINGING;
            showpasswordscreen(context);

        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(Currentstate)) {
            prevstate = TelephonyManager.EXTRA_STATE_IDLE;
            if (iscalllocked) {
                hidePasswordWindow();

            }
        }


    }
    private static String userpassword ;
    private void showpasswordscreen(Context context) {


        preferences = context.getSharedPreferences(MainActivity.PREF_NAME, 0);
        userpassword = preferences.getString(MainActivity.PASSWORD_KEY, null);
        if (userpassword != null) {
            Toast.makeText(context, "show password screen", Toast.LENGTH_SHORT).show();

            // lazem ntolob mn el user y act as adminstrator
            // permissions of adminstrator bytktep fel xml // el permission el 3adaya byyktep fel manifist 3ady
            lockDevice(context);
            ShowPasswordWindow(context);
        }

    }

    private void ShowPasswordWindow(Context context) {

        iscalllocked = true;
        // 3andk xml we 3awz t7oto fe view da by7aweloo
        // mn el a5er el xml 2t7well le view we 2t7at fe passwordview
        passwordview = LayoutInflater.from(context).inflate(R.layout.screenlayoutshape, null);

        tvWrongPassword = passwordview.findViewById(R.id.tv_wrong_password);
        enterdpassword = passwordview.findViewById(R.id.tvpassword);
        passwordview.findViewById(R.id.btn_number_1).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_2).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_3).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_4).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_5).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_6).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_7).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_8).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_number_9).setOnClickListener(this);
        passwordview.findViewById(R.id.btn_clear_all).setOnClickListener(this);
        passwordview.findViewById(R.id.iv_backspace).setOnClickListener(this);


        windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);

        // el 7agat elly hato7otha 3la el window
        //ya3nyyyyy mn el 2a5er lw el version beta3k 22l mn el orio 25taaar taype app overlay lw akber hay5taar el phone type
        // we use flag full screen 3shan el show when locked
        // pixel format da elly hya el colores
        // 2wel 7aga by5odha el wight wel hight
        // tany 7aga el tayep
        // talet 7aga el flags
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT
                , WindowManager.LayoutParams.MATCH_PARENT
                , Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
                | WindowManager.LayoutParams.TYPE_PHONE
                , WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                , PixelFormat.TRANSPARENT);

        // da by5aly ely wara el windoe zay maykoon lono dark keda matefy // 1 ya3ny zaher neeek 0 ya3ny msh zaher 5ales
        params.dimAmount = 0.5f;
        windowManager.addView(passwordview, params);


    }

    private void hidePasswordWindow() {
        iscalllocked = false;
        windowManager.removeViewImmediate(passwordview);
    }

    private void lockDevice(Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (policyManager.isAdminActive(new ComponentName(context, AdminPolicyReceiver.class))) {
            policyManager.lockNow();
        }
    }


    @Override
    public void onClick(View view) {
        tvWrongPassword.setVisibility(View.GONE);
        switch (view.getId()){
            case R.id.btn_number_0:
                enterdpassword.append("0");
                break;
            case R.id.btn_number_1:
                enterdpassword.append("1");
                break;
            case R.id.btn_number_2:
                enterdpassword.append("2");
                break;
            case R.id.btn_number_3:
                enterdpassword.append("3");
                break;
            case R.id.btn_number_4:
                enterdpassword.append("4");
                break;
            case R.id.btn_number_5:
                enterdpassword.append("5");
                break;
            case R.id.btn_number_6:
                enterdpassword.append("6");
                break;
            case R.id.btn_number_7:
                enterdpassword.append("7");
                break;
            case R.id.btn_number_8:
                enterdpassword.append("8");
                break;
            case R.id.btn_number_9:
                enterdpassword.append("9");
                break;
            case R.id.btn_clear_all:
                enterdpassword.setText("");
                break;
            case R.id.iv_backspace:
                String password = enterdpassword.getText().toString();
                if(password.length() > 0) {
                    password = password.substring(0, password.length() - 1);
                    enterdpassword.setText(password);
                }
                break;
        }
        checkPassword();
    }

    private void checkPassword() {
        String enteredPassword = enterdpassword.getText().toString();
        if(enteredPassword.length() == userpassword.length()){
            if(enteredPassword.equals(userpassword)){
                hidePasswordWindow();
            }else{
                tvWrongPassword.setVisibility(View.VISIBLE);
                enterdpassword.setText("");
            }
        }
    }
}


