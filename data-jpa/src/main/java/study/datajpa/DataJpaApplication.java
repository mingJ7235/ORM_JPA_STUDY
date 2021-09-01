package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing //이걸 넣어야 Spring data jpa 에서 auditing이 된다.
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	/**
	 * 등록자, 수정자를 넣기위한 작업
	 */
	@Bean
	public AuditorAware <String> auditorProvider () {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
