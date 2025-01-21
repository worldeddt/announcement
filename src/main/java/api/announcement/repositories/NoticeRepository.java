package api.announcement.repositories;


import api.announcement.entities.Notice;
import api.announcement.enums.NoticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAll(Pageable pageable);

    Page<Notice> findAllByStatus(Pageable pageable, NoticeStatus status);

    Optional<Notice> findByIdAndStatus(Long id, NoticeStatus status);
}
