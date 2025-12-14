package asteria.services.ai;

import asteria.services.bridge.StockCalculationSuite;
import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;

import java.util.List;

public class AzureOpenAI implements MessageSender {
    private final ChatCompletionService chatService;
    private final ChatHistory chatHistory;
    private final ChatHistoryManager chatHistoryManager;
    private final Kernel kernel;
    public AzureOpenAI(ChatHistory chatHistory, ChatHistoryManager chatHistoryManager) {
        this.chatHistory = chatHistory;
        this.chatHistoryManager = chatHistoryManager;

        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential("40MRj0fd7wtdSWOjvOGfc1R1nSunaBPJ6pUAjCWrQUMaUgV4onVzJQQJ99BFACfhMk5XJ3w3AAAAACOGPLIF"))
                .endpoint("https://sebas-mbs1z6xr-swedencentral.openai.azure.com/")
                .buildAsyncClient();

        this.chatService = OpenAIChatCompletion.builder()
                .withModelId("gpt-5.1-chat")
                .withOpenAIAsyncClient(client)
                .build();

        this.kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatService)
                .build();

        this.chatHistory.addSystemMessage("You are Asteria, the user's colleague tasked with helping the user with their task, and their stock advisor.");

        List<ChatHistoryManager.ChatMessage> previous = chatHistoryManager.loadHistory();
        for (ChatHistoryManager.ChatMessage m : previous) {
            if (m.role().equalsIgnoreCase("user")) {
                this.chatHistory.addUserMessage(m.message());
            } else if (m.role().equalsIgnoreCase("assistant")) {
                this.chatHistory.addAssistantMessage(m.message());
            }
        }
    }

    @Override
    public String sendMessage(String userInput) {
        chatHistory.addUserMessage(userInput);
        chatHistoryManager.appendMessage("user", userInput);

        try {
            List<ChatMessageContent<?>> results = chatService
                    .getChatMessageContentsAsync(chatHistory, kernel, null)
                    .block();

            String aiReply = "";
            if (results != null && !results.isEmpty()) {
                aiReply = results.get(0).getContent();
            }

            chatHistory.addAssistantMessage(aiReply);
            chatHistoryManager.appendMessage("assistant", aiReply);

            return aiReply;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}