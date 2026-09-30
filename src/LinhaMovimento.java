import excecoes.FormatoLinhaInvalidaException;
import excecoes.ValorMovimentoInvalidoException;

/**
 * Representa um registo individual de movimento bancário e fornece os métodos
 * de parsing e validação de dados de negócio.
 * 
 * @author Carlos Marques
 * @version 1.0
 */
public class LinhaMovimento {

    /** Delimitador padrão de campos na linha de registo. */
    public static final String DELIMITER = ";";

    /** Número exato de campos esperados por registo. */
    public static final int EXPECTED_FIELD_COUNT = 4;

    /** Índice do campo CLIENTE. */
    public static final int INDEX_CLIENTE = 0;

    /** Índice do campo TIPO_OPERACAO. */
    public static final int INDEX_TIPO_OPERACAO = 1;

    /** Índice do campo VALOR. */
    public static final int INDEX_VALOR = 2;

    /** Índice do campo DESCRICAO. */
    public static final int INDEX_DESCRICAO = 3;

    private final String cliente;
    private final String tipoOperacao;
    private final double valor;
    private final String descricao;

    /**
     * Construtor parametrizado para instanciar um registo de movimento válido.
     *
     * @param cliente      Identificador do cliente.
     * @param tipoOperacao Tipo da operação bancária (ex.: TRANSFERENCIA, DEPOSITO).
     * @param valor        Montante monetário da operação.
     * @param descricao    Descrição sumária do movimento.
     */
    public LinhaMovimento(String cliente, String tipoOperacao, double valor, String descricao) {
        this.cliente = cliente;
        this.tipoOperacao = tipoOperacao;
        this.valor = valor;
        this.descricao = descricao;
    }

    /**
     * Analisa, converte e valida uma linha de texto crua num objeto {@link LinhaMovimento}.
     *
     * @param linha Conteúdo textual da linha a analisar.
     * @return Instância de {@link LinhaMovimento} validada.
     * @throws FormatoLinhaInvalidaException    Se a linha for nula, vazia ou tiver número de campos incorreto.
     * @throws ValorMovimentoInvalidoException Se o valor for não numérico, nulo, zero ou negativo.
     */
    public static LinhaMovimento parse(String linha)
            throws FormatoLinhaInvalidaException, ValorMovimentoInvalidoException {

        if (linha == null || linha.trim().isEmpty()) {
            throw new FormatoLinhaInvalidaException("Linha nula ou vazia.");
        }

        String[] campos = linha.split(DELIMITER, -1);

        if (campos.length != EXPECTED_FIELD_COUNT) {
            throw new FormatoLinhaInvalidaException("Estrutura inválida. Esperados "
                    + EXPECTED_FIELD_COUNT + " campos delimitados por '"
                    + DELIMITER + "', mas obtidos " + campos.length + ".");
        }

        String cliente = campos[INDEX_CLIENTE].trim();
        String tipoOperacao = campos[INDEX_TIPO_OPERACAO].trim();
        String valorStr = campos[INDEX_VALOR].trim();
        String descricao = campos[INDEX_DESCRICAO].trim();

        if (cliente.isEmpty()) {
            throw new FormatoLinhaInvalidaException("Campo CLIENTE não pode estar vazio.");
        }

        if (tipoOperacao.isEmpty()) {
            throw new FormatoLinhaInvalidaException("Campo TIPO_OPERACAO não pode estar vazio.");
        }

        if (descricao.isEmpty()) {
            throw new FormatoLinhaInvalidaException("Campo DESCRICAO não pode estar vazio.");
        }

        if (valorStr.isEmpty()) {
            throw new ValorMovimentoInvalidoException("Campo VALOR monetário não pode estar vazio.");
        }

        double valor;
        try {
            String valorNormalizado = valorStr.replace(',', '.');
            valor = Double.parseDouble(valorNormalizado);
        } catch (NumberFormatException e) {
            throw new ValorMovimentoInvalidoException("Montante monetário inválido (não numérico): '" + valorStr + "'.", e);
        }

        if (valor <= 0.0) {
            throw new ValorMovimentoInvalidoException("Montante monetário deve ser estritamente positivo (> 0.0): " + valor);
        }

        return new LinhaMovimento(cliente, tipoOperacao, valor, descricao);
    }

    /**
     * Obtém o identificador do cliente.
     *
     * @return Nome ou ID do cliente.
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Obtém o tipo de operação bancária.
     *
     * @return Tipo de operação.
     */
    public String getTipoOperacao() {
        return tipoOperacao;
    }

    /**
     * Obtém o montante monetário do movimento.
     *
     * @return Valor numérico do movimento.
     */
    public double getValor() {
        return valor;
    }

    /**
     * Obtém a descrição do movimento.
     *
     * @return Descrição textual.
     */
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return String.format("LinhaMovimento{cliente='%s', tipo='%s', valor=%.2f, descricao='%s'}",
                cliente, tipoOperacao, valor, descricao);
    }
}
