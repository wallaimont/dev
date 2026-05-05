package com.seguradora.site.repository;

import com.seguradora.site.entity.LeadRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRecordRepository extends JpaRepository<LeadRecord, Long> {

    List<LeadRecord> findAllByOrderByCreatedAtDesc();
}