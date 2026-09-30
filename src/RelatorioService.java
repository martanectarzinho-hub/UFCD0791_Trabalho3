import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável por compilar e gerar o relatório final discriminado de
 * processamento de lotes de movimentos bancários em ficheiro de texto.
 * 
 * @author Carlos Marques
 * @version 1.0
 */
public class RelatorioService {

    private static final String LINHA_DIVISORA = "================================================================================";
    private static final String LINHA_SUBDIVISORA = "--------------------------------------------------------------------------------";

    /**
     * Escreve o relatório detalhado de execução num ficheiro de destino.
     * Garante o fecho determinístico do descritor de ficheiro no bloco finally.
     *
     * @param processador      Instância do motor com as estatísticas acumuladas.
     * @param caminhoRelatorio Caminho relativo ou absoluto onde o relatório será gravado.
     */
    public void gerarRelatorio(ProcessadorMovimentos processador, String caminhoRelatorio) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(caminhoRelatorio));

            String dataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            writer.write(LINHA_DIVISORA);
            writer.newLine();
            writer.write("RELATÓRIO DE PROCESSAMENTO DE MOVIMENTOS BANCÁRIOS");
            writer.newLine();
            writer.write("Data de Execução: " + dataHora);
            writer.newLine();
            writer.write(LINHA_DIVISORA);
            writer.newLine();
            writer.newLine();

            // 1. Resumo Global
            writer.write("1. RESUMO GLOBAL DO LOTE");
            writer.newLine();
            writer.write(LINHA_SUBDIVISORA);
            writer.newLine();
            writer.write(String.format("Total de Linhas Lidas:              %d%n", processador.getTotalLinhasLidas()));
            writer.write(String.format("Registos Validados com Sucesso:     %d%n", processador.getTotalRegistosValidos()));
            writer.write(String.format("Registos Inválidos / Rejeitados:     %d%n", processador.getTotalRegistosInvalidos()));
            writer.write(String.format("Montante Total Processado (Válido): %.2f EUR%n", processador.getMontanteTotalValido()));
            writer.newLine();

            // 2. Detalhe por Tipo de Operação
            writer.write("2. DESCRIMINAÇÃO POR TIPO DE OPERAÇÃO");
            writer.newLine();
            writer.write(LINHA_SUBDIVISORA);
            writer.newLine();

            Map<String, Double> montantes = processador.getMontantesPorTipo();
            Map<String, Integer> contagens = processador.getContagensPorTipo();

            if (montantes.isEmpty()) {
                writer.write("Nenhuma operação válida foi registada.\n");
            } else {
                writer.write(String.format("%-25s | %-12s | %-18s%n", "TIPO DE OPERAÇÃO", "QUANTIDADE", "MONTANTE TOTAL"));
                writer.write(LINHA_SUBDIVISORA);
                writer.newLine();
                for (Map.Entry<String, Double> entry : montantes.entrySet()) {
                    String tipo = entry.getKey();
                    double montante = entry.getValue();
                    int qtd = contagens.getOrDefault(tipo, 0);
                    writer.write(String.format("%-25s | %-12d | %15.2f EUR%n", tipo, qtd, montante));
                }
            }
            writer.newLine();

            // 3. Registo de Anomalias e Erros
            writer.write("3. REGISTO DISCRIMINADO DE ANOMALIAS E ERROS");
            writer.newLine();
            writer.write(LINHA_SUBDIVISORA);
            writer.newLine();

            List<ProcessadorMovimentos.RegistoAnomalia> anomalias = processador.getListaAnomalias();
            if (anomalias.isEmpty()) {
                writer.write("Sem anomalias registadas durante o processamento do lote.\n");
            } else {
                for (ProcessadorMovimentos.RegistoAnomalia anomalia : anomalias) {
                    writer.write(anomalia.toString());
                    writer.newLine();
                }
            }

            writer.newLine();
            writer.write(LINHA_DIVISORA);
            writer.newLine();
            writer.write("FIM DO RELATÓRIO");
            writer.newLine();

            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro crítico de I/O ao gravar o ficheiro de relatório: " + e.getMessage());
        } finally {
            fecharEscritorSilenciosamente(writer);
        }
    }

    /**
     * Encerra o descritor de escrita na cláusula finally com verificação de não-nulidade
     * e captura interna de potenciais {@link IOException}.
     *
     * @param writer Instância do BufferedWriter a fechar.
     */
    private void fecharEscritorSilenciosamente(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o descritor de ficheiro de relatório: " + e.getMessage());
            }
        }
    }
}
