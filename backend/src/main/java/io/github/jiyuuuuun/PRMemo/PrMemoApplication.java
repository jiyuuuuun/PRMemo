package io.github.jiyuuuuun.PRMemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PrMemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrMemoApplication.class, args);
	}

}
