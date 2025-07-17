package com.example.android_native

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.example.android_native.thirdactivity.ThirdActivity

class SecondActivity : AppCompatActivity() {

    // Deklarasi view
    private lateinit var txtName: TextView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var ivPhoto: ImageView
    private lateinit var btnChooseUser: Button
    private lateinit var btnBack: ImageButton

    // Activity Result Launcher untuk menerima data dari ThirdActivity
    private val userResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Ambil data yang dikirimkan dari ThirdActivity
            val selectedName = result.data?.getStringExtra("USER_NAME")
            val selectedEmail = result.data?.getStringExtra("USER_EMAIL")
            val photoUrl = result.data?.getStringExtra("USER_PHOTO")

            // Tampilkan nama dan email yang dipilih
            selectedName?.let { tvName.text = it }
            selectedEmail?.let { tvEmail.text = it }

            // Tampilkan avatar menggunakan Glide jika URL tidak kosong
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.default_photo)
                    .into(ivPhoto)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Atur status bar jadi transparan
        val window = window
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_second)

        // Inisialisasi semua komponen tampilan
        txtName = findViewById(R.id.txtName)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        ivPhoto = findViewById(R.id.ivPhoto)
        btnChooseUser = findViewById(R.id.btnChooseUser)
        btnBack = findViewById(R.id.btnBack)

        // Ambil data nama dari MainActivity
        val name = intent.getStringExtra("name")
        Log.d("SecondActivity", "Received name: $name")

        // Tampilkan nama user (kalau tidak ada akan menampilkan pesan default)
        txtName.text = if (!name.isNullOrEmpty()) name else "Input your name first"

        // Tampilkan data default sebelum memilih user dari ThirdActivity
        tvName.text = "Selected User"
        tvEmail.text = "selecteduser@example.com"
        ivPhoto.setImageResource(R.drawable.default_photo)

        // Tombol kembali ke halaman sebelumnya
        btnBack.setOnClickListener {
            finish()
        }

        // Tombol untuk membuka ThirdActivity dan memilih user
        btnChooseUser.setOnClickListener {
            val intent = Intent(this@SecondActivity, ThirdActivity::class.java)
            userResultLauncher.launch(intent)
        }
    }
}
