package br.com.becommerce.core.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ExceptionDTO {

    private String type;
    private String message;
    private List<String> errors = new ArrayList<>();
    private String stackTrace;
}
