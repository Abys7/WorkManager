package com.example.workmanaging.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workmanaging.R;
import com.example.workmanaging.view.adapter.UserActionAdapter;
import com.example.workmanaging.viewmodel.UserActionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private UserActionViewModel userActionViewModel;
    private static final String PREFS_NAME = "WorkManagingPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int userId = prefs.getInt(KEY_USER_ID, -1);

        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Profile Button Logic
        ImageButton btnProfile = findViewById(R.id.profile_icon);
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
        });

        RecyclerView recyclerView = findViewById(R.id.rv_recent_actions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        UserActionAdapter adapter = new UserActionAdapter();
        recyclerView.setAdapter(adapter);

        userActionViewModel = new ViewModelProvider(this).get(UserActionViewModel.class);
        userActionViewModel.getActionsForUser(userId).observe(this, actions -> {
            adapter.setActions(actions);
        });

        adapter.setOnItemClickListener(action -> {
            if ("CREATE_CLIENT".equals(action.actionType)) {
                Intent intent = new Intent(this, ClientDetailActivity.class);
                intent.putExtra("CLIENT_ID", action.referenceId);
                startActivity(intent);
            } else if ("CREATE_PROJECT".equals(action.actionType) || "OPEN_PROJECT".equals(action.actionType)) {
                Intent intent = new Intent(this, ProjectDetailActivity.class);
                intent.putExtra("PROJECT_ID", action.referenceId);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_clients) {
                startActivity(new Intent(getApplicationContext(), ClientsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_projects) {
                startActivity(new Intent(getApplicationContext(), ProjectsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}