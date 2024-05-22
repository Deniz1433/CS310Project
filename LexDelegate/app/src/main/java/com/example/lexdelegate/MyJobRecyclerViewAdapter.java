package com.example.lexdelegate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyJobRecyclerViewAdapter extends RecyclerView.Adapter<MyJobRecyclerViewAdapter.ViewHolder> {

    private final List<JobItem> jobItems;
    private final nameContract nc;
    private final Bundle bundle;

    public MyJobRecyclerViewAdapter(List<JobItem> items, nameContract nc) {
        jobItems = items;
        this.nc = nc;
        this.bundle = new Bundle();  // Initialize bundle with nameContract
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_jobs_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        JobItem jobItem = jobItems.get(position);
        holder.jobType.setText(jobItem.jobType);
        holder.fee.setText(jobItem.fee);
        holder.username.setText(jobItem.username);

        holder.detailButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(holder.itemView);
            bundle.putParcelable("creds", nc);
            bundle.putParcelable("job", jobItem);
            navController.navigate(R.id.action_jobListFragment_to_jobDetailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return jobItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView jobType;
        public final TextView fee;
        public final TextView username;
        public final Button detailButton;

        public ViewHolder(View view) {
            super(view);
            jobType = view.findViewById(R.id.jobType);
            fee = view.findViewById(R.id.fee);
            username = view.findViewById(R.id.username);
            detailButton = view.findViewById(R.id.detailButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + fee.getText() + "'";
        }
    }
}
