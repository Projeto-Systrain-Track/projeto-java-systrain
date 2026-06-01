package school.sptech.dto.jira;

public class JiraIssueDTO {
    public Campos fields;

    public JiraIssueDTO(String projetoKey, String tipoIssue, String titulo, String descricaoCompleta) {
        this.fields = new Campos();
        this.fields.project = new Projeto(projetoKey);
        this.fields.issuetype = new TipoChamado(tipoIssue);
        this.fields.summary = titulo;
        this.fields.description = descricaoCompleta;
    }

    public static class Campos {
        public Projeto project;
        public TipoChamado issuetype;
        public String summary;
        public String description;
    }

    public static class Projeto {
        public String key;
        public Projeto(String key) { this.key = key; }
    }

    public static class TipoChamado {
        public String name;
        public TipoChamado(String name) { this.name = name; }
    }
}