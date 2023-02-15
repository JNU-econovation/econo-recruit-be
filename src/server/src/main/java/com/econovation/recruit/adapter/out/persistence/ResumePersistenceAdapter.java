package com.econovation.recruit.adapter.out.persistence;

import com.econovation.recruit.application.port.out.ResumeRecordPort;
import com.econovation.recruit.domain.dto.ResumeInsertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class ResumePersistenceAdapter implements ResumeRecordPort {
    private static final String NO_OBJECT_EXCEPTION = "데이터가 존재하지 않습니다.";
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<ResumeInsertDto> saveAll(List<ResumeInsertDto> resumes){
        String sql = "INSERT INTO resume (applicant_id, question_id, answer) " +
                "VALUES (?, ?, ?)";
        if (!resumes.isEmpty()) {
            batchInsert(resumes, sql);
            return resumes;
        }
        throw new IllegalArgumentException(NO_OBJECT_EXCEPTION);
    }
    private void batchInsert(List<ResumeInsertDto> resumes, String sql) {
        jdbcTemplate.batchUpdate(sql,
                resumes,
                resumes.size(),
                (PreparedStatement ps, ResumeInsertDto resume) -> {
                    ps.setInt(1, resume.getApplicantId());
                    ps.setInt(2,resume.getQuestionId());
                    ps.setString(3, resume.getAnswer());
                });
    }
}