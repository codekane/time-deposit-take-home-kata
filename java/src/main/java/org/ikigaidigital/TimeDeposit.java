package org.ikigaidigital;

import jakarta.persistence.*;

/**
 * Time deposit entity representing a customer's time deposit account.
 *
 * Note: JPA annotations added to enable persistence. A protected no-arg
 * constructor was added as required by JPA specification. This is an additive
 * change that does not break existing consumers of this class - the original
 * constructor, fields, and methods remain unchanged.
 */
@Entity
@Table(name = "timeDeposits")
public class TimeDeposit {

    @Id
    private int id;

    @Column(nullable = false)
    private String planType;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private int days;

    /**
     * No-arg constructor required by JPA.
     */
    protected TimeDeposit() {
    }

    public TimeDeposit(int id, String planType, Double balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

    public int getId() { return id; }

    public String getPlanType() {
        return planType;
    }

    public Double getBalance() {
        return balance;
    }

    public int getDays() {
        return days;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
