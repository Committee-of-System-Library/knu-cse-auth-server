package kr.ac.knu.cse.dues.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.ac.knu.cse.dues.domain.Dues;

public interface DuesRepository extends JpaRepository<Dues, Long> {
}
