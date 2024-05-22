package com.example.lexdelegate.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lexdelegate.JobItem;
import com.example.lexdelegate.MyJobRecyclerViewAdapter;
import com.example.lexdelegate.R;
import com.example.lexdelegate.databinding.FragmentJobDetailBinding;
import com.example.lexdelegate.nameContract;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class jobDetailFragment extends Fragment {

    private FragmentJobDetailBinding binding;

    private JobItem jobItem;

    private String name;
    private String phone;

    private Bundle bundle;

    private nameContract x;

    private nameContract nc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        bundle = new Bundle();

        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);


        if (getArguments() != null) {
            jobItem = getArguments().getParcelable("job");

            nc = getArguments().getParcelable("creds");

            name = nc.name;
            phone = nc.contact;
        }

        if (jobItem != null) {
            TextView jobCommissionerTextView = view.findViewById(R.id.username);
            TextView jobTypeTextView = view.findViewById(R.id.jobType);
            TextView jobDetailTextView = view.findViewById(R.id.jobDetail);
            TextView feeTextView = view.findViewById(R.id.fee);
            TextView contactTextView = view.findViewById(R.id.contact);
            TextView cityTextView = view.findViewById(R.id.city);

            jobCommissionerTextView.setText(jobItem.username);
            jobTypeTextView.setText(jobItem.jobType);
            jobDetailTextView.setText(jobItem.jobDetail);
            feeTextView.setText(jobItem.fee);
            contactTextView.setText(jobItem.contact);
            cityTextView.setText(jobItem.city);
        } else {
            Toast.makeText(getActivity(), "Job details not available", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up back navigation for the purple arrow
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            nameContract cred = new nameContract(name, phone);
            bundle.putParcelable("creds", cred);
            navController.navigate(R.id.action_jobDetailFragment_to_jobListFragment, bundle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
