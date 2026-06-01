package school.sptech.dto.tela;

public class IncidenteTelaDTO {
    public DetalhesTela detalhes_tela;
    public MetricasFormatadas metricas_formatadas;
    public DadosBrutos dados_brutos;

    public static class DetalhesTela {
        public String statusSLA = "atencao";
        public String titulo;
        public String linha;
        public String nivel;
        public String horario;
        public String descricao;
        public String responsavel = "NÃO ATRIBUIDO"; //atualizado com o dado do Jira dps
        public String status = "ABERTO";
        public String componente;
        public String tipo;
        public Clima clima;
    }

    public static class Clima {
        public Double temperatura;
        public String condicao;
        public String vento_kmh;
        public String icone;
    }

    public static class MetricasFormatadas {
        public String cpu;
        public String ram;
        public String disco;
        public String latencia;
        public String disparo;
    }

    public static class DadosBrutos {
        public Integer id_rbc;
        public String nome_rbc;
        public Double score_saude_original;
        public String data_hora_completa;
        public Integer id_empresa;
        public String nome_empresa;
        public String jiraKey; // Guarda o ID do card do Jira para a tela usar
    }
}