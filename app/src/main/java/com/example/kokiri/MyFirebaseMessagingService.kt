import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if the message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            // Handle the data payload.
            handleDataMessage(remoteMessage.data)
        }

        // Check if the message contains a notification payload.
        remoteMessage.notification?.let {
            // Handle the notification payload.
            handleNotification(it)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"]

        when (type) {
            "update" -> {
                // Handle update message
                // Example: Notify the user about an update
                showNotification("App Update", "There is a new update available.")
            }
            "restriction" -> {
                // Handle restriction message
                // Example: Notify the child about a new restriction
                showNotification("App Restriction", "A new app restriction is in effect.")
            }
            // Add more cases based on your payload structure
            else -> {
                // Handle other types or default case
            }
        }
    }

    private fun handleNotification(notification: RemoteMessage.Notification) {
        // Handle the notification payload directly if needed.
        // You might want to display a notification in this case.
        showNotification(notification.title, notification.body)
    }

    private fun showNotification(title: String?, body: String?) {
        // Display the notification using the NotificationCompat library.
        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            // Add other notification settings as needed

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notify with a unique notification ID to update or replace existing notifications.
        notificationManager.notify(/* unique_notification_id */ 1, notificationBuilder.build())
    }
}
