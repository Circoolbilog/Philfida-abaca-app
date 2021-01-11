package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters;

public class DiseaseIntexItem {
    private int mDiseaseImageImageResource;
    private String mDiseaseName;

    public DiseaseIntexItem(int diseaseImageImageResource, String diseaseName) {
        this.mDiseaseImageImageResource = diseaseImageImageResource;
        this.mDiseaseName = diseaseName;
    }

    public int getDiseaseImageImageResource() {
        return mDiseaseImageImageResource;
    }

    public String getDiseaseName() {
        return mDiseaseName;
    }
}
