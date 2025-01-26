package tk.artsakenos.vault.model;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_search_data")
@Data
@NoArgsConstructor
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = true)
    private String userName;

    @Column(name = "user_role", nullable = true)
    private String userRole;

    @Column(name = "query", nullable = true)
    private String query;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "referrer", nullable = true)
    private String referrer;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "request_method", nullable = false)
    private String requestMethod;

    @Column(name = "request_uri", nullable = false)
    private String requestURI;

    @Column(name = "query_string", nullable = true)
    private String queryString;

    @Column(name = "protocol", nullable = false)
    private String protocol;

    @Column(name = "server_name", nullable = false)
    private String serverName;

    @Column(name = "server_port", nullable = false)
    private int serverPort;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public UserData(HttpServletRequest request, String userName, String userRole, String query) {
        setUserAgent(request.getHeader("User-Agent"));
        setReferrer(request.getHeader("Referer"));
        setIpAddress(request.getRemoteAddr());
        setRequestMethod(request.getMethod());
        setRequestURI(request.getRequestURI());
        setQueryString(request.getQueryString());
        setProtocol(request.getProtocol());
        setServerName(request.getServerName());
        setServerPort(request.getServerPort());
        setUserName(userName);
        setUserRole(userRole);
        setQuery(query);
    }
}