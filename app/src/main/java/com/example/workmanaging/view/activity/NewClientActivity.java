package com.example.workmanaging.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.Cliente;
import com.example.workmanaging.model.entity.UserAction;
import com.example.workmanaging.viewmodel.ClienteViewModel;
import com.example.workmanaging.viewmodel.UserActionViewModel;
import java.util.Date;

public class NewClientActivity extends AppCompatActivity {

    private EditText etName, etCompany, etEmail, etPhone, etDesc;
    private ClienteViewModel clienteViewModel;
    private UserActionViewModel userActionViewModel;
    private static final String PREFS_NAME = "WorkManagingPrefs";
    private static final String KEY_USER_ID = "userId";
    
    private int editClientId = -1;
    private Cliente clientToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        etName = findViewById(R.id.et_client_name);
        etCompany = findViewById(R.id.et_client_company);
        etEmail = findViewById(R.id.et_client_email);
        etPhone = findViewById(R.id.et_client_phone);
        etDesc = findViewById(R.id.et_client_desc);
        Button btnSave = findViewById(R.id.btn_save);
        TextView tvTitle = findViewById(R.id.tv_new_client_title);

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        userActionViewModel = new ViewModelProvider(this).get(UserActionViewModel.class);
        
        editClientId = getIntent().getIntExtra("CLIENT_ID_EDIT", -1);
        
        if (editClientId != -1) {
            tvTitle.setText("Edit Client");
            btnSave.setText("Update");
            loadClientData();
        }

        btnSave.setOnClickListener(v -> saveClient());
    }
    
    private void loadClientData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        if (userId == -1) return;

        // In a real app, we should have getClientById. Here we filter from list for simplicity as in DetailActivity
        clienteViewModel.getClientsForUser(userId).observe(this, clients -> {
            if (clientToEdit == null) { // Load only once
                for (Cliente c : clients) {
                    if (c.clienteId == editClientId) {
                        clientToEdit = c;
                        etName.setText(c.nome);
                        etCompany.setText(c.azienda);
                        etEmail.setText(c.email);
                        etPhone.setText(c.telefono);
                        etDesc.setText(c.descrizione);
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

        if (editClientId != -1 && clientToEdit != null) {
            // Update existing
            clientToEdit.nome = name;
            clientToEdit.azienda = company;
            clientToEdit.email = email;
            clientToEdit.telefono = phone;
            clientToEdit.descrizione = desc;
            
            clienteViewModel.update(clientToEdit); // Assuming update method exists
            
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
            // Insert new
            Cliente newClient = new Cliente();
            newClient.userId = userId;
            newClient.nome = name;
            newClient.azienda = company;
            newClient.email = email;
            newClient.telefono = phone;
            newClient.descrizione = desc;

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
                    finish();
                });
            });
        }
    }
}