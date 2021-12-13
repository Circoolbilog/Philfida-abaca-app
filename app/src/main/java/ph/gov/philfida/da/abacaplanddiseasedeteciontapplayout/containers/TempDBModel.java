package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;


public class TempDBModel {
    int id;
    String imageName, symptomsDetected;
    byte[] capturedImage1;
    byte[] capturedImage2;

    public TempDBModel(int id, String imageNeme, byte[] capturedImage1, byte[] capturedImage2, String symptomsDetected, Float confidence) {
    }

    @Override
    public String toString() {
        return "TempDBModel{" +
                "id=" + id +
                ", imageName='" + imageName + '\'' +
                ", symptomsDetected='" + symptomsDetected + '\'' +
                ", capturedImage1=" + capturedImage1 +
                ", capturedImage2=" + capturedImage2 +
                ", confidence=" + confidence +
                '}';
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    Float confidence;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymptomsDetected() {
        return symptomsDetected;
    }

    public void setSymptomsDetected(String symptomsDetected) {
        this.symptomsDetected = symptomsDetected;
    }

    public byte[] getCapturedImage1() {
        return capturedImage1;
    }

    public void setCapturedImage1(byte[] capturedImage1) {
        this.capturedImage1 = capturedImage1;
    }

    public byte[] getCapturedImage2() {
        return capturedImage2;
    }

    public void setCapturedImage2(byte[] capturedImage2) {
        this.capturedImage2 = capturedImage2;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }
}
