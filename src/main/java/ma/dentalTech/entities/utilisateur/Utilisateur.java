package ma.dentalTech.entities.utilisateur;

import ma.dentalTech.entities.role.Role;

public class Utilisateur {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;

    public Utilisateur() {}
    public Utilisateur(Long id, String username, String email, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    public Long getId() { return id; }

        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }

        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }

        public void setEmail(String email) { this.email = email; }

        public String getPasswordHash() { return passwordHash; }

        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public Role getRole() { return role; }

        public void setRole(Role role) { this.role = role; }
}
