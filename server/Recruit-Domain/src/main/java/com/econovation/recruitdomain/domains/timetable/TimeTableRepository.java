package com.econovation.recruitdomain.domains.timetable;

import com.econovation.recruitdomain.domains.applicant.Applicant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {
    List<TimeTable> findByApplicant(Applicant applicant);
}
