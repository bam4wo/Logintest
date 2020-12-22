package com.bam.loginn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SignUp extends AppCompatActivity {

    TextInputEditText textInputEditTextEmployeeId, textInputEditTextPassword, textInputEditTextPassword2, textInputEditTextEmail;
    Button buttonSignUp;
    TextView textViewLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextEmployeeId = findViewById(R.id.employeeId);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextPassword2 = findViewById(R.id.password2);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);
        //隱藏密碼
        textInputEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        textInputEditTextPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());

        handleSSLHandshake(); //呼叫忽略https的證書校驗方法

        CheckBox passcheck = findViewById(R.id.passcheck);
        passcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //顯示密碼
                    textInputEditTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隱藏密碼
                    textInputEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        CheckBox passcheck2 = findViewById(R.id.passcheck2);
        passcheck2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //顯示密碼
                    textInputEditTextPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //隱藏密碼
                    textInputEditTextPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        //點此登入
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String employee_id, email, password, password2;
                employee_id = String.valueOf(textInputEditTextEmployeeId.getText());
                email = String.valueOf(textInputEditTextEmail.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
                password2 = String.valueOf(textInputEditTextPassword2.getText());

                if (!employee_id.equals("") && !email.equals("") && !password.equals("") && !password2.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[3];
                            field[0] = "employee_id";
                            field[1] = "email";
                            field[2] = "password";
                            //Creating array for data
                            String[] data = new String[4];
                            data[0] = employee_id;
                            data[1] = email;
                            data[2] = password;
                            data[3] = password2;
                            //判斷密碼跟確認密碼是否一致(絹)
                            while (!data[2].equals(data[3])) {
                                textInputEditTextPassword2.setText("");
                                field[2] = "";
                                field[3] = "";
                                Toast.makeText(SignUp.this,"密碼不一致！",Toast.LENGTH_LONG).show();
                                break;
                            }
                            PutData putData = new PutData("https://192.168.1.109/Hospital/signup.php", "POST", field, data); //網址要改成自己的php檔位置及自己的ip
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.equals("Sign Up Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"All fields require", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //忽略https的證書校驗
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的證書
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }

    }
}