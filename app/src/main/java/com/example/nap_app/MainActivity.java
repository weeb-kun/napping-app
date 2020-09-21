/*
   Copyright 2020 weebkun

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.example.nap_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.example.nap_app.service.NappingService;
import com.example.nap_app.service.ServiceManager;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int notificationId = 1;

        // check for permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED
        ){
            // 1 or more permissions got denied.
            // request permissions
            ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), granted -> {
                boolean allGranted = true;
                for(Map.Entry<String, Boolean> entry : granted.entrySet()) {
                    if(entry.getValue()) {
                        continue;
                    } else {
                        allGranted = false;
                    }
                }
                if(allGranted){
                    setContentView(R.layout.activity_main);
                }
            });
            requestPermissions.launch(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS});
        }

        //create notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("napping", "naps", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Button napButton = findViewById(R.id.button);
        Intent intent = new Intent(this, NappingService.class);

        // get notification manager
        NotificationManagerCompat manager = NotificationManagerCompat.from(NappingService.service);

        // check if service is still running
        if(ServiceManager.state.booleanState){
            //service running
            napButton.setText("Wake up");
            napButton.setOnClickListener(v -> {
                //stop service
                stopService(intent);
                manager.cancel(notificationId);
            });
        } else {
            napButton.setOnClickListener(v -> {
                // start napping service
                startService(intent);
            });
        }
    }
}