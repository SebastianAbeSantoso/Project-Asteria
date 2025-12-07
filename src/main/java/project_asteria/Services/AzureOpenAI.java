package project_asteria.Services;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;

import java.util.List;

public class AzureOpenAI implements SendMessage, ShowMessage {
    private final OpenAIAsyncClient client;
    private final ChatCompletionService chatService;
    private final Kernel kernel;
    private String reply;
    private String userInput;

    public AzureOpenAI() {
        this.client = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential("40MRj0fd7wtdSWOjvOGfc1R1nSunaBPJ6pUAjCWrQUMaUgV4onVzJQQJ99BFACfhMk5XJ3w3AAAAACOGPLIF"))
                .endpoint("https://sebas-mbs1z6xr-swedencentral.openai.azure.com/")
                .buildAsyncClient();

        this.chatService = OpenAIChatCompletion.builder()
                .withModelId("gpt-4.1")
                .withOpenAIAsyncClient(client)
                .build();

        this.kernel = Kernel.builder()
                .withAIService(ChatCompletionService.class, chatService)
                .build();

    }


    public String sendMessage (String userInput){
        List<ChatMessageContent<?>> results = chatService
                .getChatMessageContentsAsync(userInput, kernel, new InvocationContext
                        .Builder()
                        .build())
                .block();
        this.userInput = userInput;
        reply = results.get(0).getContent();
        return reply;
    }

        public void showMessage () {
            System.out.println("AI: " + reply);
    }
}