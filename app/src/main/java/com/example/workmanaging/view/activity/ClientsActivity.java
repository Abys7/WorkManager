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
import com.example.workmanaging.view.adapter.ClientAdapter;
import com.example.workmanaging.viewmodel.ClienteViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClientsActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private static final String PREFS_NAME = "WorkManagingPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clients);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
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

        // Gestione Profile Icon
        ImageButton btnProfile = findViewById(R.id.profile_icon);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, AccountActivity.class));
            });
        }

        RecyclerView recyclerView = findViewById(R.id.rv_clients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ClientAdapter adapter = new ClientAdapter();
        recyclerView.setAdapter(adapter);

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        clienteViewModel.getClientsForUser(userId).observe(this, clients -> {
            adapter.setClients(clients);
        });

        adapter.setOnItemClickListener(client -> {
            Intent intent = new Intent(ClientsActivity.this, ClientDetailActivity.class);
            intent.putExtra("CLIENT_ID", client.clienteId);
            startActivity(intent);
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_client);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ClientsActivity.this, NewClientActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_clients);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_clients) {
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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