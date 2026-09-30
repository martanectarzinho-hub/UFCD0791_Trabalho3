/**
 * Classe principal da aplicação de consola para processamento de lotes de
 * movimentos bancários com tratamento robusto de exceções e geração de relatórios.
 * 
 * @author Carlos Marques
 * @version 1.0
 */
public class Principal {

    /** Número mínimo de argumentos exigidos na linha de comandos. */
    public static final int PARAMETROS_MINIMOS = 2;

    /** Índice do argumento correspondente ao ficheiro de entrada. */
    public static final int INDEX_ARG_ENTRADA = 0;

    /** Índice do argumento correspondente ao ficheiro de relatório. */
    public static final int INDEX_ARG_RELATORIO = 1;

    /**
     * Ponto de entrada da aplicação.
     *
     * @param args Argumentos da linha de comandos:
     *             args[0] = caminho do ficheiro de movimentos de entrada,
     *             args[1] = caminho do ficheiro de relatório de saída.
     */
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   MOTOR DE PROCESSAMENTO DE MOVIMENTOS BANCÁRIOS - UFCD 0791   ");
        System.out.println("=================================================================");

        if (args.length < PARAMETROS_MINIMOS) {
            System.err.println("\n[ERRO] Parâmetros de linha de comandos insuficientes!");
            System.out.println("Uso correto:");
            System.out.println("  java -cp bin Principal <caminho_entrada> <caminho_relatorio>");
            System.out.println("\nExemplo:");
            System.out.println("  java -cp bin Principal dados/movimentos.txt relatorios/relatorio.txt\n");
            return;
        }

        String caminhoEntrada = args[INDEX_ARG_ENTRADA];
        String caminhoRelatorio = args[INDEX_ARG_RELATORIO];

        System.out.println("Ficheiro de Entrada:   " + caminhoEntrada);
        System.out.println("Ficheiro de Relatório: " + caminhoRelatorio);
        System.out.println("-----------------------------------------------------------------");
        System.out.println("A iniciar o processamento do lote...");

        ProcessadorMovimentos processador = new ProcessadorMovimentos();
        processador.processarFicheiro(caminhoEntrada);

        System.out.println("Processamento do lote concluído.");
        System.out.printf("  -> Linhas Lidas:       %d%n", processador.getTotalLinhasLidas());
        System.out.printf("  -> Registos Válidos:   %d%n", processador.getTotalRegistosValidos());
        System.out.printf("  -> Anomalias / Erros:  %d%n", processador.getTotalRegistosInvalidos());
        System.out.printf("  -> Montante Total:     %.2f EUR%n", processador.getMontanteTotalValido());

        System.out.println("-----------------------------------------------------------------");
        System.out.println("A gerar o relatório final discriminado...");

        RelatorioService relatorioService = new RelatorioService();
        relatorioService.gerarRelatorio(processador, caminhoRelatorio);

        System.out.println("[SUCESSO] Operação concluída. Relatório gravado em: " + caminhoRelatorio);
        System.out.println("=================================================================\n");
    }
}
