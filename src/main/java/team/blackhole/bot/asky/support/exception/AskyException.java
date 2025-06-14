package team.blackhole.bot.asky.support.exception;

/**
 * Исключение аргуса
 */
public class AskyException extends RuntimeException {

    /**
     * Конструктор исключения аргуса
     */
    public AskyException() {
        super();
    }

    /**
     * Конструктор исключения аргуса
     * @param message сообщение об ошибке
     */
    public AskyException(String message) {
        super(message);
    }

    /**
     * Конструктор исключения аргуса
     * @param message сообщение об ошибке
     * @param cause   причина (которая сохраняется для последующего извлечения методом getCause())
     */
    public AskyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Конструктор исключения аргуса
     * @param cause причина (которая сохраняется для последующего извлечения методом getCause())
     */
    public AskyException(Throwable cause) {
        super(cause);
    }
}