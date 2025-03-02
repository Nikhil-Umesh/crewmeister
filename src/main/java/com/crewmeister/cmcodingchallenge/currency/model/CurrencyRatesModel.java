package com.crewmeister.cmcodingchallenge.currency.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "forex")
@IdClass(value = CurrencyRateModelPrimaryKey.class)
public class CurrencyRatesModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String conversionDate;
    @Id
    private String forexName;
    @Column
    private BigDecimal forexValue;
}
