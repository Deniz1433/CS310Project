package com.example.lexdelegate.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lexdelegate.JobItem;
import com.example.lexdelegate.MyJobRecyclerViewAdapter;
import com.example.lexdelegate.R;
import com.example.lexdelegate.nameContract;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.lexdelegate.JobType;

public class JobsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OkHttpClient client;
    private RecyclerView recyclerView;
    private MyJobRecyclerViewAdapter adapter;
    private List<JobItem> jobItems;
    private String username;
    private String contact;
    private Bundle b;

    public JobsFragment() {
        // Mandatory empty constructor
    }

    public static JobsFragment newInstance(int columnCount) {
        JobsFragment fragment = new JobsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            nameContract x = getArguments().getParcelable("creds");
            if (x != null) {
                username = x.name;
                contact = x.contact;
            }
        }

        client = new OkHttpClient();
        jobItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = new Bundle();

        View view = inflater.inflate(R.layout.fragment_jobs_list, container, false);

        // Set the welcome message
        TextView welcomeTextView = view.findViewById(R.id.welcome_message);
        welcomeTextView.setText("Welcome, " + username);

        // Set the adapter
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.recyclerView);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        nameContract cred = new nameContract(username, contact);
        adapter = new MyJobRecyclerViewAdapter(jobItems, cred);
        recyclerView.setAdapter(adapter);

        fetchJobs();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button create = view.findViewById(R.id.jobCreate);
        create.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            nameContract cred = new nameContract(username, contact);
            b.putParcelable("creds", cred);
            Log.d("err", b.toString());
            navController.navigate(R.id.action_jobListFragment_to_jobCreate, b);
        });
    }

    private void fetchJobs() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/getJobs")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Failed to fetch jobs", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        jobItems.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            JobItem jobItem = new JobItem(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("username"),
                                    JobType.fromInt(jsonObject.getInt("job_type")).toString(),
                                    jsonObject.getString("job_detail"),
                                    jsonObject.getString("fee"),
                                    jsonObject.getString("contact"),
                                    jsonObject.getString("city")
                            );
                            jobItems.add(jobItem);
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                        }
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "Failed to parse jobs", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
}
