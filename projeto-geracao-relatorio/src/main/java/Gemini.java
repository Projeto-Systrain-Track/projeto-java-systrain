import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;

public class Gemini {
    private Client client;
    private final String modelo = "gemini-2.5-flash";



    public Gemini() {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GEMINI_API_KEY");
        this.client = Client.builder().apiKey(apiKey).build();
    }

    public String gerarAnaliseRelatorio(String prompt) {
        try{
            GenerateContentResponse resposta = client.models.generateContent(modelo, prompt, null);
            return resposta.text();
        } catch (Exception e) {
            System.out.println("Erro ao gerar análise com GEMINI: " + e.getMessage());;
        }
        return null;
    }
}


