package slack.app;

import slack.config.Slack;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;



public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        String content = new String(Files.readAllBytes(Paths.get("src/main/element_0.json")));

        JSONObject root = new JSONObject(content);

        int idMaquina = root.getInt("id_maquina");
        String empresa = root.getString("nome_empresa");
        String mac = root.getString("endereco_mac");

        JSONArray metricasArray = root.getJSONArray("ultimas_metricas");

        JSONArray blocks = new JSONArray();

        blocks.put(new JSONObject()
                .put("type", "header")
                .put("text", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", "📊 Alertas: " + root.getString("nome_empresa"))
                        .put("emoji", true)));

        for (int i = 0; i < metricasArray.length(); i++) {

            JSONObject m = metricasArray.getJSONObject(i);

            double score = m.getDouble("score");
            String criticidade = m.getString("criticidade");
            double cpu = m.getDouble("percentual_uso_cpu");
            double ram = m.getDouble("percentual_uso_ram");
            double disco = m.getDouble("percentual_uso_disco");
            String dataHora = m.getString("data_hora");


            blocks.put(new JSONObject()
                    .put("type", "section")
                    .put("fields", new JSONArray()
                            .put(new JSONObject().put("type", "mrkdwn").put("text", "*Score:* " + score))
                            .put(new JSONObject().put("type", "mrkdwn").put("text", "*Criticidade:* " + criticidade))
                            .put(new JSONObject().put("type", "mrkdwn").put("text", "*CPU:* " + cpu + "%"))
                            .put(new JSONObject().put("type", "mrkdwn").put("text", "*RAM:* " + ram + "%"))
                            .put(new JSONObject().put("type", "mrkdwn").put("text", "*Disco:* " + disco + "%"))
                    ));


            blocks.put(new JSONObject()
                    .put("type", "context")
                    .put("elements", new JSONArray()
                            .put(new JSONObject()
                                    .put("type", "mrkdwn")
                                    .put("text", "📅 " + dataHora))));


            blocks.put(new JSONObject().put("type", "divider"));
        }

        JSONObject json = new JSONObject();
        json.put("blocks", blocks);

        Slack.sendMessage(json);

    }
}
