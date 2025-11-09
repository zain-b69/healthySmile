package ma.dentalTech.entities.patient;

import java.time.LocalDate;

public class PatientChat {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String email;
    private String insurance;

    public PatientChat() {}
    public PatientChat(Long id, String firstName, String lastName, LocalDate birthDate, String address, String phone, String email, String insurance) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.insurance = insurance;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getFirstName() { return firstName; }

        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }

        public void setLastName(String lastName) { this.lastName = lastName; }

        public LocalDate getBirthDate() { return birthDate; }

        public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

        public String getAddress() { return address; }

        public void setAddress(String address) { this.address = address; }

        public String getPhone() { return phone; }

        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }

        public void setEmail(String email) { this.email = email; }

        public String getInsurance() { return insurance; }

        public void setInsurance(String insurance) { this.insurance = insurance; }

}
