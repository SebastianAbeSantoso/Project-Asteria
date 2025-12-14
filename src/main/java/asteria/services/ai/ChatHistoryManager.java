package asteria.services.ai;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryManager {
    private final String filePath;

    public record ChatMessage(String role, String message) {}

    public ChatHistoryManager(String filePath) {
        this.filePath = filePath;
    }

    public List<ChatMessage> loadHistory() {
        List<ChatMessage> history = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) return history;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);

                if (parts.length == 2) {
                    String role = parts[0];
                    String message = parts[1].replace("<nl>", "\n");
                    history.add(new ChatMessage(role, message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public void appendMessage(String role, String message) {
        String safeMessage = message.replace("\n", "<nl>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(role + "|" + safeMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearHistory() {
        new File(filePath).delete();
    }
}