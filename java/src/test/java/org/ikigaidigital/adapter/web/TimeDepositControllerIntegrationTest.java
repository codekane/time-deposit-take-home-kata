package org.ikigaidigital.adapter.web;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.persistence.TimeDepositRepository;
import org.ikigaidigital.adapter.persistence.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class TimeDepositControllerIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimeDepositRepository timeDepositRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @BeforeEach
    void setUp() {
        withdrawalRepository.deleteAll();
        timeDepositRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /time-deposits returns empty list when no deposits exist")
    void getTimeDeposits_empty() throws Exception {
        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /time-deposits returns deposits with withdrawals")
    void getTimeDeposits_withData() throws Exception {
        // Given
        TimeDeposit deposit = new TimeDeposit(1, "basic", 1000.00, 31);
        timeDepositRepository.save(deposit);

        Withdrawal withdrawal = new Withdrawal(1, new BigDecimal("100.00"), LocalDate.of(2024, 1, 15));
        withdrawalRepository.save(withdrawal);

        // When/Then
        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].planType", is("basic")))
                .andExpect(jsonPath("$[0].balance", is(1000.00)))
                .andExpect(jsonPath("$[0].days", is(31)))
                .andExpect(jsonPath("$[0].withdrawals", hasSize(1)))
                .andExpect(jsonPath("$[0].withdrawals[0].amount", is(100.00)));
    }

    @Test
    @DisplayName("GET /time-deposits returns deposits with empty withdrawals list")
    void getTimeDeposits_noWithdrawals() throws Exception {
        // Given
        TimeDeposit deposit = new TimeDeposit(1, "premium", 5000.00, 60);
        timeDepositRepository.save(deposit);

        // When/Then
        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].withdrawals", hasSize(0)));
    }

    @Test
    @DisplayName("POST /time-deposits/update-balances applies interest to basic plan")
    void updateBalances_basicPlan() throws Exception {
        // Given - basic plan after 30 days gets 1%/12 monthly interest
        TimeDeposit deposit = new TimeDeposit(1, "basic", 1200.00, 31);
        timeDepositRepository.save(deposit);

        // When
        mockMvc.perform(post("/time-deposits/update-balances"))
                .andExpect(status().isOk());

        // Then - 1200 * 0.01 / 12 = 1.00
        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[0].balance", is(1201.00)));
    }

    @Test
    @DisplayName("POST /time-deposits/update-balances applies interest to student plan")
    void updateBalances_studentPlan() throws Exception {
        // Given - student plan gets 3%/12 monthly interest
        TimeDeposit deposit = new TimeDeposit(1, "student", 1200.00, 31);
        timeDepositRepository.save(deposit);

        // When
        mockMvc.perform(post("/time-deposits/update-balances"))
                .andExpect(status().isOk());

        // Then - 1200 * 0.03 / 12 = 3.00
        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[0].balance", is(1203.00)));
    }

    @Test
    @DisplayName("POST /time-deposits/update-balances applies interest to premium plan after 45 days")
    void updateBalances_premiumPlan() throws Exception {
        // Given - premium plan gets 5%/12 monthly interest after 45 days
        TimeDeposit deposit = new TimeDeposit(1, "premium", 1200.00, 46);
        timeDepositRepository.save(deposit);

        // When
        mockMvc.perform(post("/time-deposits/update-balances"))
                .andExpect(status().isOk());

        // Then - 1200 * 0.05 / 12 = 5.00
        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[0].balance", is(1205.00)));
    }

    @Test
    @DisplayName("POST /time-deposits/update-balances does not apply interest within first 30 days")
    void updateBalances_noInterestFirst30Days() throws Exception {
        // Given
        TimeDeposit deposit = new TimeDeposit(1, "basic", 1200.00, 30);
        timeDepositRepository.save(deposit);

        // When
        mockMvc.perform(post("/time-deposits/update-balances"))
                .andExpect(status().isOk());

        // Then - no interest applied
        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[0].balance", is(1200.00)));
    }

    @Test
    @DisplayName("POST /time-deposits/update-balances updates multiple deposits")
    void updateBalances_multipleDeposits() throws Exception {
        // Given
        timeDepositRepository.save(new TimeDeposit(1, "basic", 1200.00, 31));
        timeDepositRepository.save(new TimeDeposit(2, "student", 1200.00, 31));
        timeDepositRepository.save(new TimeDeposit(3, "premium", 1200.00, 46));

        // When
        mockMvc.perform(post("/time-deposits/update-balances"))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/time-deposits"))
                .andExpect(jsonPath("$[?(@.id == 1)].balance", contains(1201.00)))
                .andExpect(jsonPath("$[?(@.id == 2)].balance", contains(1203.00)))
                .andExpect(jsonPath("$[?(@.id == 3)].balance", contains(1205.00)));
    }
}