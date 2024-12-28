package tk.artsakenos.vault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tk.artsakenos.vault.model.UserData;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

    List<UserData> findTop10ByOrderByTimestampDesc();

}