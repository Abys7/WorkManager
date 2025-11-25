package com.example.workmanaging.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.workmanaging.R;
import com.example.workmanaging.view.fragment.ClientsFragment;
import com.example.workmanaging.view.fragment.HomeFragment;
import com.example.workmanaging.view.fragment.ProjectsFragment;
import com.example.workmanaging.view.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout homeContent;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Bottom navigation padding handled automatically usually or specific fix
            return insets;
        });

        homeContent = findViewById(R.id.home_content);
        fragmentContainer = findViewById(R.id.fragment_container);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Initialize HomeFragment in homeContent container if you want to treat Home as a fragment too, 
        // OR keep it as a View. The current layout has a LinearLayout for Home content.
        // To be consistent with MVVM and Fragment navigation, it's better to use HomeFragment.
        // However, your layout has a specific LinearLayout for Home.
        // I will follow the logic: Home -> View.VISIBLE, Others -> Fragment Container.
        // BUT, cleaner approach: Use HomeFragment inside fragment_container as well.
        
        // Let's stick to the layout provided:
        // If Home is selected, show home_content, hide fragment_container.
        // If others selected, hide home_content, show fragment_container and load fragment.

        // First load:
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
            showHome();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                showHome();
                return true;
            } else {
                Fragment selectedFragment = null;
                if (itemId == R.id.navigation_projects) {
                    selectedFragment = new ProjectsFragment();
                } else if (itemId == R.id.navigation_clients) {
                    selectedFragment = new ClientsFragment();
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    showFragment(selectedFragment);
                }
                return true;
            }
        });
    }

    private void showHome() {
        // Since we have a HomeFragment class now, we should probably use it.
        // But the layout has a dedicated "home_content" view.
        // Option A: Use the "home_content" view as defined in layout (simple TextView).
        // Option B: Put HomeFragment into "fragment_container" and hide "home_content".
        
        // Given you asked to connect to layouts already created and we have `fragment_home.xml` and `HomeFragment.java`,
        // we should use HomeFragment. 
        
        homeContent.setVisibility(View.GONE); // Hide the static layout
        fragmentContainer.setVisibility(View.VISIBLE);
        
        loadFragment(new HomeFragment());
    }

    private void showFragment(Fragment fragment) {
        homeContent.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        loadFragment(fragment);
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
