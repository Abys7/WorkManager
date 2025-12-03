package com.example.workmanaging.view.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.Cliente;
import com.example.workmanaging.model.entity.JobStatus;
import com.example.workmanaging.model.entity.Progetto;
import com.example.workmanaging.model.entity.UserAction;
import com.example.workmanaging.viewmodel.ClienteViewModel;
import com.example.workmanaging.viewmodel.ProgettoViewModel;
import com.example.workmanaging.viewmodel.UserActionViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewProjectActivity extends AppCompatActivity {

    private EditText etTitle, etDesc;
    private TextView tvStartDate, tvEndDate, tvTitlePage;
    private Spinner spinnerClients;
    private Button btnSave;
    private ProgettoViewModel progettoViewModel;
    private ClienteViewModel clienteViewModel;
    private UserActionViewModel userActionViewModel;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private List<Cliente> clientList = new ArrayList<>();
    private static final String PREFS_NAME = "WorkManagingPrefs";
    private static final String KEY_USER_ID = "userId";

    private int editProjectId = -1;
    private Progetto projectToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        etTitle = findViewById(R.id.et_project_title);
        etDesc = findViewById(R.id.et_project_desc);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        spinnerClients = findViewById(R.id.spinner_clients);
        btnSave = findViewById(R.id.btn_save);
        tvTitlePage = findViewById(R.id.tv_new_project_title);

        progettoViewModel = new ViewModelProvider(this).get(ProgettoViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        userActionViewModel = new ViewModelProvider(this).get(UserActionViewModel.class);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int userId = prefs.getInt(KEY_USER_ID, -1);

        if (userId == -1) {
            finish();
            return;
        }

        editProjectId = getIntent().getIntExtra("PROJECT_ID_EDIT", -1);

        setupSpinners(userId, () -> {
             if (editProjectId != -1) {
                tvTitlePage.setText("Edit Job");
                btnSave.setText("Update");
                loadProjectData(userId);
            }
        });
       
        setupDatePickers();

        btnSave.setOnClickListener(v -> saveProject(userId));
    }

    private void setupSpinners(int userId, Runnable onClientsLoaded) {
        clienteViewModel.getClientsForUser(userId).observe(this, clients -> {
            clientList = clients;
            List<String> clientNames = new ArrayList<>();
            clientNames.add("None");
            for (Cliente c : clients) {
                clientNames.add(c.nome);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClients.setAdapter(adapter);
            if (onClientsLoaded != null) {
                onClientsLoaded.run();
            }
        });
    }
    
    private void loadProjectData(int userId) {
        progettoViewModel.getProjectsForUser(userId).observe(this, projects -> {
            if (projectToEdit == null) {
                for (Progetto p : projects) {
                    if (p.progettoId == editProjectId) {
                        projectToEdit = p;
                        etTitle.setText(p.titolo);
                        etDesc.setText(p.descrizione);
                        if (p.inizio != null) {
                            startCalendar.setTime(p.inizio);
                            updateLabel(tvStartDate, startCalendar);
                        }
                        if (p.scadenza != null) {
                            endCalendar.setTime(p.scadenza);
                            updateLabel(tvEndDate, endCalendar);
                        }
                        setClientSelection();
                        break;
                    }
                }
            }
        });
    }

    private void setClientSelection() {
        if (projectToEdit != null && projectToEdit.clienteId != null && !clientList.isEmpty()) {
            for (int i = 0; i < clientList.size(); i++) {
                if (clientList.get(i).clienteId == projectToEdit.clienteId) {
                    spinnerClients.setSelection(i + 1);
                    break;
                }
            }
        }
    }

    private void setupDatePickers() {
        tvStartDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                startCalendar.set(year, month, dayOfMonth);
                updateLabel(tvStartDate, startCalendar);
            }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvEndDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                endCalendar.set(year, month, dayOfMonth);
                updateLabel(tvEndDate, endCalendar);
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateLabel(TextView textView, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        textView.setText(sdf.format(calendar.getTime()));
    }

    private void saveProject(int userId) {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editProjectId != -1 && projectToEdit != null) {
            projectToEdit.titolo = title;
            projectToEdit.descrizione = desc;
            if (!tvStartDate.getText().toString().equals("Select Start Date")) {
                projectToEdit.inizio = startCalendar.getTime();
            }
            if (!tvEndDate.getText().toString().equals("Select End Date")) {
                projectToEdit.scadenza = endCalendar.getTime();
            }
            int selectedPosition = spinnerClients.getSelectedItemPosition();
            if (selectedPosition > 0 && !clientList.isEmpty()) {
                projectToEdit.clienteId = clientList.get(selectedPosition - 1).clienteId;
            } else {
                projectToEdit.clienteId = null;
            }
            projectToEdit.stato = calculateStatus(projectToEdit.inizio, projectToEdit.scadenza);
            progettoViewModel.update(projectToEdit);
            
            Toast.makeText(this, "Project updated", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Progetto newProject = new Progetto();
            newProject.userId = userId;
            newProject.titolo = title;
            newProject.descrizione = desc;

            if (!tvStartDate.getText().toString().equals("Select Start Date")) {
                newProject.inizio = startCalendar.getTime();
            }
            if (!tvEndDate.getText().toString().equals("Select End Date")) {
                newProject.scadenza = endCalendar.getTime();
            }

            int selectedPosition = spinnerClients.getSelectedItemPosition();
            if (selectedPosition > 0 && !clientList.isEmpty()) {
                newProject.clienteId = clientList.get(selectedPosition - 1).clienteId;
            }

            newProject.stato = calculateStatus(newProject.inizio, newProject.scadenza);

            progettoViewModel.insert(newProject, id -> {
                UserAction action = new UserAction();
                action.userId = userId;
                action.actionType = "CREATE_PROJECT";
                action.referenceId = (int) id;
                action.timestamp = new Date();
                action.title = "New Project: " + title;
                action.subtitle = newProject.stato != null ? newProject.stato.name() : "";
                action.status = newProject.stato != null ? newProject.stato.name() : "";
                userActionViewModel.insert(action);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Project saved", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
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
