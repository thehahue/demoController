package at.bbrz.demo;

import at.bbrz.demo.service.HundeService;
import at.bbrz.demo.service.HundeServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private HundeServiceInterface hundeService;

	@Test
	void contextLoads() {
		assertThat(hundeService).isInstanceOf(HundeService.class);
	}

}
