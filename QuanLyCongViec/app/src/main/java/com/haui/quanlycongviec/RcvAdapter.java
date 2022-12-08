package com.haui.quanlycongviec;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haui.quanlycongviec.model.Plan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RcvAdapter extends RecyclerView.Adapter<RcvAdapter.ViewHolder> {
    private IOnLongClickItemListener onLongClickItemListener;
    private List<Plan> plans;

    public RcvAdapter(List<Plan> plans) {
        this.plans = plans;
    }


    public void updateData(List<Plan> plans){
        this.plans = plans;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_plan, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Plan plan = plans.get(i);
        viewHolder.tvTime.setText(getDate(plan.getTime()));
        viewHolder.tvTitle.setText(plan.getTitle());
        viewHolder.tvType.setText(String.format(viewHolder.itemView.getContext().getString(R.string.str_type),plan.getType()));
        viewHolder.tvDes.setText(String.format(viewHolder.itemView.getContext().getString(R.string.str_des),plan.getDescription()));
    }

    public void setOnLongClickItemListener(IOnLongClickItemListener onLongClickItemListener) {
        this.onLongClickItemListener = onLongClickItemListener;
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_type)
        TextView tvType;
        @BindView(R.id.tv_description)
        TextView tvDes;
        @BindView(R.id.cl_container)
        ConstraintLayout clContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickItemListener.onClickItem(clContainer,getAdapterPosition());
                    return false;
                }

            });

            itemView.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvType.getVisibility() == View.GONE){
                        tvType.setVisibility(View.VISIBLE);
                        tvDes.setVisibility(View.VISIBLE);
                    }else {
                        tvType.setVisibility(View.GONE);
                        tvDes.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public interface IOnLongClickItemListener {
        void onClickItem(ConstraintLayout view, int position);
    }


    public String getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
