import excecoes.FormatoLinhaInvalidaException;
import excecoes.ValorMovimentoInvalidoException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor de processamento de lotes de movimentos bancários. Orquestra a leitura
 * resiliente de ficheiros de entrada, converte e valida registos atómicos e
 * mantém o estado global de estatísticas e registo de anomalias.
 * 
 * @author Carlos Marques
 * @version 1.0
 */
public class ProcessadorMovimentos {

    /**
     * Classe utilitária imutável para registar anomalias detetadas durante o lote.
     */
    public static class RegistoAnomalia {
        private final int numeroLinha;
        private final String conteudoLinha;
        private final String motivoRejeicao;

        public RegistoAnomalia(int numeroLinha, String conteudoLinha, String motivoRejeicao) {
            this.numeroLinha = numeroLinha;
            this.conteudoLinha = conteudoLinha != null ? conteudoLinha : "";
            this.motivoRejeicao = motivoRejeicao != null ? motivoRejeicao : "";
        }

        public int getNumeroLinha() {
            return numeroLinha;
        }

        public String getConteudoLinha() {
            return conteudoLinha;
        }

        public String getMotivoRejeicao() {
            return motivoRejeicao;
        }

        @Override
        public String toString() {
            if (numeroLinha > 0) {
                return String.format("Linha %d | Registo: [%s] | Motivo: %s",
                        numeroLinha, conteudoLinha, motivoRejeicao);
            }
            return String.format("Geral | %s", motivoRejeicao);
        }
    }

    private int totalLinhasLidas = 0;
    private int totalRegistosValidos = 0;
    private int totalRegistosInvalidos = 0;
    private double montanteTotalValido = 0.0;

    private final Map<String, Double> montantesPorTipo = new HashMap<>();
    private final Map<String, Integer> contagensPorTipo = new HashMap<>();
    private final List<RegistoAnomalia> listaAnomalias = new ArrayList<>();

    /**
     * Métodos intermédio de orquestração que delega o parsing da linha ao modelo
     * e declara explicitamente a propagação contratual de exceções checked.
     *
     * @param linha Conteúdo textual da linha a converter e validar.
     * @return Objeto {@link LinhaMovimento} devidamente construído e validado.
     * @throws FormatoLinhaInvalidaException    Se a estrutura da linha for inválida (checked).
     * @throws ValorMovimentoInvalidoException Se o montante monetário for inválido (unchecked).
     */
    public LinhaMovimento validarEConverter(String linha)
            throws FormatoLinhaInvalidaException, ValorMovimentoInvalidoException {
        return LinhaMovimento.parse(linha);
    }

    /**
     * Processa um ficheiro de lote de movimentos bancários de forma resiliente.
     * Garante o fecho determinístico de recursos I/O na cláusula finally e a continuidade
     * da iteração perante registos corrompidos.
     *
     * @param caminhoFicheiro Caminho relativo ou absoluto do ficheiro de entrada.
     */
    public void processarFicheiro(String caminhoFicheiro) {
        BufferedReader reader = null;
        try {
            File ficheiro = new File(caminhoFicheiro);
            if (!ficheiro.exists()) {
                registarAnomalia(0, "<FICHEIRO_INEXISTENTE>",
                        "Erro: O ficheiro de entrada '" + caminhoFicheiro + "' não foi encontrado no sistema de ficheiros.");
                return;
            }

            reader = new BufferedReader(new FileReader(ficheiro));
            String linha;
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                totalLinhasLidas++;

                try {
                    LinhaMovimento movimento = validarEConverter(linha);
                    acumularMovimentoValido(movimento);
                } catch (FormatoLinhaInvalidaException e) {
                    registarAnomalia(numeroLinha, linha, "Formato Estrutural Inválido - " + e.getMessage());
                } catch (ValorMovimentoInvalidoException e) {
                    registarAnomalia(numeroLinha, linha, "Dados Monetários Inválidos - " + e.getMessage());
                } catch (Exception e) {
                    registarAnomalia(numeroLinha, linha, "Erro Inesperado no Processamento - " + e.getMessage());
                }
            }

            if (totalLinhasLidas == 0) {
                registarAnomalia(0, "<FICHEIRO_VAZIO>", "Aviso: O ficheiro de entrada está totalmente vazio.");
            }

        } catch (IOException e) {
            registarAnomalia(0, "<ERRO_LEITURA_IO>", "Falha crítica de I/O ao ler o ficheiro: " + e.getMessage());
        } finally {
            fecharLeitorSilenciosamente(reader);
        }
    }

    /**
     * Encerra com segurança o descritor de leitura na cláusula finally,
     * executando null-check explícito e captura interna de potenciais {@link IOException}.
     *
     * @param reader Instância do BufferedReader a fechar.
     */
    private void fecharLeitorSilenciosamente(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o descritor de ficheiro de entrada: " + e.getMessage());
            }
        }
    }

    /**
     * Adiciona os dados de um movimento válido às métricas agregadas globais.
     *
     * @param movimento Objeto com os dados do movimento.
     */
    private void acumularMovimentoValido(LinhaMovimento movimento) {
        totalRegistosValidos++;
        montanteTotalValido += movimento.getValor();

        String tipo = movimento.getTipoOperacao().toUpperCase();
        montantesPorTipo.put(tipo, montantesPorTipo.getOrDefault(tipo, 0.0) + movimento.getValor());
        contagensPorTipo.put(tipo, contagensPorTipo.getOrDefault(tipo, 0) + 1);
    }

    /**
     * Regista uma anomalia capturada e incrementa o contador de registos inválidos.
     *
     * @param numeroLinha Numeração da linha onde ocorreu o erro.
     * @param conteudo    Conteúdo textual bruto da linha.
     * @param motivo      Motivo ou detalhe da rejeição.
     */
    private void registarAnomalia(int numeroLinha, String conteudo, String motivo) {
        if (numeroLinha > 0) {
            totalRegistosInvalidos++;
        }
        listaAnomalias.add(new RegistoAnomalia(numeroLinha, conteudo, motivo));
    }

    public int getTotalLinhasLidas() {
        return totalLinhasLidas;
    }

    public int getTotalRegistosValidos() {
        return totalRegistosValidos;
    }

    public int getTotalRegistosInvalidos() {
        return totalRegistosInvalidos;
    }

    public double getMontanteTotalValido() {
        return montanteTotalValido;
    }

    public Map<String, Double> getMontantesPorTipo() {
        return Collections.unmodifiableMap(montantesPorTipo);
    }

    public Map<String, Integer> getContagensPorTipo() {
        return Collections.unmodifiableMap(contagensPorTipo);
    }

    public List<RegistoAnomalia> getListaAnomalias() {
        return Collections.unmodifiableList(listaAnomalias);
    }
}
