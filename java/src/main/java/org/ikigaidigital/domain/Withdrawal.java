package org.ikigaidigital.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Withdrawal entity representing a withdrawal made from a time deposit.
 */
@Entity
@Table(name = "withdrawals")
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "timeDepositId", nullable = false)
    private Integer timeDepositId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    /**
     * No-arg constructor required by JPA.
     */
    protected Withdrawal() {
    }

    public Withdrawal(Integer timeDepositId, BigDecimal amount, LocalDate date) {
        this.timeDepositId = timeDepositId;
        this.amount = amount;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTimeDepositId() {
        return timeDepositId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}