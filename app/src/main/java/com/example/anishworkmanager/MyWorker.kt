package com.example.anishworkmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        val DATA_KEY = "data_key"
        val MESSAGE_ID: Int = 1001
    }

    override fun doWork(): Result {
        Log.i("anisham", "anisham MyWorker doWork " + Thread.currentThread())

        //pass data to activity
        val data: Data = Data.Builder().putString(DATA_KEY, "Hello from doWork").build()

        //Receive data from activity
        val response: String? = inputData.getString(DATA_KEY)
        if (response != null) {
            Log.i("anisham", "anisham MyWorker Receive data from activity == " + response)
            displayNotification(response, response + "anish")
        }


        return Result.success(data)
    }

    fun displayNotification(title: String, message: String) {
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel = NotificationChannel(
                "message_channel",
                "task_notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationIntent = Intent(applicationContext, ShowDetails::class.java)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, "message_channel")
                .setContentTitle(title).setContentText(message)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(applicationContext, R.color.purple_700))
                .setAutoCancel(true)

        notificationManager.notify(MESSAGE_ID, notification.build())
    }

}