package kr.ac.knu.cse.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.ac.knu.cse.application.dto.HandoutScanResult;
import kr.ac.knu.cse.application.dto.SnackEventResponse;
import kr.ac.knu.cse.application.dto.SnackHandoutResponse;
import kr.ac.knu.cse.domain.registry.CseStudentRegistry;
import kr.ac.knu.cse.domain.registry.CseStudentRegistryRepository;
import kr.ac.knu.cse.domain.snack.SnackEvent;
import kr.ac.knu.cse.domain.snack.SnackEventRepository;
import kr.ac.knu.cse.domain.snack.SnackEventStatus;
import kr.ac.knu.cse.domain.snack.SnackHandout;
import kr.ac.knu.cse.domain.snack.SnackHandoutRepository;
import kr.ac.knu.cse.global.exception.snack.SnackEventNotFoundException;
import kr.ac.knu.cse.global.exception.snack.SnackEventNotOpenException;
import kr.ac.knu.cse.infrastructure.ledger.LedgerDuesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnackEventService {

    private final SnackEventRepository eventRepository;
    private final SnackHandoutRepository handoutRepository;
    private final CseStudentRegistryRepository registryRepository;
    private final LedgerDuesClient ledgerDuesClient;

    public String suggestSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int term = now.getMonthValue() <= 6 ? 1 : 2;
        return year + "-" + term;
    }

    @Transactional
    public SnackEventResponse openEvent(
            String name,
            String semester,
            boolean requiresPayment,
            String adminStudentNumber
    ) {
        SnackEvent event = SnackEvent.open(name, semester, requiresPayment, adminStudentNumber);
        SnackEvent saved = eventRepository.save(event);
        return SnackEventResponse.of(saved, 0L);
    }

    public List<SnackEventResponse> listEvents(SnackEventStatus statusFilter) {
        List<SnackEvent> events = (statusFilter == null)
                ? eventRepository.findAllByOrderByOpenedAtDesc()
                : eventRepository.findAllByStatusOrderByOpenedAtDesc(statusFilter);

        return events.stream()
                .map(e -> SnackEventResponse.of(e, handoutRepository.countByEventId(e.getId())))
                .toList();
    }

    public SnackEventResponse getEvent(Long eventId) {
        SnackEvent event = findEventOrThrow(eventId);
        return SnackEventResponse.of(event, handoutRepository.countByEventId(eventId));
    }

    public List<SnackHandoutResponse> listHandouts(Long eventId) {
        findEventOrThrow(eventId);
        return handoutRepository.findAllByEventIdOrderByReceivedAtAsc(eventId).stream()
                .map(SnackHandoutResponse::from)
                .toList();
    }

    @Transactional
    public SnackEventResponse closeEvent(Long eventId) {
        SnackEvent event = findEventOrThrow(eventId);
        if (event.isOpen()) {
            event.close();
        }
        return SnackEventResponse.of(event, handoutRepository.countByEventId(eventId));
    }

    @Transactional
    public HandoutScanResult handout(Long eventId, String studentNumber) {
        SnackEvent event = findEventOrThrow(eventId);
        if (!event.isOpen()) {
            throw new SnackEventNotOpenException();
        }

        Optional<CseStudentRegistry> registry = registryRepository.findByStudentNumber(studentNumber);
        if (registry.isEmpty()) {
            return HandoutScanResult.notFound(studentNumber);
        }
        CseStudentRegistry student = registry.get();

        if (handoutRepository.existsByEventIdAndStudentNumber(eventId, studentNumber)) {
            SnackHandout existing = handoutRepository
                    .findAllByEventIdOrderByReceivedAtAsc(eventId).stream()
                    .filter(h -> h.getStudentNumber().equals(studentNumber))
                    .findFirst()
                    .orElseThrow();
            return HandoutScanResult.duplicate(
                    studentNumber, student.getName(), student.getMajor(), existing.getReceivedAt()
            );
        }

        if (event.isRequiresPayment()) {
            boolean paid = ledgerDuesClient.isPaid(studentNumber, event.getSemester());
            if (!paid) {
                return HandoutScanResult.unpaid(studentNumber, student.getName(), student.getMajor());
            }
        }

        try {
            SnackHandout handout = handoutRepository.save(
                    SnackHandout.of(eventId, studentNumber, student.getName(), student.getMajor())
            );
            return HandoutScanResult.ok(
                    studentNumber, student.getName(), student.getMajor(), handout.getReceivedAt()
            );
        } catch (DataIntegrityViolationException e) {
            // race: another scan inserted the same row between exists-check and save
            return HandoutScanResult.duplicate(studentNumber, student.getName(), student.getMajor(), null);
        }
    }

    public SnackEvent findEventEntity(Long eventId) {
        return findEventOrThrow(eventId);
    }

    public List<SnackHandout> findHandoutsForExport(Long eventId) {
        findEventOrThrow(eventId);
        return handoutRepository.findAllByEventIdOrderByReceivedAtAsc(eventId);
    }

    private SnackEvent findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(SnackEventNotFoundException::new);
    }
}
