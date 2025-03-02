package com.crewmeister.cmcodingchallenge.currency.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ForexValueModel {
    private String forexName;
    private BigDecimal value;
}
