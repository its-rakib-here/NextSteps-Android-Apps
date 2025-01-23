package com.example.nextsteps;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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


    FloatingActionButton fab;
    BottomNavigationView bottom_navigation;
    FrameLayout content_frame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottom_navigation = findViewById(R.id.bottom_navigation);
        content_frame = findViewById(R.id.content_frame);


        fab=findViewById(R.id.fab);


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
                        ShowTaskFragment fragment = new ShowTaskFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, new ShowTaskFragment()) // Replace 'fragment_container' with your actual container ID
                                .addToBackStack(null) // Optional: Adds this transaction to the back stack
                                .commit();

                        Toast.makeText(MainActivity.this,"Task is selected",Toast.LENGTH_SHORT).show();

                        return true;
                    case 2:
                        ShowTaskFragment projectFragment = new ShowTaskFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, new ShowProjectFragment()) // Replace 'fragment_container' with your actual container ID
                                .addToBackStack(null) // Optional: Adds this transaction to the back stack
                                .commit();

                        Toast.makeText(MainActivity.this,"Task is selected",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });


    }
}
