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

package com.example.nap_app.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;

import com.example.nap_app.MainActivity;
import com.example.nap_app.R;

public class NappingService extends Service {

    private WorkManager manager;
    int notificationId = 1;

    @SuppressLint("StaticFieldLeak")
    public static Service service;

    public NappingService(){}

    public NappingService(WorkManager manager) {
        this.manager = manager;
    }

    /**
     * fired when this service is started.
     */
    @Override
    public void onCreate() {
        ServiceManager.state = ServiceManager.ServiceState.RUNNING;
        HandlerThread thread = new HandlerThread("nappingService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notifIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(notifIntent);

        // get notification manager
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // set service so its retrievable from MainActivity. the NotificationManagerCompat needs this context to close the notification.
        NappingService.service = this;

        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "napping")
                .setSmallIcon(R.drawable.notif_napping)
                .setContentTitle("Napping")
                .setContentText("Automatically replying to messages...")
                .setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);

        // show the notification
        manager.notify(notificationId, builder.build());
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ServiceManager.state = ServiceManager.ServiceState.STOPPED;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("cannot be bound.");
    }
}
