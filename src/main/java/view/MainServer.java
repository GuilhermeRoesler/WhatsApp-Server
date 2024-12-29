package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import controller.UserDAO;
import model.ContactVerificationRequest;
import model.Message;
import model.User;

@SpringBootApplication
@RestController
public class MainServer {
    private static ConfigurableApplicationContext applicationContext;

    private static UserDAO userDAO;

    private static DarkServerGUI serverGUI;

    private static List<User> userData = new ArrayList<>();
    private static Map<String, List<Message>> userMessages = new HashMap<>();
    private static Map<String, List<String>> userContacts = new HashMap<>();

    public static void main(String[] args) {
        userDAO = new UserDAO();
        applicationContext = SpringApplication.run(MainServer.class, args);
    }

    public static void setServerGUI(DarkServerGUI gui) {
        serverGUI = gui;
    }

    public static void stopServer() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    @PostMapping("/api/whatsapp/auth/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("User cannot be null");
        }
        userDAO.insert(user);
        if (serverGUI != null) {
            serverGUI.logMessage("New user registered: " + userDAO.getUserByPhone(user.getPhone()).getName() + " ("
                    + user.getPhone() + ")");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/whatsapp/auth/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User loggedUser = userDAO.login(user);
        if (serverGUI != null && loggedUser != null)
            serverGUI.logMessage("User logged in: " + loggedUser.getName() + " (" + loggedUser.getPhone() + ")");
        if (serverGUI != null && loggedUser == null)
            serverGUI.logMessage("Login failed: User does not exist in database (try registration)");

        return loggedUser != null ? ResponseEntity.ok(loggedUser) : ResponseEntity.notFound().build();
    }

    @PostMapping("/api/whatsapp/auth/verifyContact")
    public ResponseEntity<User> verifyContact(@RequestBody ContactVerificationRequest request) {
        if (userDAO.existsInDatabase(request.getContactPhone())) {
            if (serverGUI != null)
                serverGUI.logMessage("Contact verified: " + userDAO.getUserByPhone(request.getContactPhone()).getName()
                        + " (" + request.getContactPhone() + ")");
            userContacts.computeIfAbsent(request.getCurrentUserPhone(), k -> new ArrayList<>())
                    .add(request.getContactPhone());
            return ResponseEntity.ok(userDAO.getUserByPhone(request.getContactPhone()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/whatsapp/messages")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        String conversationId = message.getConversationId();
        System.out.println("conversationId: " + conversationId);
        userMessages.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/api/whatsapp/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam(required = false) Long since,
            @RequestParam String sender,
            @RequestParam String receiver) {

        String conversationId = Message.generateConversationId(sender, receiver);
        List<Message> conversationMessages = userMessages.getOrDefault(conversationId, new ArrayList<>());

        if (since != null) {
            // Only return messages newer than 'since' timestamp
            return ResponseEntity.ok(conversationMessages.stream()
                    .filter(m -> m.getTimestamp() > since)
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok(conversationMessages);
    }

    @GetMapping("/api/whatsapp/contacts")
    public ResponseEntity<Map<String, List<String>>> getContacts() {
        return ResponseEntity.ok(userContacts);
    }

    @GetMapping("/api/whatsapp/users")
    public List<User> getUsers() {
        return userData;
    }

    @GetMapping("/api/whatsapp/users/{id}")
    public Object getUserById(@PathVariable int id) {
        return userData.get(id);
    }
}