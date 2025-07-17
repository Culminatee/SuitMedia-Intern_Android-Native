package com.example.android_native.thirdactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_native.R

// Kelas Adapter untuk RecyclerView yang menerima data berupa daftar User
class UserAdapter(
    private val userList: List<User>,
    private val context: Context,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Kelas ViewHolder untuk memegang referensi komponen di tampilan item_user.xml
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText = itemView.findViewById<TextView>(R.id.tvName)
        val emailText = itemView.findViewById<TextView>(R.id.tvEmail)
        val avatarImg = itemView.findViewById<ImageView>(R.id.ivPhoto)
    }

    // Fungsi untuk membuat ViewHolder baru (dipanggil saat RecyclerView butuh item baru)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    // Mengikat data user ke dalam ViewHolder
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        // Menetapkan nilai nama dan email ke TextView
        holder.nameText.text = user.name
        holder.emailText.text = user.email

        // Memuat gambar avatar dari URL menggunakan Glide
        Glide.with(context).load(user.avatar).into(holder.avatarImg)

        // Menangani klik pada itemView (seluruh elemen layout item)
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }
}
