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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.example.lexdelegate.databinding.FragmentSignupBinding;
import com.example.lexdelegate.R;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupFragment extends Fragment {

    private OkHttpClient client = new OkHttpClient();
    private LoginViewModel loginViewModel;
    private FragmentSignupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText nameEditText = binding.name;
        final EditText surnameEditText = binding.surname;
        final EditText phoneEditText = binding.editTextPhone;
        final EditText emailEditText = binding.editTextTextEmailAddress;
        final EditText passwordEditText = binding.editTextTextPassword;
        final EditText passwordAgainEditText = binding.editTextTextPasswordAgain;
        final EditText barNumberEditText = binding.editTextText;
        final Spinner cityDropdown = binding.CityDropdown;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;

        binding.register.setOnClickListener(v -> register());

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                registerButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    nameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
            }
        });

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
                loginViewModel.loginDataChanged(
                        nameEditText.getText().toString(),
                        passwordEditText.getText().toString()
                );
            }
        };

        nameEditText.addTextChangedListener(afterTextChangedListener);
        surnameEditText.addTextChangedListener(afterTextChangedListener);
        phoneEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordAgainEditText.addTextChangedListener(afterTextChangedListener);
        barNumberEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(
                            nameEditText.getText().toString(),
                            passwordEditText.getText().toString()
                    );
                }
                return false;
            }
        });

        registerButton.setOnClickListener(v -> register());

        // Set up back navigation for the purple arrow
        ImageView backButton = view.findViewById(R.id.imageView);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_signUpFragment_to_mainFragment);
        });

        // Set up the spinner with cities in Turkey
        Spinner citySpinner = view.findViewById(R.id.CityDropdown);
        String[] cities = {"Select city of the bar",
                "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Antalya",
                "Ardahan", "Artvin", "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt", "Bilecik",
                "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum",
                "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan", "Erzurum",
                "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır",
                "Isparta", "İstanbul", "İstanbul-2", "İzmir", "Kahramanmaraş", "Karabük", "Karaman",
                "Kars", "Kastamonu", "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir", "Kilis",
                "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Mardin", "Mersin", "Muğla",
                "Muş", "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize", "Sakarya", "Samsun",
                "Siirt", "Sinop", "Sivas", "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon",
                "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak", "Ankara", "Ankara-2"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, cities);
        citySpinner.setAdapter(adapter);
    }

    private void register() {
        binding.loading.setVisibility(View.VISIBLE);

        try {
            // Get values from the input fields
            String name = Objects.requireNonNull(binding.name.getText()).toString();
            String surname = Objects.requireNonNull(binding.surname.getText()).toString();
            String birthday = Objects.requireNonNull(binding.editTextDate.getText()).toString();
            String email = Objects.requireNonNull(binding.editTextTextEmailAddress.getText()).toString();
            String password = Objects.requireNonNull(binding.editTextTextPassword.getText()).toString();
            String phone = Objects.requireNonNull(binding.editTextPhone.getText()).toString();
            String barCity = binding.CityDropdown.getSelectedItem().toString();
            int barNumber = Integer.parseInt(Objects.requireNonNull(binding.editTextText.getText()).toString());

            // Create JSON object
            String json = String.format(
                    "{\"username\":\"%s %s\",\"birthday\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"phone\":\"%s\",\"bar_city\":\"%s\",\"bar_id\":%d}",
                    name, surname, birthday, email, password, phone, barCity, barNumber
            );

            // Log the JSON payload for debugging
            Log.d("RegisterPayload", json);

            // Use 10.0.2.2 to connect to localhost on the host machine
            String url = "http://10.0.2.2:8080/api/createUser";

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(() -> {
                        binding.loading.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Cannot Connect to Back-end", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getActivity().runOnUiThread(() -> {
                        binding.loading.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "User Successfully Created", Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigate(R.id.action_signUpFragment_to_mainFragment);
                        } else {
                            Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            binding.loading.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
