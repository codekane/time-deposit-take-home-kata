package org.ikigaidigital;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Characterization tests for TimeDepositCalculator.
 * These tests document and verify the existing behavior before refactoring.
 * DO NOT MODIFY these tests - they are the safety net for refactoring.
 */
public class TimeDepositCalculatorTest {

    private TimeDepositCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TimeDepositCalculator();
    }

    @Nested
    @DisplayName("Basic Plan - 1% annual interest (1%/12 monthly)")
    class BasicPlanTests {

        @Test
        @DisplayName("No interest for first 30 days")
        void noInterestFirst30Days() {
            TimeDeposit deposit = new TimeDeposit(1, "basic", 1200.00, 30);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        @DisplayName("Interest applied after 30 days")
        void interestAppliedAfter30Days() {
            TimeDeposit deposit = new TimeDeposit(1, "basic", 1200.00, 31);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            // 1200 * 0.01 / 12 = 1.00
            assertThat(deposit.getBalance()).isEqualTo(1201.00);
        }

        @Test
        @DisplayName("Interest rounds to 2 decimal places using HALF_UP")
        void interestRoundsCorrectly() {
            TimeDeposit deposit = new TimeDeposit(1, "basic", 1000.00, 31);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            // 1000 * 0.01 / 12 = 0.8333... rounds to 0.83
            assertThat(deposit.getBalance()).isEqualTo(1000.83);
        }
    }

    @Nested
    @DisplayName("Student Plan - 3% annual interest (3%/12 monthly), no interest after 1 year")
    class StudentPlanTests {

        @Test
        @DisplayName("No interest for first 30 days")
        void noInterestFirst30Days() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, 30);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        @DisplayName("Interest applied after 30 days and before 366 days")
        void interestAppliedBetween31And365Days() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, 31);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            // 1200 * 0.03 / 12 = 3.00
            assertThat(deposit.getBalance()).isEqualTo(1203.00);
        }

        @Test
        @DisplayName("Interest still applied at day 365")
        void interestAppliedAtDay365() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, 365);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            // 1200 * 0.03 / 12 = 3.00
            assertThat(deposit.getBalance()).isEqualTo(1203.00);
        }

        @Test
        @DisplayName("No interest after 1 year (366+ days)")
        void noInterestAfterOneYear() {
            TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, 366);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }
    }

    @Nested
    @DisplayName("Premium Plan - 5% annual interest (5%/12 monthly), starts after 45 days")
    class PremiumPlanTests {

        @Test
        @DisplayName("No interest for first 30 days")
        void noInterestFirst30Days() {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 1200.00, 30);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        @DisplayName("No interest between 31-45 days (premium waiting period)")
        void noInterestDuringWaitingPeriod() {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 1200.00, 45);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        @DisplayName("Interest applied after 45 days")
        void interestAppliedAfter45Days() {
            TimeDeposit deposit = new TimeDeposit(1, "premium", 1200.00, 46);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            // 1200 * 0.05 / 12 = 5.00
            assertThat(deposit.getBalance()).isEqualTo(1205.00);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Unknown plan type receives no interest")
        void unknownPlanTypeNoInterest() {
            TimeDeposit deposit = new TimeDeposit(1, "unknown", 1200.00, 100);
            calculator.updateBalance(Collections.singletonList(deposit));
            
            assertThat(deposit.getBalance()).isEqualTo(1200.00);
        }

        @Test
        @DisplayName("Empty list is handled gracefully")
        void emptyListHandled() {
            List<TimeDeposit> emptyList = Collections.emptyList();
            calculator.updateBalance(emptyList);
            
            // No exception thrown
            assertThat(emptyList).isEmpty();
        }

        @Test
        @DisplayName("Multiple deposits are all updated")
        void multipleDepositsUpdated() {
            List<TimeDeposit> deposits = Arrays.asList(
                new TimeDeposit(1, "basic", 1200.00, 31),
                new TimeDeposit(2, "student", 1200.00, 31),
                new TimeDeposit(3, "premium", 1200.00, 46)
            );
            calculator.updateBalance(deposits);
            
            assertThat(deposits.get(0).getBalance()).isEqualTo(1201.00); // basic: +1.00
            assertThat(deposits.get(1).getBalance()).isEqualTo(1203.00); // student: +3.00
            assertThat(deposits.get(2).getBalance()).isEqualTo(1205.00); // premium: +5.00
        }
    }
}
