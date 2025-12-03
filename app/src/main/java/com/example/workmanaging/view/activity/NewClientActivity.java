package com.example.workmanaging.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.Cliente;
import com.example.workmanaging.model.entity.UserAction;
import com.example.workmanaging.viewmodel.ClienteViewModel;
import com.example.workmanaging.viewmodel.UserActionViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class NewClientActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WorkManagingPrefs";
    private static final String KEY_USER_ID = "userId";

    private EditText etName, etCompany, etEmail, etPhone, etDesc;
    private ImageView ivClientImagePreview;
    private ClienteViewModel clienteViewModel;
    private UserActionViewModel userActionViewModel;

    private int editClientId = -1;
    private Cliente clientToEdit = null;
    private Uri newImageUri = null;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    newImageUri = uri;
                    ivClientImagePreview.setImageURI(newImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        etName = findViewById(R.id.et_client_name);
        etCompany = findViewById(R.id.et_client_company);
        etEmail = findViewById(R.id.et_client_email);
        etPhone = findViewById(R.id.et_client_phone);
        etDesc = findViewById(R.id.et_client_desc);
        ivClientImagePreview = findViewById(R.id.iv_client_image_preview);
        Button btnUploadImage = findViewById(R.id.btn_upload_image);
        Button btnSave = findViewById(R.id.btn_save);
        TextView tvTitle = findViewById(R.id.tv_new_client_title);

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        userActionViewModel = new ViewModelProvider(this).get(UserActionViewModel.class);

        editClientId = getIntent().getIntExtra("CLIENT_ID_EDIT", -1);

        if (editClientId != -1) {
            tvTitle.setText("Edit Client");
            btnSave.setText("Update");
            loadClientData();
        } else {
            ivClientImagePreview.setImageResource(R.drawable.ic_profile_placeholder);
        }

        btnUploadImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveClient());
    }

    private void loadClientData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) return;

        clienteViewModel.getClientsForUser(userId).observe(this, clients -> {
            if (clientToEdit == null) {
                for (Cliente c : clients) {
                    if (c.clienteId == editClientId) {
                        clientToEdit = c;
                        etName.setText(c.nome);
                        etCompany.setText(c.azienda);
                        etEmail.setText(c.email);
                        etPhone.setText(c.telefono);
                        etDesc.setText(c.descrizione);
                        if (c.img != null && !c.img.isEmpty()) {
                            ivClientImagePreview.setImageURI(Uri.parse(c.img));
                        } else {
                            ivClientImagePreview.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                        break;
                    }
                }
            }
        });
    }

    private void saveClient() {
        String name = etName.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int userId = prefs.getInt(KEY_USER_ID, -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String imagePath = null;
        if (newImageUri != null) {
            imagePath = saveImageToInternalStorage(newImageUri);
        } else if (clientToEdit != null) {
            imagePath = clientToEdit.img; // Keep old image if not changed
        }

        if (editClientId != -1 && clientToEdit != null) {
            clientToEdit.nome = name;
            clientToEdit.azienda = company;
            clientToEdit.email = email;
            clientToEdit.telefono = phone;
            clientToEdit.descrizione = desc;
            clientToEdit.img = imagePath;

            clienteViewModel.update(clientToEdit);

            UserAction action = new UserAction();
            action.userId = userId;
            action.actionType = "UPDATE_CLIENT";
            action.referenceId = clientToEdit.clienteId;
            action.timestamp = new Date();
            action.title = "Updated Client: " + name;
            action.subtitle = company != null ? company : "";
            userActionViewModel.insert(action);

            Toast.makeText(this, "Client updated", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Cliente newClient = new Cliente();
            newClient.userId = userId;
            newClient.nome = name;
            newClient.azienda = company;
            newClient.email = email;
            newClient.telefono = phone;
            newClient.descrizione = desc;
            newClient.img = imagePath;

            clienteViewModel.insert(newClient, id -> {
                UserAction action = new UserAction();
                action.userId = userId;
                action.actionType = "CREATE_CLIENT";
                action.referenceId = (int) id;
                action.timestamp = new Date();
                action.title = "New Client: " + name;
                action.subtitle = company != null ? company : "";

                userActionViewModel.insert(action);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Client saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewClientActivity.this, ClientDetailActivity.class);
                    intent.putExtra("CLIENT_ID", (int) id);
                    startActivity(intent);
                    finish();
                });
            });
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            // Generate a unique filename to avoid conflicts
            String fileName = "client_" + UUID.randomUUID().toString() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // When editing, delete old image file if it exists and is different
            if (clientToEdit != null && clientToEdit.img != null && !clientToEdit.img.isEmpty()) {
                try {
                    File oldFile = new File(Uri.parse(clientToEdit.img).getPath());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return Uri.fromFile(file).toString();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
