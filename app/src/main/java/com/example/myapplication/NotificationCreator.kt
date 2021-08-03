package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

object NotificationCreator {
    private var collapsedView: RemoteViews? = null
    private var notification: Notification? = null
    private val CALL_NOTIFICATION_ID: Int = 2
    var notificationManager: NotificationManager? = null
    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @SuppressLint("NewApi")
    fun setupNotification(name: String, state: Int, time: String, ctx: Context) {
        notificationManager =
            ctx.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val callState = state
        val channelId = "pda_native V2"
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val name = "call_notification_channel"

            NotificationChannel(channelId, name, importance).apply {
                setSound(null, null)
                notificationManager?.createNotificationChannel(this)
            }
        }

        val openAppIntent = Intent(ctx, CallActivity::class.java)
        openAppIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        val openAppPendingIntent = PendingIntent.getActivity(ctx, 0, openAppIntent, 0)

        val acceptCallIntent = Intent(ctx, CallActionReceiver::class.java)
        acceptCallIntent.action = OngoingCallObject.STATE_ACTIVE
        val acceptPendingIntent =
            PendingIntent.getBroadcast(ctx, 0, acceptCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val declineCallIntent = Intent(ctx, CallActionReceiver::class.java)
        declineCallIntent.action = OngoingCallObject.STATE_DISCONNECTED
        val declinePendingIntent = PendingIntent.getBroadcast(
            ctx,
            CALL_NOTIFICATION_ID,
            declineCallIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val contentTextId = when (callState) {
            Call.STATE_RINGING -> OngoingCallObject.CALLING
            Call.STATE_DIALING -> OngoingCallObject.DIALING
            Call.STATE_DISCONNECTED -> OngoingCallObject.CLL_ENDED
            Call.STATE_DISCONNECTING -> OngoingCallObject.CALL_ENDING
            else -> OngoingCallObject.ONGOING
        }

        collapsedView = RemoteViews(ctx.packageName, R.layout.call_notification).apply {
            setTextViewText(R.id.notification_caller_name, name)
            setTextViewText(R.id.notification_call_status, contentTextId)
            if (callState == Call.STATE_RINGING)
                setViewVisibility(R.id.notification_accept_call, View.VISIBLE)
            setTextViewText(R.id.notification_time, time)
            setOnClickPendingIntent(R.id.notification_decline_call, declinePendingIntent)
            setOnClickPendingIntent(R.id.notification_accept_call, acceptPendingIntent)
        }

        val builder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.ic_camera)
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_CALL)
            .setCustomContentView(collapsedView)
            .setOngoing(true)
            .setSound(null)
            .setUsesChronometer(callState == Call.STATE_ACTIVE)
            .setChannelId(channelId)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        notification = builder.build()
        notificationManager?.notify(CALL_NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun updateNotification(time: String) {
        val callState = OngoingCallObject.call?.state
        val contentTextId = when (callState) {
            Call.STATE_RINGING -> OngoingCallObject.CALLING
            Call.STATE_DIALING -> OngoingCallObject.DIALING
            Call.STATE_DISCONNECTED -> OngoingCallObject.CLL_ENDED
            Call.STATE_DISCONNECTING -> OngoingCallObject.CALL_ENDING
            else -> OngoingCallObject.ONGOING
        }
        collapsedView?.apply {
            setTextViewText(R.id.notification_time, time)
            setTextViewText(R.id.notification_call_status, contentTextId)
            setViewVisibility(R.id.notification_accept_call, View.GONE)
        }
        if (callState == Call.STATE_DISCONNECTED)
            notification?.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager?.notify(CALL_NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager?.cancel(CALL_NOTIFICATION_ID)
        notificationManager?.cancelAll()
        notification = null
        notificationManager = null
    }

}