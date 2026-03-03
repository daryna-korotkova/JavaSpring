package project.dto;

import lombok.Data;

@Data
public class CurrencyResponse {
    private String ccy;     // Код валюти (USD)
    private String base_ccy; // Базова валюта (UAH)
    private Double buy;     // Курс купівлі
    private Double sale;    // Курс продажу
}
