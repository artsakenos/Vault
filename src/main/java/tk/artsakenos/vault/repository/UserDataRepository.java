package tk.artsakenos.vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tk.artsakenos.vault.model.UserData;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
}