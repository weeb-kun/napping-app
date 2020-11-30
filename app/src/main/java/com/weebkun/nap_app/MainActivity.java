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

package com.weebkun.nap_app;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.weebkun.nap_app.service.NappingService;
import com.weebkun.nap_app.service.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int notificationId = 1;
        String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG};
        List<String> notGrantedList = new ArrayList<>();

        // check for permissions
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) notGrantedList.add(permission);
        }

        //request not granted permissions
        if(notGrantedList.size() > 0) {
            this.requestPermissions(notGrantedList.toArray(new String[]{}), 69);
        } else {
            //all permissions granted
            this.setContentView(R.layout.activity_main);
        }

        //create notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("napping", "naps", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Button napButton = this.findViewById(R.id.button);
        Intent intent = new Intent(this, NappingService.class);

        // check if service is still running
        if(!ServiceManager.state.booleanState){
            napButton.setOnClickListener(v -> {
                // start napping service
                startService(intent);
            });
        } else {
            //service running
            napButton.setText("Wake up");
            napButton.setOnClickListener(v -> {
                NotificationManagerCompat manager = NotificationManagerCompat.from(NappingService.service);
                //stop service
                stopService(intent);
                manager.cancel(notificationId);
            });
        }
    }

    /**
     * check the results of the permission request.
     * @param requestCode the request code
     * @param permissions the list of permissions
     * @param grantResults the results of request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StringBuilder toastMsg = new StringBuilder();
        if(requestCode == 69){
            for(int i = 0; i < permissions.length; i++) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    toastMsg.append("Error: permission " + permissions[i] + " not granted.\n");
                }
            }
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        }
        this.setContentView(R.layout.activity_main);
    }
}