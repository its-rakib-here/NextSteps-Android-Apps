package com.example.nextsteps;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView toolbarDate, toolbarDay, toolbarWelcomeMessage;
    FloatingActionButton fab;
    BottomNavigationView bottom_navigation;
    ImageView appbarprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarDate = findViewById(R.id.toolbar_date);
        toolbarDay = findViewById(R.id.toolbar_day);
        toolbarWelcomeMessage = findViewById(R.id.toolbar_welcome_message);
        fab = findViewById(R.id.fab);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        appbarprofile = findViewById(R.id.appbarprofile);

        appbarprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,UserProfile.class));
            }
        });






        // Set Date
        String currentDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
        toolbarDate.setText(currentDate);

        // Set Day
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        toolbarDay.setText(currentDay);

        // Set Welcome Message
        String userName = "Rakib"; // Replace with the username from the database
        String welcomeMessage = "Welcome back, " + userName + "! Let's finish your today's work";
        toolbarWelcomeMessage.setText(welcomeMessage);




        // Set Floating Action Button click listener
        fab.setOnClickListener(view -> {
            // Create the dialog
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_create_options);
            dialog.setCancelable(true);

            // Get buttons from the dialog
            Button btnCreateTask = dialog.findViewById(R.id.btn_create_task);
            Button btnCreateProject = dialog.findViewById(R.id.btn_create_project);

            // Set button click listeners
            btnCreateTask.setOnClickListener(v -> {
                dialog.dismiss(); // Close the dialog
                startActivity(new Intent(MainActivity.this, CreateTask.class));
            });

            btnCreateProject.setOnClickListener(v -> {
                dialog.dismiss(); // Close the dialog
                startActivity(new Intent(MainActivity.this, ProjectActivity.class));
            });

            // Show the dialog
            dialog.show();
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new HomeFragment())
                    .commit();
        }


        // Set Bottom Navigation View listener
        pickSelectedBottomNavigaton();
    }



    private void pickSelectedBottomNavigaton()
    {
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Get the order value which represents the position of the item
                int position = item.getOrder();

                switch (position) {
                    case 0:
                        // First item (Home) selected
                        if (!(getSupportFragmentManager().findFragmentById(R.id.content_frame) instanceof HomeFragment)) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, new HomeFragment())
                                    .commit();
                        }
                        return true;
                    case 1:
                        // Second item (Task) selected
                        // Add your code here for Task
                        startActivity(new Intent(MainActivity.this, ShowTask.class));
                        Toast.makeText(MainActivity.this,"Task is selected",Toast.LENGTH_SHORT).show();

                        return true;
                    case 2:
                        startActivity(new Intent(MainActivity.this, ShowProjectActivity.class));

                        // Third item (Project) selected
                        // Add your code here for Project
                        return true;
                    default:
                        return false;
                }
            }
        });


    }
}
