package com.example.workmanaging.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.Cliente;
import com.example.workmanaging.viewmodel.ClienteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClientDetailActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private Cliente currentClient;
    private int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        clientId = getIntent().getIntExtra("CLIENT_ID", -1);
        if (clientId == -1) {
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tv_client_name);
        TextView tvCompany = findViewById(R.id.tv_client_company);
        TextView tvEmail = findViewById(R.id.tv_client_email);
        TextView tvPhone = findViewById(R.id.tv_client_phone);
        TextView tvDesc = findViewById(R.id.tv_client_desc);
        FloatingActionButton btnDelete = findViewById(R.id.btn_delete);
        FloatingActionButton btnEdit = findViewById(R.id.btn_edit);

        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        int userId = getSharedPreferences("WorkManagingPrefs", 0).getInt("userId", -1);
        
        clienteViewModel.getClientsForUser(userId).observe(this, clients -> {
            for (Cliente c : clients) {
                if (c.clienteId == clientId) {
                    currentClient = c;
                    tvName.setText(c.nome);
                    tvCompany.setText(c.azienda);
                    tvEmail.setText(c.email);
                    tvPhone.setText(c.telefono);
                    tvDesc.setText(c.descrizione);
                    break;
                }
            }
        });

        btnEdit.setOnClickListener(v -> {
            if (currentClient != null) {
                Intent intent = new Intent(ClientDetailActivity.this, NewClientActivity.class);
                intent.putExtra("CLIENT_ID_EDIT", currentClient.clienteId);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (currentClient != null) {
                clienteViewModel.delete(currentClient);
                Toast.makeText(this, "Client deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}