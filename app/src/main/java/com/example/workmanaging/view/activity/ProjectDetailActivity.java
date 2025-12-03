package com.example.workmanaging.view.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.JobStatus;
import com.example.workmanaging.model.entity.Progetto;
import com.example.workmanaging.model.entity.UserAction;
import com.example.workmanaging.viewmodel.ClienteViewModel;
import com.example.workmanaging.viewmodel.ProgettoViewModel;
import com.example.workmanaging.viewmodel.UserActionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProjectDetailActivity extends AppCompatActivity {

    private ProgettoViewModel progettoViewModel;
    private ClienteViewModel clienteViewModel;
    private UserActionViewModel userActionViewModel;
    private Progetto currentProject;
    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        if (projectId == -1) {
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tv_project_title_detail);
        TextView tvClient = findViewById(R.id.tv_project_client);
        TextView tvDates = findViewById(R.id.tv_project_dates_detail);
        TextView tvDesc = findViewById(R.id.tv_project_desc_detail);
        View statusIndicator = findViewById(R.id.status_indicator);
        FloatingActionButton btnDelete = findViewById(R.id.btn_delete);
        FloatingActionButton btnEdit = findViewById(R.id.btn_edit);

        progettoViewModel = new ViewModelProvider(this).get(ProgettoViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        userActionViewModel = new ViewModelProvider(this).get(UserActionViewModel.class);
        
        int userId = getSharedPreferences("WorkManagingPrefs", 0).getInt("userId", -1);

        progettoViewModel.getProjectsForUser(userId).observe(this, projects -> {
            for (Progetto p : projects) {
                if (p.progettoId == projectId) {
                    currentProject = p;
                    tvTitle.setText(p.titolo);
                    tvDesc.setText(p.descrizione);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String start = p.inizio != null ? sdf.format(p.inizio) : "-";
                    String end = p.scadenza != null ? sdf.format(p.scadenza) : "-";
                    tvDates.setText("Dates: " + start + " - " + end);
                    
                    JobStatus status = calculateStatus(p.inizio, p.scadenza);
                    int colorRes;
                    switch (status) {
                        case COMPLETATO: colorRes = R.color.status_completed; break;
                        case IN_CORSO: colorRes = R.color.status_in_progress; break;
                        default: colorRes = R.color.status_not_started; break;
                    }
                    if(statusIndicator != null) {
                        statusIndicator.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, colorRes)));
                    }
                    
                    if (p.clienteId != null) {
                        clienteViewModel.getClientsForUser(userId).observe(this, clients -> {
                             for (com.example.workmanaging.model.entity.Cliente c : clients) {
                                 if (c.clienteId == p.clienteId) {
                                     tvClient.setText("Client: " + c.nome);
                                     break;
                                 }
                             }
                        });
                    } else {
                        tvClient.setText("Client: Not Associated");
                    }

                    if (savedInstanceState == null) {
                         UserAction action = new UserAction();
                         action.userId = userId;
                         action.actionType = "OPEN_PROJECT";
                         action.referenceId = projectId;
                         action.timestamp = new Date();
                         action.title = "Viewed Project: " + p.titolo;
                         action.subtitle = status.name();
                         action.status = status.name();
                         userActionViewModel.insert(action);
                    }

                    break;
                }
            }
        });

        btnEdit.setOnClickListener(v -> {
            if (currentProject != null) {
                Intent intent = new Intent(ProjectDetailActivity.this, NewProjectActivity.class);
                intent.putExtra("PROJECT_ID_EDIT", currentProject.progettoId);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (currentProject != null) {
                progettoViewModel.delete(currentProject);
                Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private JobStatus calculateStatus(Date start, Date end) {
        Date now = new Date();
        if (end != null && now.after(end)) {
            return JobStatus.COMPLETATO;
        } else if (start != null && now.after(start)) {
            return JobStatus.IN_CORSO;
        } else {
            return JobStatus.NON_INIZIATO;
        }
    }
}