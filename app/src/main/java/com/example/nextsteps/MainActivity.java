package com.example.nextsteps;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextView toolbar_date_time;
    private final Handler handler = new Handler();
    private Runnable updateTimeTask;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView appbarprofile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar_date_time=findViewById(R.id.toolbar_date_time);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        appbarprofile=findViewById(R.id.appbarprofile);




        String currentDateTime = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault()).format(new Date());
        toolbar_date_time.setText(currentDateTime);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh data or content
            refreshContent();

            // Stop the refresh animation after a delay (2 seconds)
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });


    }

    private void startUpdatingTime() {
        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                // Get current date and time
                String currentDateTime = new SimpleDateFormat("EEE, MMM d, yyyy h:mm:ss a", Locale.getDefault()).format(new Date());

                // Set the date and time in the toolbar
                toolbar_date_time.setText(currentDateTime);

                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the first execution
        handler.post(updateTimeTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending updates to avoid memory leaks
        handler.removeCallbacks(updateTimeTask);
    }
    private void refreshContent() {
        // Logic to refresh content
        String refreshedDateTime = new SimpleDateFormat("EEE, MMM d, yyyy h:mm:ss a", Locale.getDefault()).format(new Date());
        toolbar_date_time.setText(refreshedDateTime);

        // You can also reload data or perform any refresh logic here
    }


}
