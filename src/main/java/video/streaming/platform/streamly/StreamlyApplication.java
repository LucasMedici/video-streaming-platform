package video.streaming.platform.streamly;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StreamlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamlyApplication.class, args);
	}

}
