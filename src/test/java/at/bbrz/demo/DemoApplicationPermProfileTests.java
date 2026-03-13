package at.bbrz.demo;

import at.bbrz.demo.service.HundePermService;
import at.bbrz.demo.service.HundeServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("perm")
class DemoApplicationPermProfileTests {

    @Autowired
    private HundeServiceInterface hundeService;

    @Test
    void contextLoads_withPermProfile_usesHundePermService() {
        assertThat(hundeService).isInstanceOf(HundePermService.class);
    }
}
