package ma.dentalTech.service.modules.users.api;

public interface PasswordManagementService {

    void changePassword(Long userId, String oldPassword, String newPassword);

    void resetPassword(Long userId, String newPassword);
}
