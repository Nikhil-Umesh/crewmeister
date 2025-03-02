package com.crewmeister.cmcodingchallenge.currency.model;

import javax.persistence.Id;
import java.io.Serializable;

public class CurrencyRateModelPrimaryKey implements Serializable {
    private static final long serialVersionUID = 2L;
    @Id
    private String conversionDate;
    @Id
    private String forexName;
}
