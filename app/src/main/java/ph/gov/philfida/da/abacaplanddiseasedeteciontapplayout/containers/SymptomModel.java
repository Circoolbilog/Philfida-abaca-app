package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

public class SymptomModel {
    private String symptomName;
    private Boolean Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV;

    public SymptomModel() {
    }

    public SymptomModel(String symptomName, Boolean Bract_Mosaic, Boolean Bunchy_Top, Boolean CMV, Boolean Gen_Mosaic, Boolean SCMV) {
        this.symptomName = symptomName;
        this.Bract_Mosaic = Bract_Mosaic;
        this.Bunchy_Top = Bunchy_Top;
        this.CMV = CMV;
        this.Gen_Mosaic = Gen_Mosaic;
        this.SCMV = SCMV;
    }


    //getter and setter
    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public Boolean getBract_Mosaic() {
        return Bract_Mosaic;
    }

    public void setBract_Mosaic(Boolean bract_Mosaic) {
        Bract_Mosaic = bract_Mosaic;
    }

    public Boolean getBunchy_Top() {
        return Bunchy_Top;
    }

    public void setBunchy_Top(Boolean bunchy_Top) {
        Bunchy_Top = bunchy_Top;
    }

    public Boolean getCMV() {
        return CMV;
    }

    public void setCMV(Boolean CMV) {
        this.CMV = CMV;
    }

    public Boolean getGen_Mosaic() {
        return Gen_Mosaic;
    }

    public void setGen_Mosaic(Boolean gen_Mosaic) {
        Gen_Mosaic = gen_Mosaic;
    }

    public Boolean getSCMV() {
        return SCMV;
    }

    public void setSCMV(Boolean SCMV) {
        this.SCMV = SCMV;
    }


    //toString

    @Override
    public String toString() {
        return "SymptomModel{" +
                "symptomName='" + symptomName + '\'' +
                ", Bract_Mosaic=" + Bract_Mosaic +
                ", Bunchy_Top=" + Bunchy_Top +
                ", CMV=" + CMV +
                ", Gen_Mosaic=" + Gen_Mosaic +
                ", SCMV=" + SCMV +
                '}';
    }
}
