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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.edt_phone)
    TextInputEditText edtPhone;
    @BindView(R.id.edt_password)
    TextInputEditText edtPassword;
    @BindView(R.id.edt_confirm)
    TextInputEditText edtConfirm;

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (edtPhone.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập sđt!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập mật khẩu!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtConfirm.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập lại mật khẩu!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!edtPassword.getText().toString().equals(edtConfirm.getText().toString())){
                    Toast.makeText(this,"Mật khẩu nhập lại chưa chính xác!",Toast.LENGTH_SHORT).show();
                    return;
                }

                createAccount(edtPhone.getText().toString(), edtPassword.getText().toString());
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.tv_login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

        }
    }

    private void createAccount(String phone, String password) {
        realm.beginTransaction();
        Account account = new Account();
        account.setPhone(phone);
        account.setPassword(password);
        realm.insert(account);
        realm.commitTransaction();
    }

}
