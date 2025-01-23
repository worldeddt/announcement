package api.announcement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        int retries = 5;
        while (retries > 0) {
            try {
                DataSource dataSource = DataSourceBuilder.create()
                        .url(url)
                        .username(username)
                        .password(password)
                        .build();
                dataSource.getConnection();  // DB 연결 확인
                return dataSource;
            } catch (SQLException e) {
                retries--;
                if (retries == 0) {
                    throw new RuntimeException("Failed to connect to database after retries", e);
                }
                try {
                    Thread.sleep(5000);  // 5초 대기 후 재시도
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException("Unexpected error in data source initialization");
    }
}
