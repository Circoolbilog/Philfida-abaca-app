package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database

data class CaptureRecord(
    val id: Long? = null,
    val imagePath: String,
    val imageName: String,
    val detectionResults: String? = null,
    val symptomsDetected: String? = null,
    val confidenceScores: String? = null,
    val boundingBoxes: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long
)
