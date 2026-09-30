package excecoes;

/**
 * Exceção verificada (checked exception) lançada quando uma linha do ficheiro
 * de movimentos apresenta inconsistências estruturais no seu formato, como campos
 * insuficientes, delimitadores em falta ou conteúdo nulo/vazio.
 * 
 * @author Carlos Marques
 * @version 1.0
 */
public class FormatoLinhaInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor por omissão.
     */
    public FormatoLinhaInvalidaException() {
        super();
    }

    /**
     * Construtor parametrizado com mensagem explicativa.
     *
     * @param message Mensagem detalhada sobre o erro estrutural de formato.
     */
    public FormatoLinhaInvalidaException(String message) {
        super(message);
    }

    /**
     * Construtor parametrizado com a causa original do erro.
     *
     * @param cause A exceção causa original (Throwable).
     */
    public FormatoLinhaInvalidaException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor parametrizado com mensagem explicativa e causa original.
     *
     * @param message Mensagem detalhada sobre o erro estrutural de formato.
     * @param cause   A exceção causa original (Throwable).
     */
    public FormatoLinhaInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
