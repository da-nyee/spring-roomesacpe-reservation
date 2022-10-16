package nextstep.application;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.domain.Reservation;
import nextstep.domain.repository.ReservationRepository;
import nextstep.exception.ReservationException;
import nextstep.presentation.dto.ReservationRequest;
import nextstep.presentation.dto.ReservationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Long make(ReservationRequest request) {
        Optional<Reservation> reservation = findBy(request);

        if (reservation.isPresent()) {
            throw new ReservationException(String.format("%s에는 이미 예약이 차있습니다.", reservation));
        }

        reservationRepository.save(toReservation(request));
        return findBy(request)
            .map(Reservation::getId)
            .orElseThrow(() -> new ReservationException("존재하지 않는 예약입니다."));
    }

    private Optional<Reservation> findBy(ReservationRequest request) {
        return reservationRepository.findBy(request.getDate(), request.getTime());
    }

    private Reservation toReservation(ReservationRequest request) {
        return new Reservation(request.getDate(), request.getTime(), request.getName());
    }

    public boolean exist(Long id) {
        return reservationRepository.exist(id);
    }

    public List<ReservationResponse> checkAll(String date) {
        List<Reservation> reservations = reservationRepository.findAllBy(date);

        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }
        return reservations.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getDate().toString(),
            reservation.getTime().toString(),
            reservation.getName()
        );
    }

    @Transactional
    public void cancel(String date, String time) {
        reservationRepository.findBy(date, time)
            .ifPresentOrElse(reservation -> reservationRepository.delete(date, time), () -> {
                throw new ReservationException("존재하는 예약이 없어 예약을 취소할 수 없습니다.");
            });
    }

    @Transactional
    public void cancelAll() {
        reservationRepository.deleteAll();
    }
}
