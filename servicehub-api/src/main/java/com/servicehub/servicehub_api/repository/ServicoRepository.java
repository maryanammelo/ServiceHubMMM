package com.servicehub.servicehub_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.servicehub.servicehub_api.model.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
}