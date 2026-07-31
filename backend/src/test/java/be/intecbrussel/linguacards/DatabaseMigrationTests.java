package be.intecbrussel.linguacards;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseMigrationTests {

    @Autowired
    private Flyway flyway;

    @Test
    void flywayAppliesTheCurrentSchemaVersion() {
        MigrationInfo currentMigration = flyway.info().current();

        assertThat(currentMigration).isNotNull();
        assertThat(currentMigration.getVersion()).hasToString("1");
    }
}
