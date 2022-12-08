package com.haui.quanlycongviec;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.haui.quanlycongviec.model.Plan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RcvAdapter.IOnLongClickItemListener {
    @BindView(R.id.rv_plans)
    RecyclerView rvPlans;
    @BindView(R.id.btn_add)
    ImageView btnAdd;
    @BindView(R.id.tv_date)
    TextView tvDate;
    private RcvAdapter adapter;
    private List<Plan> plans = new ArrayList<>();
    private Realm realm;
    private long date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        btnAdd.setOnClickListener(this);
        date = Calendar.getInstance().getTimeInMillis();
        plans = loadData(date);
        adapter = new RcvAdapter(plans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter.setOnLongClickItemListener(this);
        rvPlans.setLayoutManager(layoutManager);
        rvPlans.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(this, AddPlanActivity.class), 14);
    }

    @Override
    public void onClickItem(ConstraintLayout view, int position) {
        PopupMenu popup = new PopupMenu(MainActivity.this,view);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete:
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Plan> result = realm.where(Plan.class).equalTo("id",plans.get(position).getId()).findAll();
                                result.deleteAllFromRealm();
                            }
                        });
                        plans.remove(position);
                        adapter.updateData(plans);
                        break;
                    case R.id.action_edit:
                        Intent intent = new Intent(MainActivity.this,AddPlanActivity.class);
                        intent.putExtra("id",plans.get(position).getId());
                        startActivityForResult(intent,14);
                        break;
                }
                return true;
            }
        });

        popup.show();

    }

    private List<Plan> loadData(long date) {
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();

        List<Plan> plans = new ArrayList<>();
        calStart.setTime(new Date(date)); // compute start of the day for the timestamp
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);

        calEnd.setTime(new Date(date)); // compute end of the day for the timestamp
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        RealmResults<Plan> realmList = realm.where(Plan.class).lessThan("time", calEnd.getTimeInMillis()).greaterThan("time", calStart.getTimeInMillis()).findAll();

        plans.addAll(realmList);
        return plans;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 14) {
            plans = loadData(date);
            adapter.updateData(plans);
        }
    }

    @OnClick(R.id.tv_date)
    public void onViewClicked() {
        final Calendar currentDate = Calendar.getInstance();
        Calendar dateT;
        dateT = Calendar.getInstance();
        Context context = this;
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateT.set(year, monthOfYear, dayOfMonth);
                tvDate.setText(getDate(dateT.getTimeInMillis()));
                plans = loadData(dateT.getTimeInMillis());
                adapter.updateData(plans);
                date = dateT.getTimeInMillis();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
