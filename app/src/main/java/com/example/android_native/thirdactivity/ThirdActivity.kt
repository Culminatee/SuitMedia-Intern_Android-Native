package com.example.android_native.thirdactivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android_native.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ThirdActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()

    private var currentPage = 1
    private val perPage = 6     // Jumlah data user yang diambil per halaman
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur status bar supaya transparan
        val window: Window = window
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_third)

        // Inisialisasi komponen UI
        recyclerView = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBack)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Inisialisasi adapter dan listener saat user dipilih
        adapter = UserAdapter(userList, this) { user ->
            val resultIntent = Intent().apply {
                putExtra("USER_NAME", user.name)
                putExtra("USER_EMAIL", user.email)
                putExtra("USER_PHOTO", user.avatar)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Setup Swipe Refresh
        swipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            isLastPage = false
            userList.clear()
            adapter.notifyDataSetChanged()
            fetchUsersFromApi(currentPage)
        }

        // Setup infinite scroll (load more ketika sampai bawah)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as LinearLayoutManager
                if (!isLoading && !isLastPage &&
                    layoutManager.findLastCompletelyVisibleItemPosition() == userList.size - 1
                ) {
                    currentPage++
                    fetchUsersFromApi(currentPage)
                }
            }
        })

        // Tombol kembali
        btnBack.setOnClickListener {
            finish()
        }

        // Ambil data awal
        fetchUsersFromApi(currentPage)
    }

    // Fungsi untuk mengambil data user dari API
    private fun fetchUsersFromApi(page: Int) {
        isLoading = true
        runOnUiThread { swipeRefreshLayout.isRefreshing = true }

        Executors.newSingleThreadExecutor().execute {
            try {
                val url = URL("https://reqres.in/api/users?page=$page&per_page=$perPage")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()
                    parseUserJson(response.toString(), page)
                } else {
                    runOnUiThread {
                        swipeRefreshLayout.isRefreshing = false
                        isLoading = false
                        Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error: ", e)
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi untuk parsing JSON dan mengisi data ke list
    private fun parseUserJson(jsonString: String, page: Int) {
        try {
            val jsonObject = JSONObject(jsonString)
            val usersArray = jsonObject.getJSONArray("data")

            if (usersArray.length() == 0 && page == 1) {
                runOnUiThread {
                    Toast.makeText(this, "Empty Data", Toast.LENGTH_SHORT).show()
                }
            }

            for (i in 0 until usersArray.length()) {
                val userObj = usersArray.getJSONObject(i)
                val name = "${userObj.getString("first_name")} ${userObj.getString("last_name")}"
                val email = userObj.getString("email")
                val avatar = userObj.getString("avatar")

                userList.add(User(name, email, avatar))
            }

            val total = jsonObject.getInt("total")
            val totalLoaded = userList.size
            isLastPage = totalLoaded >= total

            runOnUiThread {
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
                isLoading = false
                if (isLastPage) {
                    Toast.makeText(this, "All data has been loaded", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "JSON Parsing Error", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }
}
