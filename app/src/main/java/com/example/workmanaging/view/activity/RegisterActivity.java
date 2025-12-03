package com.example.workmanaging.view.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.User;
import com.example.workmanaging.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etUsername, etPassword, etConfirmPassword;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLoginLink.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.getUserByUsername(username).observe(this, existingUser -> {
            if (existingUser != null) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            } else {
                userViewModel.getUserByEmail(email).observe(this, existingEmailUser -> {
                    if (existingEmailUser != null) {
                        Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        registerUser(email, username, password);
                    }
                });
            }
        });
    }

    private void registerUser(String email, String username, String password) {
        User newUser = new User();
        newUser.email = email;
        newUser.nomeUtente = username;
        newUser.password = password;

        userViewModel.insert(newUser);
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        finish();
    }
}
