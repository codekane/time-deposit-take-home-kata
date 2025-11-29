package org.ikigaidigital.adapter.web;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for withdrawal data in API responses.
 */
public class WithdrawalResponse {

    private Integer id;
    private BigDecimal amount;
    private LocalDate date;

    public WithdrawalResponse(Integer id, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}