package excecoes;

/**
 * Exceção não verificada (unchecked exception) lançada perante falhas de
 * conversão de dados monetários, valores não numéricos, montantes nulos,
 * zero ou estritamente negativos.
 * 
 * @author Carlos Marques
 * @version 1.0
 */
public class ValorMovimentoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor por omissão.
     */
    public ValorMovimentoInvalidoException() {
        super();
    }

    /**
     * Construtor parametrizado com mensagem explicativa.
     *
     * @param message Mensagem detalhada sobre o erro de valor monetário.
     */
    public ValorMovimentoInvalidoException(String message) {
        super(message);
    }

    /**
     * Construtor parametrizado com a causa original do erro.
     *
     * @param cause A exceção causa original (Throwable).
     */
    public ValorMovimentoInvalidoException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor parametrizado com mensagem explicativa e causa original.
     *
     * @param message Mensagem detalhada sobre o erro de valor monetário.
     * @param cause   A exceção causa original (Throwable).
     */
    public ValorMovimentoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
