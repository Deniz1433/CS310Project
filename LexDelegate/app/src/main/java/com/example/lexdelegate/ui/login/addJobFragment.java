package com.example.lexdelegate.ui.login;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lexdelegate.JobItem;
import com.example.lexdelegate.JobType;
import com.example.lexdelegate.R;
import com.example.lexdelegate.databinding.FragmentAddJobBinding;
import com.example.lexdelegate.nameContract;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addJobFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentAddJobBinding binding;

    private String name;
    private String phone;

    private OkHttpClient client;

    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        bundle = new Bundle();
        binding = FragmentAddJobBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void create_job() {
        String compensation = binding.compensationAmount.getText().toString();
        String jobDetail = binding.jobDescription.getText().toString();
        int jobType = binding.JobDetailDropdown.getSelectedItemPosition(); // Ensure jobType is correctly set from the dropdown

        binding.loading.setVisibility(View.VISIBLE);

        try {
            String json = String.format(
                    "{\"username\":\"%s\",\"job_type\":\"%d\",\"job_detail\":\"%s\",\"fee\":\"%s\",\"contact\":\"%s\"}",
                    name, jobType, jobDetail, compensation, phone
            );

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            // Log the JSON payload for debugging
            Log.d("RegisterPayload", json);

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/createJob")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(() -> {
                        binding.loading.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Job could not be created", Toast.LENGTH_SHORT).show();
                        Log.e("error", "Request failed: ", e);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> {
                            binding.loading.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Job Successfully Created.", Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(requireView());
                            nameContract cred = new nameContract(name, phone);
                            bundle.putParcelable("creds", cred);
                            navController.navigate(R.id.action_createJob_to_jobListFragment, bundle);
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            binding.loading.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Job Cannot be Created.", Toast.LENGTH_SHORT).show();
                            Log.e("error", "Server error: " + response.code() + " " + response.message());
                        });
                    }
                }
            });
        } catch (Exception e) {
            binding.loading.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            Log.e("error", "Exception: ", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            nameContract x = getArguments().getParcelable("creds");
            name = x.name;
            phone = x.contact;
        }

        client = new OkHttpClient();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.createJob.setOnClickListener(v -> create_job());

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            nameContract cred = new nameContract(name, phone);
            bundle.putParcelable("creds", cred);
            Log.d("err", bundle.toString());
            navController.navigate(R.id.action_createJob_to_jobListFragment, bundle);
        });

        Spinner jobSpinner = view.findViewById(R.id.JobDetailDropdown);
        String[] des = {"Select type of the job", "Dilekçe", "Diğer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, des);
        jobSpinner.setAdapter(adapter);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText compensationEditText = binding.compensationAmount;
        final EditText jobDescriptionEditText = binding.jobDescription;
        final Button createJobButton = binding.createJob;
        final ProgressBar loadingProgressBar = binding.loading;

        // Observe form state
        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            createJobButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                compensationEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                jobDescriptionEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        // Text changed listeners
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(compensationEditText.getText().toString(),
                        jobDescriptionEditText.getText().toString());
            }
        };
        compensationEditText.addTextChangedListener(afterTextChangedListener);
        jobDescriptionEditText.addTextChangedListener(afterTextChangedListener);

        // Editor action listener
        jobDescriptionEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(compensationEditText.getText().toString(),
                        jobDescriptionEditText.getText().toString());
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
