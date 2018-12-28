package br.com.becommerce.core.inventory;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryItemDTO {

    @Null
    @ApiModelProperty(hidden = true)
    private String id;

    @Null
    @ApiModelProperty(hidden = true)
    private String inventoryId;

    @NotNull
    @DecimalMin(inclusive = false, value = "0")
    @ApiModelProperty(example = "15")
    private BigDecimal amount;

    @Null
    @ApiModelProperty(hidden = true)
    private String productId;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 200)
    @ApiModelProperty(example = "Example Product")
    private String productName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 2000)
    @ApiModelProperty(example = "The description of Example Product")
    private String productDescription;
}
