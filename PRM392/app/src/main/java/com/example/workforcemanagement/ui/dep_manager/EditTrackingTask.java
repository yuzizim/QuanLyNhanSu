package com.example.workforcemanagement.ui.dep_manager;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Task;

public class EditTrackingTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_tracking_task);

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy và hiển thị chi tiết task
        Task task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            ((EditText) findViewById(R.id.etTaskTitle)).setText(task.getTitle());
            ((EditText) findViewById(R.id.etTaskDesc)).setText(task.getDescription());
            ((EditText) findViewById(R.id.etDeadline)).setText(task.getDeadlineString());
            // TODO: set Assignee, Priority, Status spinner (load list, chọn đúng giá trị)
        }

        // TODO: Xử lý cập nhật backend khi bấm btnUpdateTask
    }
}