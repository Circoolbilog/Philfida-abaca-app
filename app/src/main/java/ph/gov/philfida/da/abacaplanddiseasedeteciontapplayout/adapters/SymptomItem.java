package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters;

public class SymptomItem {
    private final String mDiseaseName;
    private String tag;

    public SymptomItem(String diseaseName) {
        this.mDiseaseName = diseaseName;
    }

    public String getDiseaseName() {
        return mDiseaseName;
    }
}

