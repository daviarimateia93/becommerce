package br.com.becommerce.core.exception;

public abstract class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 8373968326695451567L;

    protected BusinessException(String messagePattern, Object... objects) {
        super(String.format(messagePattern, objects));
    }
}
