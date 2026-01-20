package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R

class SymptomInfo : AppCompatActivity() {
    private lateinit var symptomNameView: TextView
    private lateinit var symptomDescView: TextView
    private lateinit var symptomImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom_info)

        symptomNameView = findViewById(R.id.symptomName)
        symptomDescView = findViewById(R.id.symptomDesc)
        symptomImageView = findViewById(R.id.imageView3)

        val symptomName = intent.getStringExtra("symptomName")
        symptomNameView.text = symptomName
        // Description and image can be populated based on symptomName if available in a database
    }
}
