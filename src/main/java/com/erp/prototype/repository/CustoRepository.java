package com.erp.prototype.repository;

import com.erp.prototype.model.Custo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustoRepository extends JpaRepository<Custo, Long> {
}
