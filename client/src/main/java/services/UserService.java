package services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.AdminUser;
import models.RegularUser;
import models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private static UserService instance;
    private User currentUser;
    private final Gson gson = new Gson();

    private UserService() {
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User login(String email, String password) {
        try {
            Map<String, String> creds = new HashMap<>();
            creds.put("email", email);
            creds.put("password", password);
            String jsonBody = gson.toJson(creds);

            String response = ApiClient.post("/auth/login", jsonBody);

            // Deserialize based on role
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            String role = jsonObject.get("role").getAsString();

            if ("ADMIN".equals(role)) {
                currentUser = gson.fromJson(response, AdminUser.class);
            } else {
                currentUser = gson.fromJson(response, RegularUser.class);
            }
            return currentUser;
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    public void logout() {
        currentUser = null;
    }

    public User register(String email, String password, String fullName) {
        try {
            // Register as Regular User by default
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("password", password);
            userData.put("fullName", fullName);
            userData.put("role", "REGULAR");

            String jsonBody = gson.toJson(userData);
            String response = ApiClient.post("/auth/register", jsonBody);

            return gson.fromJson(response, RegularUser.class);
        } catch (Exception e) {
            System.err.println("Register failed: " + e.getMessage());
            return null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
