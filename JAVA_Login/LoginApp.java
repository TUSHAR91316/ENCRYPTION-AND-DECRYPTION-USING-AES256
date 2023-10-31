import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoginApp extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private List<User> users;

    private static final String CREDENTIALS_FILE = "credentials.txt";

    public LoginApp() {
        setTitle("Login Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        users = new ArrayList<>();

        // Load existing users from the file, if available
        loadUsers();

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        messageLabel = new JLabel();

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);
        panel.add(new JLabel());
        panel.add(registerButton);
        panel.add(messageLabel);

        getContentPane().add(panel);
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        boolean loggedIn = false;
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedIn = true;
                break;
            }
        }

        if (loggedIn) {
            messageLabel.setText("Login successful!");
        } else {
            messageLabel.setText("Login failed. Please check your username and password.");
        }
    }

    private void performRegistration() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        users.add(new User(newUsername, newPassword));
        saveUsers(); // Save the updated user list to the file
        messageLabel.setText("Registration successful!");
    }

    private void loadUsers() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
            reader.close();
        } catch (IOException e) {
            // Handle file reading errors
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE));
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            // Handle file writing errors
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginApp app = new LoginApp();
                app.setVisible(true);
            }
        });
    }
}

class User {
    private String username;
    private String password;

    public User(String username, String password) {
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
