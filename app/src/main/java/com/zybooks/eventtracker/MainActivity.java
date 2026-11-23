package com.zybooks.eventtracker;

//import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfig = new AppBarConfiguration.
                    Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        }

        // request user permission for event notifications
        requestPermissions();

        // TODO: if making the EncryptedDataStore application-level accessible, start service here
        // start EncryptedDataStore service
        //Intent serviceIntent = new Intent(this, EncryptedDataStore.class);

        //startService(serviceIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
            Navigation.findNavController(this, R.id.nav_host_fragment);

        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    /**
     * requestPermissions: request the permissions necessary to perform:
     * - timed event notifications
     * - network access
     * - determine network status
     */
    private void requestPermissions() {
        String[] permissions = {
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        };

        List<String> permissionRequests = new ArrayList<>();

        // determine permission status of each required permission, and add it to a list if denied
        for (String permission : permissions) {
            int permissionGranted =
                    ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                permissionRequests.add(permission);
            }
        }

        // request permission for each denied permission
        if (!permissionRequests.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, permissionRequests.toArray(new String[0]), 1001);
        }
    }
}