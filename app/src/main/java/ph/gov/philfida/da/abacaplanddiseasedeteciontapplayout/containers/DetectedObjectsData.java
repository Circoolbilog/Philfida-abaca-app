package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import java.util.List;

public class DetectedObjectsData {
    private String imageFileName;
    private List<String> detectedDiseases;
    private List<String> symptoms;
    private String geolocation;
    private int groupID;

    public DetectedObjectsData(String imageFileName, List<String> detectedDiseases, List<String> symptoms, String geolocation, int groupID) {
        this.imageFileName = imageFileName;
        this.detectedDiseases = detectedDiseases;
        this.symptoms = symptoms;
        this.geolocation = geolocation;
        this.groupID = groupID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public List<String> getDetectedDiseases() {
        return detectedDiseases;
    }

    public void setDetectedDiseases(List<String> detectedDiseases) {
        this.detectedDiseases = detectedDiseases;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }
}
