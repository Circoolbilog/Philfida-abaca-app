package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

public class DiseaseInfoDBModel {
    int id;
    private String diseaseName;
    private String diseaseDesc;
    private String picture;
    private String treatment;

    @Override
    public String toString() {
        return "DiseaseInfoDBModel{" +
                "id=" + id +
                ", diseaseName='" + diseaseName + '\'' +
                ", diseaseDesc='" + diseaseDesc + '\'' +
                ", picture='" + picture + '\'' +
                ", treatment='" + treatment + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseDesc() {
        return diseaseDesc;
    }

    public void setDiseaseDesc(String diseaseDesc) {
        this.diseaseDesc = diseaseDesc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public DiseaseInfoDBModel() {

    }

    public DiseaseInfoDBModel(int id, String diseaseName, String diseaseDesc, String picture, String treatment) {
        this.id = id;
        this.diseaseName = diseaseName;
        this.diseaseDesc = diseaseDesc;
        this.picture = picture;
        this.treatment = treatment;
    }
}
