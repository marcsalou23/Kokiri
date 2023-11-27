import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.parent_dashboard.*
import java.util.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager

data class ChildSummary(
    val name: String,
    val activitySummary: String
)

class ParentDashboardActivity : AppCompatActivity() {

    private lateinit var childSummaryAdapter: ChildSummaryAdapter
    private lateinit var childSummaries: MutableList<ChildSummary>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_dashboard)

        // Check and request permission if needed
        if (!isUsageStatsPermissionGranted(this)) {
            requestUsageStatsPermission(this)
        }

        // Initialize RecyclerView
        childSummaryAdapter = ChildSummaryAdapter()
        childSummaries = mutableListOf()
        childSummaryAdapter.submitList(childSummaries)
        childSummaryRecyclerView.layoutManager = LinearLayoutManager(this)
        childSummaryRecyclerView.adapter = childSummaryAdapter

        // Set up Firebase
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("children").child(userId ?: "")

        // Retrieve child data from Firebase
        retrieveChildData()

        // Set up click listeners for buttons
        setTimeLimitsButton.setOnClickListener {
            // Implement your logic for setting time limits
            setAppUsageRestrictions()
        }

        blockAppsButton.setOnClickListener {
            // Implement your logic for blocking apps
        }
    }

    // Check if the permission is granted
    private fun isUsageStatsPermissionGranted(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // Request permission
    private fun requestUsageStatsPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivity(intent)
    }

    private fun setAppUsageRestrictions() {
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

        // Example: Set a time limit for each app
        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            val totalUsageTime = usageStats.totalTimeInForeground

            // Implement your logic to set time limits for specific apps
            if (packageName == "com.example.app1" && totalUsageTime > YOUR_TIME_LIMIT) {
                // Display a warning or take appropriate action
                Toast.makeText(
                    this,
                    "Limit exceeded for $packageName. Take a break!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun retrieveChildData() {
        // Listen for changes in the "children" node
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                childSummaries.clear()

                for (childSnapshot in snapshot.children) {
                    val name = childSnapshot.child("name").getValue(String::class.java) ?: ""
                    val activitySummary = childSnapshot.child("activitySummary").getValue(String::class.java) ?: ""

                    val childSummary = ChildSummary(name, activitySummary)
                    childSummaries.add(childSummary)
                }

                childSummaryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }
}
