package com.example.practicaltest01var05

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// c.1
class PracticalTest01Var05SecondaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_var05_secondary)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textTemplate = findViewById<TextView>(R.id.text_template)
        val buttonVerify = findViewById<Button>(R.id.button_verify)
        val buttonCancel = findViewById<Button>(R.id.button_cancel)

        val intent = intent
        if (intent != null && intent.hasExtra("TEMPLATE")) {
            textTemplate.text = intent.getStringExtra("TEMPLATE")
        }

        buttonVerify.setOnClickListener {
            setResult(RESULT_OK, Intent())
            finish()
        }

        buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED, Intent())
            finish()
        }
    }
}