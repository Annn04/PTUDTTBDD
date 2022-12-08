package com.haui.quanlycongviec;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.haui.quanlycongviec.model.Account;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.edt_phone)
    TextInputEditText edtPhone;
    @BindView(R.id.edt_password)
    TextInputEditText edtPassword;


    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (!edtPhone.getText().toString().isEmpty() && edtPhone.getText() != null && !edtPassword.getText().toString().isEmpty() && edtPassword.getText() != null) {
                    if (checkAccount(edtPhone.getText().toString(),edtPassword.getText().toString())) {
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        Toast.makeText(this, "Sđt hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Vui lòng nhập sđt và mật khẩu!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
    }

    private boolean checkAccount(String phone, String password) {
        Account account = realm.where(Account.class).equalTo("phone", phone).and().equalTo("password", password).findFirst();
        if (account != null) {
            return true;
        } else {
            return false;
        }
    }
}
