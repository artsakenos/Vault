package tk.artsakenos.vault.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.artsakenos.vault.model.UserData;
import tk.artsakenos.vault.repository.UserDataRepository;

@Service
public class LogService {


    @Autowired
    private UserDataRepository userSearchDataRepository;

    public void logUserData(HttpServletRequest request, String query) {
        UserData userSearchData = new UserData(request, query);
        userSearchDataRepository.save(userSearchData);

    }
}
