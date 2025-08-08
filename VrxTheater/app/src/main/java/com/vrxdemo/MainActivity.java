package com.vrxdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    private TextView statusText;
    private Button enterVrButton;
    private Button exitVrButton;
    private Button recenterButton;
    private Button changeEnvironmentButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        statusText = findViewById(R.id.status_text);
        enterVrButton = findViewById(R.id.enter_vr_button);
        exitVrButton = findViewById(R.id.exit_vr_button);
        recenterButton = findViewById(R.id.recenter_button);
        changeEnvironmentButton = findViewById(R.id.change_environment_button);
        
        enterVrButton.setOnClickListener(v -> {
            statusText.setText("VR Mode Active");
            enterVrButton.setVisibility(View.GONE);
            exitVrButton.setVisibility(View.VISIBLE);
            recenterButton.setVisibility(View.VISIBLE);
            changeEnvironmentButton.setVisibility(View.VISIBLE);
        });
        
        exitVrButton.setOnClickListener(v -> {
            statusText.setText("VR Mode Inactive");
            enterVrButton.setVisibility(View.VISIBLE);
            exitVrButton.setVisibility(View.GONE);
            recenterButton.setVisibility(View.GONE);
            changeEnvironmentButton.setVisibility(View.GONE);
        });
        
        recenterButton.setOnClickListener(v -> {
            statusText.setText("View Recentered");
        });
        
        changeEnvironmentButton.setOnClickListener(v -> {
            statusText.setText("Environment Changed");
        });
    }
}