package org.ikigaidigital.adapter.persistence;

import org.ikigaidigital.domain.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {

    List<Withdrawal> findByTimeDepositId(Integer timeDepositId);
}
