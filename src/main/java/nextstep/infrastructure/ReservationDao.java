package nextstep.infrastructure;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import nextstep.domain.Reservation;
import nextstep.domain.repository.ReservationRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationDao implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Reservation> reservation = (rs, rowNum) -> new Reservation(
        rs.getLong("id"),
        rs.getString("date"),
        rs.getString("time"),
        rs.getString("name")
    );

    @Override
    public void save(Reservation reservation) {
        jdbcTemplate.update(
            "insert into reservation (date, time, name) values (?, ?, ?)",
            reservation.getDate(),
            reservation.getTime(),
            reservation.getName()
        );
    }

    @Override
    public Optional<Reservation> findBy(String date, String time) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                "select id, date, time, name from reservation where date = ? and time = ?",
                reservation,
                LocalDate.parse(date),
                LocalTime.parse(time)
            ));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exist(Long id) {
        List<Reservation> reservations = jdbcTemplate.query(
            "select id, date, time, name from reservation where id = ?",
            reservation,
            id
        );
        return !reservations.isEmpty();
    }

    @Override
    public List<Reservation> findAllBy(String date) {
        return jdbcTemplate.query(
            "select id, date, time, name from reservation where date = ?",
            reservation,
            LocalDate.parse(date)
        );
    }

    @Override
    public void delete(String date, String time) {
        jdbcTemplate.update(
            "delete from reservation where date = ? and time = ?",
            LocalDate.parse(date),
            LocalTime.parse(time)
        );
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from reservation");
    }
}
