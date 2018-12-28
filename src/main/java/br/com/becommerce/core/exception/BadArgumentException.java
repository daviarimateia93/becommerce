package br.com.becommerce.core.exception;

public class BadArgumentException extends BusinessException {

    private static final long serialVersionUID = -5797407900965580498L;

    public BadArgumentException(String messagePattern, Object... objects) {
        super(messagePattern, objects);
    }
}
