package com.example.android_native

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    // Deklarasi UI element
    private lateinit var etName: EditText
    private lateinit var etPalindrome: EditText
    private lateinit var btnCheck: Button
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur status bar supaya transparan
        val window = window
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Menggunakan layout activity_main
        setContentView(R.layout.activity_main)

        // Inisialisasi komponen UI
        etName = findViewById(R.id.etName)
        etPalindrome = findViewById(R.id.etPalindrome)
        btnCheck = findViewById(R.id.btnCheck)
        btnNext = findViewById(R.id.btnNext)

        // Tombol untuk mengecek apakah input adalah palindrome
        btnCheck.setOnClickListener {
            val input = etPalindrome.text.toString()
            if (isPalindrome(input)) {
                showDialog("isPalindrome")
            } else {
                showDialog("not Palindrome")
            }
        }

        // Tombol untuk pindah ke SecondActivity sambil mengirim nama
        btnNext.setOnClickListener {
            val name = etName.text.toString()
            val intent = Intent(this@MainActivity, SecondActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }

    // Fungsi untuk mengecek apakah string adalah palindrome
    private fun isPalindrome(input: String): Boolean {
        val clean = input.replace("\\s+".toRegex(), "").lowercase()
        val reversed = clean.reversed()
        return clean == reversed
    }

    // Menampilkan dialog pop up
    private fun showDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
