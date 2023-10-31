import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.KeySpec;

public class FileEncryptionApp {
    private JFrame frame;
    private JTextField passwordField;
    private JTextArea logTextArea;

    public FileEncryptionApp() {
        frame = new JFrame("File Encryption/Decryption");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel fileLabel = new JLabel("Select File:");
        JButton fileChooserButton = new JButton("Choose File");
        JTextField fileTextField = new JTextField(20);
        fileTextField.setEditable(false);

        JLabel passwordLabel = new JLabel("Enter Password:");
        passwordField = new JPasswordField(20);

        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");

        logTextArea = new JTextArea(10, 30);
        logTextArea.setEditable(false);

        fileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileTextField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = fileTextField.getText();
                String password = passwordField.getText();
                if (!filePath.isEmpty() && !password.isEmpty()) {
                    try {
                        long startTime = System.currentTimeMillis();
                        encryptFile(filePath, password);
                        long endTime = System.currentTimeMillis();
                        logTextArea.append("File encrypted successfully.\n");
                        logTextArea.append("Time taken: " + (endTime - startTime) + " milliseconds\n");
                        logTextArea.append("Memory used: " + getMemoryUsage() + " MB\n");
                    } catch (Exception ex) {
                        logTextArea.append("Encryption failed: " + ex.getMessage() + "\n");
                    }
                } else {
                    logTextArea.append("Please select a file and enter a password.\n");
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = fileTextField.getText();
                String password = passwordField.getText();
                if (!filePath.isEmpty() && !password.isEmpty()) {
                    try {
                        long startTime = System.currentTimeMillis();
                        decryptFile(filePath, password);
                        long endTime = System.currentTimeMillis();
                        logTextArea.append("File decrypted successfully.\n");
                        logTextArea.append("Time taken: " + (endTime - startTime) + " milliseconds\n");
                        logTextArea.append("Memory used: " + getMemoryUsage() + " MB\n");
                    } catch (Exception ex) {
                        logTextArea.append("Decryption failed: " + ex.getMessage() + "\n");
                    }
                } else {
                    logTextArea.append("Please select a file and enter a password.\n");
                }
            }
        });

        panel.add(fileLabel);
        panel.add(fileChooserButton);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(fileTextField);
        panel.add(encryptButton);
        panel.add(decryptButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(logTextArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void encryptFile(String filePath, String password) throws Exception {
        File inputFile = new File(filePath);
        File encryptedFile = new File(filePath + ".encrypted");

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        int iterations = 65536;
        int keyLength = 256;

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);
        byte[] encryptedBytes = cipher.doFinal(inputBytes);

        try (FileOutputStream outputStream = new FileOutputStream(encryptedFile)) {
            outputStream.write(salt);
            outputStream.write(iv);
            outputStream.write(encryptedBytes);
        }

        inputStream.close();
    }

    private void decryptFile(String filePath, String password) throws Exception {
        File encryptedFile = new File(filePath);
        File decryptedFile = new File(filePath.replace(".encrypted", ""));

        FileInputStream inputStream = new FileInputStream(encryptedFile);
        byte[] salt = new byte[16];
        inputStream.read(salt);
        byte[] iv = new byte[16];
        inputStream.read(iv);
        byte[] encryptedBytes = new byte[inputStream.available()];
        inputStream.read(encryptedBytes);
        inputStream.close();

        int iterations = 65536;
        int keyLength = 256;

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        try (FileOutputStream outputStream = new FileOutputStream(decryptedFile)) {
            outputStream.write(decryptedBytes);
        }
    }

    private double getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        return usedMemory / (1024.0 * 1024.0); // Convert to MB
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FileEncryptionApp();
            }
        });
    }
}

