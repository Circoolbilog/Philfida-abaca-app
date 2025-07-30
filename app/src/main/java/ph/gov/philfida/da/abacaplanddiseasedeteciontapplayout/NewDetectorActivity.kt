package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.databinding.ActivityNewDetectorBinding

/**
 * Main entry point into your app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments
 */

class NewDetectorActivity : AppCompatActivity() {

    private lateinit var activityMain2Binding: ActivityNewDetectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMain2Binding = ActivityNewDetectorBinding.inflate(layoutInflater)
        setContentView(activityMain2Binding.root)

    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}
