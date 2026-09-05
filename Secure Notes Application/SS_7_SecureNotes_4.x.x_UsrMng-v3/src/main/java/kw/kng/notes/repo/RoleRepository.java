package kw.kng.notes.repo;

import kw.kng.notes.entities.AppRole;
import kw.kng.notes.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}