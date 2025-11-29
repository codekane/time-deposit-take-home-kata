package org.ikigaidigital.adapter.persistence;

import org.ikigaidigital.TimeDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeDepositRepository extends JpaRepository<TimeDeposit, Integer> {
}