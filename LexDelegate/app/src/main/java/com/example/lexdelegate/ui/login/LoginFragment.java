package com.example.lexdelegate.ui.login;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.lexdelegate.databinding.FragmentLoginBinding;
import com.example.lexdelegate.R;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.lexdelegate.nameContract;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private OkHttpClient client;
    private static final String KEYSTORE_ALIAS = "jwtTokenAlias";

    private Bundle b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        b = new Bundle();
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        client = new OkHttpClient();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.register.setOnClickListener(v -> login());

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
                // Check if both fields are filled
                boolean isReady = binding.name.getText().toString().trim().length() > 0 &&
                        binding.surname.getText().toString().trim().length() > 0;
                binding.register.setEnabled(isReady);
            }
        };

        binding.name.addTextChangedListener(afterTextChangedListener);
        binding.surname.addTextChangedListener(afterTextChangedListener);
        binding.surname.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });

        // Set up back navigation for the purple arrow
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_signInFragment_to_mainFragment);
        });
    }

    private void login() {
        String username = binding.name.getText().toString();
        String password = binding.surname.getText().toString();
        binding.loading.setVisibility(View.VISIBLE);

        try {
            String json = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\"}",
                    username, password
            );

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            // Log the JSON payload for debugging
            Log.d("RegisterPayload", json);

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/api/loginUser")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(() -> {
                        binding.loading.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                        Log.e("error", String.valueOf(e));
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        String token = jsonObject.get("token").getAsString();
                        String name = jsonObject.get("username").getAsString();
                        String phone = jsonObject.get("contact").getAsString();
                        storeToken(token);
                        getActivity().runOnUiThread(() -> {
                            binding.loading.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(requireView());
                            nameContract cred = new nameContract(name, phone);
                            b.putParcelable("creds", cred);
                            navController.navigate(R.id.action_signInFragment_to_jobListFragment, b);
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            binding.loading.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                            Log.e("error", String.valueOf(response));
                        });
                    }
                }
            });
        } catch (Exception e) {
            binding.loading.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            Log.e("error", String.valueOf(e));
        }
    }

    private void storeToken(String token) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .build());
                keyGenerator.generateKey();
            }

            SecretKey secretKey = ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null)).getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] iv = cipher.getIV();
            byte[] encryption = cipher.doFinal(token.getBytes("UTF-8"));
            String encryptedToken = Base64.encodeToString(encryption, Base64.DEFAULT);
            // Store `encryptedToken` securely in shared preferences or another secure place
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
