package com.example.workforcemanagement.ui.dep_manager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Task;

public class DetailTrackingTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_tracking_task);

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy và hiển thị chi tiết task
        Task task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            ((TextView) findViewById(R.id.tvTaskTitle)).setText(task.getTitle());
            ((TextView) findViewById(R.id.tvTaskDesc)).setText(task.getDescription());
            ((TextView) findViewById(R.id.tvAssignee)).setText(task.getAssigneeName());
            ((TextView) findViewById(R.id.tvDeadline)).setText(task.getDeadlineString());
            ((TextView) findViewById(R.id.tvPriority)).setText(task.getPriority());
            ((TextView) findViewById(R.id.tvStatus)).setText(task.getStatus());
        }

        // Nút đóng
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
    }
}