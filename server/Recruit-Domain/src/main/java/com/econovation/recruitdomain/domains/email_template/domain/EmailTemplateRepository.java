package com.econovation.recruitdomain.domains.email_template.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    @Query("SELECT et FROM EmailTemplate et WHERE et.emailTemplateType = :name")
    Optional<EmailTemplate> findByEmailTemplateType(String name);
}
