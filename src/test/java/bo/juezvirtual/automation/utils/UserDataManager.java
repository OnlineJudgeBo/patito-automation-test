package bo.juezvirtual.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton manager to parse and load QA user credentials from users.json,
 * with transparent fallback support for BrowserConfig properties and environment variables.
 */
public final class UserDataManager {
    private static final Map<String, UserCredentials> USERS = new HashMap<>();

    static {
        loadUserData();
    }

    private UserDataManager() {
    }

    private static void loadUserData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Read from classpath resource
            InputStream is = UserDataManager.class.getClassLoader().getResourceAsStream("testdata/users.json");
            if (is == null) {
                // Fallback to relative file path search
                Path path = Paths.get("src/test/resources/testdata/users.json");
                if (Files.exists(path)) {
                    is = Files.newInputStream(path);
                }
            }

            if (is != null) {
                JsonNode rootNode = mapper.readTree(is);
                rootNode.fields().forEachRemaining(entry -> {
                    String alias = entry.getKey();
                    JsonNode userNode = entry.getValue();
                    String username = userNode.get("username").asText();
                    String password = userNode.get("password").asText();
                    USERS.put(alias.toLowerCase(), new UserCredentials(username, password));
                });
            } else {
                System.err.println("Warning: users.json could not be loaded!");
            }
        } catch (Exception e) {
            System.err.println("Error loading users.json: " + e.getMessage());
        }
    }

    /**
     * Resolves credentials based on user alias.
     */
    public static UserCredentials getUser(String alias) {
        if (alias == null) {
            return null;
        }
        String key = alias.toLowerCase();
        
        // Prioritize dynamic properties/environment overrides for default profiles
        if ("administrador_qa".equals(key)) {
            String adminUser = bo.juezvirtual.automation.config.BrowserConfig.getAdminUsername();
            String adminPass = bo.juezvirtual.automation.config.BrowserConfig.getAdminPassword();
            if (adminUser != null && !adminUser.isEmpty()) {
                return new UserCredentials(adminUser, adminPass);
            }
        }
        if ("participante_qa".equals(key)) {
            String clientUser = bo.juezvirtual.automation.config.BrowserConfig.getClientUsername();
            String clientPass = bo.juezvirtual.automation.config.BrowserConfig.getClientPassword();
            if (clientUser != null && !clientUser.isEmpty()) {
                return new UserCredentials(clientUser, clientPass);
            }
        }

        return USERS.get(key);
    }

    public static final class UserCredentials {
        private final String username;
        private final String password;

        public UserCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
