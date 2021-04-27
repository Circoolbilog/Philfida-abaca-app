package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

public class User {
    public String lastName, middleName, firstName, birthday, email, permanentAddress, occupation, institution;

    public User() {

    }

    public User(String lastName, String middleName, String firstName, String birthday,
                String email, String permanentAddress, String occupation, String institution) {
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.email = email;
        this.permanentAddress = permanentAddress;
        this.occupation = occupation;
        this.institution = institution;
    }
}
