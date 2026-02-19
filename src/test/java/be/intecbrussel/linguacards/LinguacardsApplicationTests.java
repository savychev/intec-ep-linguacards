package be.intecbrussel.linguacards;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled default Spring Initializr context test to enforce Testcontainers-based integration tests only")
class LinguacardsApplicationTests {

    @Test
    void contextLoads() {
    }

}
