package com.example.practicaltest01var05

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// a.2
class MainActivity : AppCompatActivity() {

    private lateinit var textField: EditText
    private lateinit var buttonNavigate: Button
    private lateinit var buttonTopLeft: Button
    private lateinit var buttonTopRight: Button
    private lateinit var buttonCenter: Button
    private lateinit var buttonBottomLeft: Button
    private lateinit var buttonBottomRight: Button

    private var numberOfPresses = 0

    private val SERVICE_START_THRESHOLD = 3
    private var isServiceStarted = false

    private lateinit var secondaryActivityLauncher: ActivityResultLauncher<Intent>

    // d.2
    private val messageBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == "ro.pub.cs.systems.eim.practicaltest01var05.action") {
                val data = intent.getStringExtra("data")
                Log.d("PracticalTest01Var05", "Received message: $data")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_var05_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textField = findViewById(R.id.text_field)
        buttonNavigate = findViewById(R.id.button_navigate)
        buttonTopLeft = findViewById(R.id.button_top_left)
        buttonTopRight = findViewById(R.id.button_top_right)
        buttonCenter = findViewById(R.id.button_center)
        buttonBottomLeft = findViewById(R.id.button_bottom_left)
        buttonBottomRight = findViewById(R.id.button_bottom_right)

        // b.2
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("numberOfPresses")) {
                numberOfPresses = savedInstanceState.getInt("numberOfPresses")
                Toast.makeText(this, "Number of presses: $numberOfPresses", Toast.LENGTH_LONG).show()
                Log.d("EIM_RECEIVER", "NUMBER: $numberOfPresses")
            }
        }

        // b.1
        val buttonClickListener = { view: View ->
            val button = view as Button
            val text = button.text.toString()
            if (textField.text.toString().isEmpty()) {
                textField.setText(text)
            } else {
                textField.setText(textField.text.toString() + ", " + text)
            }
            numberOfPresses++

            // d.1
            val currentElements = textField.text.toString().split(",")
            if (currentElements.size > SERVICE_START_THRESHOLD && !isServiceStarted) {
                val serviceIntent = Intent(this, PracticalTest01Var05Service::class.java)
                serviceIntent.putExtra("TEMPLATE", textField.text.toString())
                startService(serviceIntent)
                isServiceStarted = true
            }
        }

        buttonTopLeft.setOnClickListener(buttonClickListener)
        buttonTopRight.setOnClickListener(buttonClickListener)
        buttonCenter.setOnClickListener(buttonClickListener)
        buttonBottomLeft.setOnClickListener(buttonClickListener)
        buttonBottomRight.setOnClickListener(buttonClickListener)

        // c.2
        secondaryActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Result: Verify", Toast.LENGTH_LONG).show()
            } else if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Result: Cancel", Toast.LENGTH_LONG).show()
            }
            textField.setText("")
            numberOfPresses = 0
        }

        buttonNavigate.setOnClickListener {
            val intent = Intent(this, PracticalTest01Var05SecondaryActivity::class.java)
            intent.putExtra("TEMPLATE", textField.text.toString())
            secondaryActivityLauncher.launch(intent)
        }
    }

    // b.2
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("numberOfPresses", numberOfPresses)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        // d.2
        val intentFilter = IntentFilter("ro.pub.cs.systems.eim.practicaltest01var05.action")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(messageBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(messageBroadcastReceiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(messageBroadcastReceiver)
    }

    override fun onDestroy() {
        // d.1
        val serviceIntent = Intent(this, PracticalTest01Var05Service::class.java)
        stopService(serviceIntent)
        super.onDestroy()
    }
}


//~/Library/Android/sdk/platform-tools/adb shell 'am broadcast -a ro.pub.cs.systems.eim.practicaltest01var05.action --es data "Bine ai venit la EIM"'