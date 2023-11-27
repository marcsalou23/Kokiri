import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationTitleTextView: TextView
    private lateinit var notificationListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize UI components
        notificationTitleTextView = findViewById(R.id.notificationTitleTextView)
        notificationListView = findViewById(R.id.notificationListView)

        // Set up the list of notifications (replace with your actual data)
        val notifications = arrayOf("Notification 1", "Notification 2", "Notification 3")

        // Create an ArrayAdapter to populate the ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notifications)

        // Set the adapter to the ListView
        notificationListView.adapter = adapter

        // Set the title of the activity
        notificationTitleTextView.text = "Notifications"
    }
}
