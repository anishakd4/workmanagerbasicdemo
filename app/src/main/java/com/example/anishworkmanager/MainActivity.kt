package com.example.anishworkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        findViewById<Button>(R.id.run_work).setOnClickListener {
            val constraints: Constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            //pass data to our worker
            val data: Data =
                Data.Builder().putString(MyWorker.DATA_KEY, "Data from activity").build()

            val workRequest =
                OneTimeWorkRequest.Builder(MyWorker::class.java).setInputData(data).addTag("myanishwork")
                    .setConstraints(constraints).build()

            val workRequest2 =
                PeriodicWorkRequest.Builder(MyWorker::class.java, 10, TimeUnit.HOURS).build()


            //chainingworks
            WorkManager.getInstance(applicationContext).beginWith(workRequest).then(workRequest).then(workRequest).enqueue()

            WorkManager.getInstance(applicationContext).enqueue(workRequest)

            WorkManager.getInstance(applicationContext)
                .getWorkInfoByIdLiveData(workRequest.id)
                .observe(this, object : Observer<WorkInfo> {
                    override fun onChanged(t: WorkInfo?) {
                        if (t != null) {
                            if (t.state == WorkInfo.State.SUCCEEDED) {
                                val str: String? = t.outputData.getString(MyWorker.DATA_KEY)
                                textView.text = str
                            }
                        }
                    }

                })
        }
    }

    fun cancelWork(workRequest: WorkRequest){
        WorkManager.getInstance(applicationContext)
            .cancelAllWorkByTag("myanishwork")
            //.cancelAllWork()
    }
}