import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_child_dashboard.*
import java.text.SimpleDateFormat
import java.util.*

class ChildDashboardActivity : AppCompatActivity() {

    // ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_dashboard)

        // Check and request permission if needed
        if (!isUsageStatsPermissionGranted(this)) {
            requestUsageStatsPermission(this)
        }

        // Set up click listeners for buttons
        setTimeLimitsButton.setOnClickListener {
            // Implement your logic for setting time limits
            setAppUsageRestrictions()
        }

        blockAppsButton.setOnClickListener {
            // Implement your logic for blocking apps
        }

        // Track and display app usage statistics
        trackAppUsage()
    }

    private fun trackAppUsage() {
        try {
            // Get the UsageStatsManager
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            // Get the start and end times (e.g., last 24 hours)
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 24 * 60 * 60 * 1000

            // Get the list of app usage stats for the specified time range
            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            // Display app usage statistics
            displayAppUsage(usageStatsList)
        } catch (e: SecurityException) {
            // Handle the case where the app does not have permission to access app usage stats
            Toast.makeText(
                this,
                "Please grant permission to access app usage stats",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            // Handle other exceptions
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayAppUsage(usageStatsList: List<UsageStats>) {
        val appUsageStringBuilder = StringBuilder()

        for (usageStats in usageStatsList) {
            val appName = getAppNameFromPackage(usageStats.packageName)
            val totalUsageTime = usageStats.totalTimeInForeground

            appUsageStringBuilder.append("$appName: ${formatMillisToTime(totalUsageTime)}\n")
        }

        appUsageTextView.text = appUsageStringBuilder.toString()
    }

    private fun getAppNameFromPackage(packageName: String): String {
        val packageManager = packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            // Handle exceptions, e.g., when the app is uninstalled
            "Unknown"
        }
    }

    private fun formatMillisToTime(millis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date(millis))
    }

    // ...
}
