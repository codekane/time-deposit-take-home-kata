package org.ikigaidigital.adapter.web;

import java.util.List;

/**
 * DTO for time deposit data in API responses.
 * Includes withdrawals as required by the API specification.
 */
public class TimeDepositResponse {

    private int id;
    private String planType;
    private Double balance;
    private int days;
    private List<WithdrawalResponse> withdrawals;

    public TimeDepositResponse(int id, String planType, Double balance, int days, 
                                List<WithdrawalResponse> withdrawals) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
        this.withdrawals = withdrawals;
    }

    public int getId() {
        return id;
    }

    public String getPlanType() {
        return planType;
    }

    public Double getBalance() {
        return balance;
    }

    public int getDays() {
        return days;
    }

    public List<WithdrawalResponse> getWithdrawals() {
        return withdrawals;
    }
}