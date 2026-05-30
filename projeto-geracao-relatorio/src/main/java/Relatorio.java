import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.FileOutputStream;

public class Relatorio {
    private String nomeArquivo;
    private Document relatorio;
    private final Font titulo = new Font(Font.STRIKETHRU, 18, Font.BOLD);
    private final Font subtitulo = new Font(Font.STRIKETHRU, 16, Font.BOLD);
    private final Font corpo = new Font(Font.STRIKETHRU, 14, Font.NORMAL);

    public Relatorio(String nomeArquivo) {
        this.relatorio = new Document();
        try {
            PdfWriter.getInstance(relatorio, new FileOutputStream(nomeArquivo + ".pdf"));
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao iniciar relatório: " + e.getMessage());
        }
    }

    public void abrirRelatorio(){
        relatorio.open();
    }
    public void fecharRelatorio(){
        relatorio.close();
    }



    public void adicionarCabecalho(String titulo, String subtitulo) {
        relatorio.open();
        Paragraph cabecalho = new Paragraph(titulo, this.titulo);
        cabecalho.setAlignment(Element.ALIGN_CENTER);
        cabecalho.setSpacingAfter(10f);
        relatorio.add(cabecalho);
        Paragraph cabecalho2 = new Paragraph(subtitulo, this.subtitulo);
        cabecalho2.setAlignment(Element.ALIGN_CENTER);
        cabecalho2.setSpacingAfter(10f);
        relatorio.add(cabecalho2);
        System.out.println("Cabeçalho salvo com sucesso!" + relatorio);
    }
    public void escreverCorpo(String texto){
        relatorio.open();
        Paragraph texto_corpo = new Paragraph(texto, corpo);
        texto_corpo.setAlignment(Element.ALIGN_JUSTIFIED);
        texto_corpo.setSpacingBefore(10f);
        texto_corpo.setSpacingAfter(10f);
        relatorio.add(texto_corpo);
        System.out.println("Corpo salvo com sucesso!" + relatorio);
    }


}
