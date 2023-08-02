package com.example.randomapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(context: Context, private val listener: OnSwitchClickListener) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {
    private var list: List<App>? = null
    private val packageManager: PackageManager

    init {
        packageManager = context.packageManager
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<App>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val app = list!![position]
        holder.imgApp.setImageDrawable(packageManager.getApplicationIcon(app.info))
        holder.nameApp.text = packageManager.getApplicationLabel(app.info)
        holder.chooseApp.isChecked = app.isChecked
        holder.chooseApp.setOnClickListener { listener.onChangeChecked(packageManager.getApplicationLabel(app.info).toString(), !app.isChecked) }
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgApp: ImageView
        val nameApp: TextView

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val chooseApp: Switch

        init {
            imgApp = itemView.findViewById(R.id.imageApp)
            nameApp = itemView.findViewById(R.id.nameApp)
            chooseApp = itemView.findViewById(R.id.btnChoose)
        }
    }

    interface OnSwitchClickListener {
        fun onChangeChecked(name: String?, state: Boolean)
    }
}