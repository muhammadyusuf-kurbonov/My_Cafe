package uz.muhammayusuf.kurbonov.mycafe.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.ui.activities.MainActivity
import kotlin.random.Random


class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Timber.d("onNewToken: new Token is $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val manager = NotificationManagerCompat.from(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ready_orders_channel",
                "Completed orders channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.channel_description)
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            channel.enableVibration(true)
            channel.enableLights(true)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("source", "notification")

        manager.notify(
            Random.nextInt(255),
            NotificationCompat.Builder(this, "ready_orders_channel")
                .setContentTitle(getString(R.string.order_ready_title))
                .setContentText(getString(R.string.order_ready_content, p0.data["table"]))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        )
    }
}
