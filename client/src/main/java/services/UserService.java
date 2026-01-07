package services;

import models.User;

import java.util.List;

public class UserService {

    private static UserService instance;
    private AuthService authService = AuthService.getInstance();

    private UserService() {
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User login(String email, String password) {
        return authService.login(email, password);
    }

    public void logout() {
        authService.logout();
    }

    public User register(String email, String password, String fullName) {
        return authService.register(email, password, fullName);
    }

    public User getCurrentUser() {
        return authService.getCurrentUser();
    }

    public List<User> getAllUsers() {
        return authService.getAllUsers();
    }

    public int getUserCount() {
        return authService.getUserCount();
    }
}
