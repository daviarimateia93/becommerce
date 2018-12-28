package br.com.becommerce.core.exception;

public class NotFoundException extends BusinessException {

    private static final long serialVersionUID = -8011743156158774600L;

    public NotFoundException(String messagePattern, Object... objects) {
        super(messagePattern, objects);
    }
}
