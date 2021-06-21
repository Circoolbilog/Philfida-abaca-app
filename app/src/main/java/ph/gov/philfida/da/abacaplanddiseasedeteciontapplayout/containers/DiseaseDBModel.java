package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

public class DiseaseDBModel {
    int id;
    private String symptomName;

    public DiseaseDBModel() {
    }

    @Override
    public String toString() {
        return "DiseaseDBModel{" +
                "id=" + id +
                ", symptomName='" + symptomName + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public DiseaseDBModel(int id, String symptomName) {
        this.id = id;
        this.symptomName = symptomName;

    }
}
