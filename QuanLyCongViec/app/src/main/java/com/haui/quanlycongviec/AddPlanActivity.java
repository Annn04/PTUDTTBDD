package com.haui.quanlycongviec;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.haui.quanlycongviec.model.Account;
import com.haui.quanlycongviec.model.Plan;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class AddPlanActivity extends AppCompatActivity {
    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.edt_person_do)
    TextInputEditText edtPersonDo;
    @BindView(R.id.edt_person_create)
    TextInputEditText edtPersonCreate;
    @BindView(R.id.tv_date)
    TextView tvDate;
    Calendar date;
    @BindView(R.id.edt_name)
    TextInputEditText edtName;
    @BindView(R.id.edt_description)
    TextInputEditText edtDescription;
    String type = "";
    private String[] types = new String[]{"Ăn chơi", "Gia đình", "Bạn bè", "Công ty", "Cá nhân"};
    private ArrayAdapter<String> adapter;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        if (getIntent().getStringExtra("id") != null){
            loadData(getIntent().getStringExtra("id"));
        }
        init();
    }

    private void loadData(String id){
        Plan plan = realm.where(Plan.class).equalTo("id",id).findFirst();
        edtName.setText(plan.getTitle());
        edtDescription.setText(plan.getDescription());
        edtPersonCreate.setText(plan.getNamePersonCreate());
        edtPersonDo.setText(plan.getNamePersonDo());

        String type = plan.getType();
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)){
                spType.setSelection(i);
                return;
            }
        }
    }
    public void init() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, types);
        spType.setAdapter(adapter);
        date = Calendar.getInstance();
        tvDate.setText(getDate(date.getTimeInMillis()));

    }


    @OnClick({R.id.tv_date, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
                showDateTimePicker();
                break;
            case R.id.btn_save:
                if (edtName.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập tên công việc!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtDescription.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập mô tả công việc!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtPersonCreate.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập người tạo công việc!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtPersonDo.getText().toString().isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập người làm công việc!",Toast.LENGTH_SHORT).show();
                    return;
                }
                type = types[0];
                spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        type = types[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (getIntent().getStringExtra("id") != null) {
                    realm.beginTransaction();
                    Plan plan = realm.where(Plan.class).equalTo("id",getIntent().getStringExtra("id")).findFirst();

                    plan.setTitle(edtName.getText().toString());
                    plan.setDescription(edtDescription.getText().toString());
                    plan.setNamePersonCreate(edtPersonCreate.getText().toString());
                    plan.setType(type);
                    plan.setNamePersonDo(edtPersonDo.getText().toString());
                    realm.commitTransaction();

                }else {
                    Plan plan = new Plan();

                    plan.setTitle(edtName.getText().toString());
                    plan.setDescription(edtDescription.getText().toString());
                    plan.setNamePersonCreate(edtPersonCreate.getText().toString());
                    plan.setType(type);
                    plan.setNamePersonDo(edtPersonDo.getText().toString());
                    plan.setTime(date.getTimeInMillis());
                    plan.setId("plan"+date.getTimeInMillis());
                    addPlan(plan);
                }
                Toast.makeText(this,"Thêm công việc thành công!",Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        Context context = this;
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        tvDate.setText(getDate(date.getTimeInMillis()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public String getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy ");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void addPlan(Plan plan){
        realm.beginTransaction();
        realm.insert(plan);
        realm.commitTransaction();
    }

}
