package com.example.popupmonitor

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class PopupMonitorService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 仅处理窗口状态变化事件 (弹窗、切换应用等)
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            
            // 过滤掉系统 UI 和本项目自身
            if (packageName == "com.android.systemui" || packageName == applicationContext.packageName) {
                return
            }

            Log.d("PopupMonitor", "检测到窗口变化，来源包名: $packageName")
            
            // 发送广播通知 Activity 更新 UI
            val intent = Intent("com.example.popupmonitor.NEW_POPUP")
            intent.putExtra("package_name", packageName)
            intent.putExtra("timestamp", System.currentTimeMillis())
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    override fun onInterrupt() {
        // 服务中断时的处理
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("PopupMonitor", "无障碍服务已连接")
    }
}
