package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters;

public class DiseaseIndexItem {
    private int mDiseaseImageImageResource;
    private String mDiseaseName;

    public DiseaseIndexItem(int diseaseImageImageResource, String diseaseName) {
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
