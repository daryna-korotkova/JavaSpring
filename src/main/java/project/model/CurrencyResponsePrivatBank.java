package project.model;

import lombok.Data;

@Data
public class CurrencyResponsePrivatBank {
    private String ccy;
    private String base_ccy;
    private Double buy;
    private Double sale;
}
