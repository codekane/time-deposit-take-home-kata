package org.ikigaidigital.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ikigaidigital.application.TimeDepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/time-deposits")
@Tag(name = "Time Deposits", description = "Time deposit management endpoints")
public class TimeDepositController {

    private final TimeDepositService timeDepositService;

    public TimeDepositController(TimeDepositService timeDepositService) {
        this.timeDepositService = timeDepositService;
    }

    @GetMapping
    @Operation(summary = "Get all time deposits", 
               description = "Retrieves all time deposits with their associated withdrawals")
    public ResponseEntity<List<TimeDepositResponse>> getAllTimeDeposits() {
        return ResponseEntity.ok(timeDepositService.getAllTimeDeposits());
    }
}