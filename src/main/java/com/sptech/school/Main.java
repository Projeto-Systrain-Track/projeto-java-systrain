package com.sptech.school;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs do sistema em tempo real");

        String espaco = "       ";
        String divisão = "\n \n -------------------------------------------------------------------------------------------------------------------- \n \n";
        String sublinhado = espaco + "=============================================================================" + espaco;


        while (true){
            System.out.println(sublinhado);
            System.out.println(espaco + "| Data e Hora: " + getData());
            System.out.println(espaco + "| Conteudo: " + getMensagens());
            System.out.println(sublinhado);
            System.out.println(divisão);
            try {
                Thread.sleep(getIntervalo());
            } catch (InterruptedException e) {
                System.out.println("Thread interrompida");
            }
        }

    }

    public static String getData(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        LocalDateTime dataHora= LocalDateTime.now();
        String formatado = dataHora.format(formatter);
        return (formatado);
    }

    public static String getMensagens() {
        String espaco = "       ";
        List<String> mensagens = new ArrayList<>(Arrays.asList(
                ("Chamado aberto em jira! Codigo do chamado: " + getNumeroAleatorio() + "\n"  +  espaco +"| Empresa: " + getEmpresa()),
                "Conexão com o banco de dados bem sucedida!",
                "Requisição com o banco de dados realizada com sucesso!",
                "Instabilidade devido a rede do sistema",
                "Conexão com o Jira bem sucedida",
                "Conexão com o Slack bem sucedida",

                "Usuário autenticado com sucesso no sistema",
                "Token de autenticação gerado com sucesso",
                "Falha ao validar token de autenticação",
                "Sessão do usuário expirada",
                "Sessão do usuário renovada com sucesso",

                "Nova requisição HTTP recebida pelo servidor",
                "Resposta HTTP enviada ao cliente",
                "Timeout na requisição HTTP",
                "Requisição cancelada pelo cliente",

                "Tabela atualizada no banco de dados",
                "Registro inserido com sucesso no banco",
                "Registro removido do banco de dados",
                "Registro atualizado no banco de dados",
                "Falha ao executar query no banco de dados",

                "Backup do banco de dados iniciado",
                "Backup do banco de dados concluído",
                "Falha ao executar backup do banco de dados",
                "Restaurando backup do banco de dados",

                "Inicializando serviços do sistema",
                "Serviços do sistema inicializados com sucesso",
                "Encerrando serviços do sistema",
                "Sistema desligado corretamente",

                "Processo agendado iniciado",
                "Processo agendado finalizado",
                "Erro ao executar processo agendado",

                "Memória do sistema acima do limite esperado",
                "Uso de CPU elevado detectado",
                "Recursos do sistema normalizados",

                "Nova conexão estabelecida com servidor externo",
                "Conexão com servidor externo encerrada",
                "Falha ao conectar com servidor externo",

                "Arquivo de log rotacionado com sucesso",
                "Arquivo temporário criado",
                "Arquivo temporário removido",

                "Permissão de acesso negada para o usuário",
                "Permissão de acesso concedida",
                "Tentativa de acesso não autorizado detectada",

                "Integração com API externa realizada com sucesso",
                "Falha na comunicação com API externa",
                "Resposta inválida recebida da API externa",

                "Fila de processamento iniciada",
                "Item adicionado à fila de processamento",
                "Item removido da fila de processamento",
                "Fila de processamento vazia",

                "Inicializando módulo de autenticação",
                "Módulo de autenticação carregado com sucesso",
                "Inicializando módulo de cache",
                "Cache carregado na memória",
                "Cache invalidado devido a atualização de dados",
                "Falha ao atualizar cache do sistema",

                "Conectando ao servidor de autenticação",
                "Servidor de autenticação respondeu com sucesso",
                "Falha ao conectar ao servidor de autenticação",

                "Inicializando serviço de notificações",
                "Serviço de notificações iniciado",
                "Notificação enviada ao usuário",
                "Falha ao enviar notificação ao usuário",

                "Nova requisição recebida pela API",
                "Processando requisição da API",
                "Resposta da API enviada ao cliente",
                "Erro interno no processamento da API",

                "Inicializando fila de mensagens",
                "Mensagem adicionada à fila",
                "Consumidor da fila iniciou processamento",
                "Mensagem processada com sucesso",
                "Erro ao processar mensagem da fila",

                "Conexão estabelecida com servidor Redis",
                "Falha ao conectar ao servidor Redis",
                "Cache Redis atualizado",
                "Entrada removida do cache Redis",

                "Conectando ao serviço de armazenamento",
                "Arquivo enviado ao serviço de armazenamento",
                "Falha ao enviar arquivo ao armazenamento",
                "Arquivo recuperado com sucesso do armazenamento",

                "Inicializando módulo de segurança",
                "Verificação de segurança concluída",
                "Alerta de segurança registrado no sistema",
                "Tentativa suspeita de login detectada",

                "Deploy da aplicação iniciado",
                "Deploy da aplicação concluído com sucesso",
                "Falha durante o processo de deploy",
                "Rollback de deploy iniciado",
                "Rollback concluído com sucesso",

                "Monitoramento do sistema iniciado",
                "Monitoramento detectou latência elevada",
                "Latência do sistema voltou ao normal",

                "Conectando ao serviço de métricas",
                "Métricas enviadas ao servidor de monitoramento",
                "Falha ao enviar métricas para monitoramento",

                "Thread de processamento iniciada",
                "Thread finalizada corretamente",
                "Erro inesperado na thread de processamento",

                "Inicializando serviço de logs",
                "Serviço de logs iniciado",
                "Falha ao gravar entrada no arquivo de log",

                "Atualização de configuração detectada",
                "Configuração recarregada com sucesso",
                "Erro ao recarregar configuração do sistema",

                "Sincronização com servidor remoto iniciada",
                "Sincronização concluída com sucesso",
                "Erro durante sincronização com servidor remoto"
        ));

        int Random= (int) Math.floor(Math.random() * mensagens.toArray().length);
            String mensagemAleatoria = mensagens.get(Random);
            return (mensagemAleatoria);

    }

    public static int getNumeroAleatorio(){
        int random = (int) Math.floor(Math.random()* 999999999);
        return (random);
    }

    public static int getIntervalo(){
        return (int)Math.floor(Math.random() * 10000);
    }

    public static String getEmpresa(){
        List<String> listaEmpresas = new ArrayList<>(Arrays.asList(
                "CPTM",
                "Via Modalidade!",
                "Rumo Logistica",
                "MRS Logística",
                "VLI Logística",
                "Vale",
                "EFVM",
                "EFC",
                "TLSA"
        ));
        int random = (int) Math.floor(Math.random() * listaEmpresas.toArray().length);
        return listaEmpresas.get(random);
    }
}
