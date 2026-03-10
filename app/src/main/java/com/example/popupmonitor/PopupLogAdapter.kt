package com.example.popupmonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PopupLogAdapter(
    private val logs: List<PopupRecord>,
    private val onUninstallClick: (String) -> Unit
) : RecyclerView.Adapter<PopupLogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.txtAppName)
        val pkgName: TextView = view.findViewById(R.id.txtPkgName)
        val time: TextView = view.findViewById(R.id.txtTime)
        val btnUninstall: Button = view.findViewById(R.id.btnUninstall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popup_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.appName.text = log.appName
        holder.pkgName.text = log.packageName
        holder.time.text = log.time
        holder.btnUninstall.setOnClickListener { onUninstallClick(log.packageName) }
    }

    override fun getItemCount() = logs.size
}
