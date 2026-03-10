package com.example.popupmonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: PopupLogAdapter
    private val popupLogs = mutableListOf<PopupRecord>()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val packageName = intent?.getStringExtra("package_name") ?: return
            val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())
            
            // 识别应用信息
            val appInfo = getAppInfo(packageName)
            val record = PopupRecord(
                packageName = packageName,
                appName = appInfo.first,
                time = formatTime(timestamp)
            )
            
            // 更新列表
            popupLogs.add(0, record)
            adapter.notifyItemInserted(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化列表
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = PopupLogAdapter(popupLogs) { packageName ->
            uninstallApp(packageName)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 权限引导按钮
        findViewById<Button>(R.id.btnPermission).setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "请在设置中找到本应用并开启无障碍服务", Toast.LENGTH_LONG).show()
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("com.example.popupmonitor.NEW_POPUP"))
    }

    private fun getAppInfo(packageName: String): Pair<String, String> {
        return try {
            val pm = packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            val name = pm.getApplicationLabel(info).toString()
            Pair(name, packageName)
        } catch (e: Exception) {
            Pair("未知应用", packageName)
        }
    }

    private fun formatTime(time: Long): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(time))
    }

    private fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}

data class PopupRecord(
    val packageName: String,
    val appName: String,
    val time: String
)
