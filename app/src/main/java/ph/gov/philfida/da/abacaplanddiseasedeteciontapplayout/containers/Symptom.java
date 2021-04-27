package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

public class Symptom {
    private String SymptomName, Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV;

    public Symptom() {
    }

    public Symptom(String SymptomName, String Bract_Mosaic, String Bunchy_Top, String CMV, String Gen_Mosaic, String SCMV) {
        this.SymptomName = SymptomName;
        this.Bract_Mosaic = Bract_Mosaic;
        this.Bunchy_Top = Bunchy_Top;
        this.CMV = CMV;
        this.Gen_Mosaic = Gen_Mosaic;
        this.SCMV = SCMV;
    }
}
