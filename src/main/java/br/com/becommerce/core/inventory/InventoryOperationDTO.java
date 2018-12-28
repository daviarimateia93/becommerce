package br.com.becommerce.core.inventory;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryOperationDTO {

    public enum Operation {
        ADD, SUBTRACT
    }

    @Null
    @ApiModelProperty(hidden = true)
    private String inventoryItemId;

    @NotNull
    private Operation operation;

    @NotNull
    @DecimalMin(inclusive = false, value = "0")
    @ApiModelProperty(example = "1.5")
    private BigDecimal value;
}
