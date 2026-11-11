package com.example.practicaltest01var05

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// d.1
class PracticalTest01Var05Service : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val template = intent?.getStringExtra("TEMPLATE")
        if (template != null) {
            val elements = template.split(",")
            job = serviceScope.launch {
                for (element in elements) {
                    val elementTrimmed = element.trim()
                    if (elementTrimmed.isNotEmpty()) {
                        val broadcastIntent = Intent()
                        broadcastIntent.action = "ro.pub.cs.systems.eim.practicaltest01var05.action"
                        broadcastIntent.putExtra("data", elementTrimmed)
                        sendBroadcast(broadcastIntent)
                        Log.d("EIM_SERVICE", element)
                        delay(5000)
                    }
                }
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}