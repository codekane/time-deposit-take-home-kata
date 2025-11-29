package org.ikigaidigital.application;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.adapter.persistence.TimeDepositRepository;
import org.ikigaidigital.adapter.persistence.WithdrawalRepository;
import org.ikigaidigital.adapter.web.TimeDepositResponse;
import org.ikigaidigital.adapter.web.WithdrawalResponse;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeDepositService {

    private final TimeDepositRepository timeDepositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TimeDepositCalculator calculator;

    public TimeDepositService(TimeDepositRepository timeDepositRepository,
                               WithdrawalRepository withdrawalRepository) {
        this.timeDepositRepository = timeDepositRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.calculator = new TimeDepositCalculator();
    }

    /**
     * Retrieves all time deposits with their associated withdrawals.
     */
    @Transactional(readOnly = true)
    public List<TimeDepositResponse> getAllTimeDeposits() {
        List<TimeDeposit> deposits = timeDepositRepository.findAll();

        return deposits.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates balances for all time deposits by applying interest calculations.
     * Uses the existing TimeDepositCalculator to ensure backward compatibility.
     */
    @Transactional
    public void updateAllBalances() {
        List<TimeDeposit> deposits = timeDepositRepository.findAll();
        calculator.updateBalance(deposits);
        timeDepositRepository.saveAll(deposits);
    }

    private TimeDepositResponse toResponse(TimeDeposit deposit) {
        List<Withdrawal> withdrawals = withdrawalRepository.findByTimeDepositId(deposit.getId());

        List<WithdrawalResponse> withdrawalResponses = withdrawals.stream()
                .map(w -> new WithdrawalResponse(w.getId(), w.getAmount(), w.getDate()))
                .collect(Collectors.toList());

        return new TimeDepositResponse(
                deposit.getId(),
                deposit.getPlanType(),
                deposit.getBalance(),
                deposit.getDays(),
                withdrawalResponses
        );
    }
}