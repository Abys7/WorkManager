package com.example.workmanaging.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.User;
import com.example.workmanaging.viewmodel.UserViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AccountActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private User currentUser;
    private static final String PREFS_NAME = "WorkManagingPrefs";
    private static final String KEY_USER_ID = "userId";
    private ImageView ivProfileImage;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    saveImageToInternalStorage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ImageButton btnBackTop = findViewById(R.id.btn_back_top);
        FrameLayout btnBackBottom = findViewById(R.id.btn_back_bottom);
        TextView tvUsername = findViewById(R.id.tv_username);
        TextView tvEmail = findViewById(R.id.tv_email);
        TextView tvProfession = findViewById(R.id.tv_profession);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        View profileContainer = findViewById(R.id.profile_image_container);

        btnBackTop.setOnClickListener(v -> finish());
        btnBackBottom.setOnClickListener(v -> finish());

        profileContainer.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int userId = prefs.getInt(KEY_USER_ID, -1);

        if (userId != -1) {
            userViewModel.getUserById(userId).observe(this, user -> {
                if (user != null) {
                    currentUser = user;
                    tvUsername.setText(user.nomeUtente);
                    tvEmail.setText(user.email);
                    tvProfession.setText(user.professione != null && !user.professione.isEmpty() ? user.professione : "Not specified");
                    
                    if (user.img != null && !user.img.isEmpty()) {
                        try {
                            ivProfileImage.setImageURI(Uri.parse(user.img));
                        } catch (Exception e) {
                            ivProfileImage.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                    } else {
                        ivProfileImage.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                }
            });
        }

        tvProfession.setOnClickListener(v -> {
            if (currentUser != null) {
                showProfessionMenu(tvProfession);
            }
        });
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_" + currentUser.userId + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();
            
            String path = Uri.fromFile(file).toString();
            currentUser.img = path;
            userViewModel.update(currentUser);
            
            ivProfileImage.setImageURI(Uri.parse(path));
            Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProfessionMenu(TextView anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.profession_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            String profession = item.getTitle().toString();
            if (currentUser != null) {
                currentUser.professione = profession;
                userViewModel.update(currentUser);
            }
            return true;
        });

        popup.show();
    }
}
