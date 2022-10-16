package nextstep.domain.repository;

import java.util.List;
import java.util.Optional;
import nextstep.domain.Theme;

public interface ThemeRepository {

    void save(Theme theme);

    Optional<Theme> findBy(String name);

    Optional<Theme> findBy(Long id);

    List<Theme> findAll();

    void delete(Long id);

    void deleteAll();
}
