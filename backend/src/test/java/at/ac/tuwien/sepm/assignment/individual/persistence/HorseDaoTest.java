package at.ac.tuwien.sepm.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;

import java.time.LocalDate;
import java.util.List;

import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(10);
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-1L, "Wendy"))
        .contains(tuple(-2L, "Bucky"))
        .contains(tuple(-3L, "Tricky"))
        .contains(tuple(-4L, "Dambo"))
        .contains(tuple(-5L, "Buck"))
        .contains(tuple(-6L, "Krill"))
        .contains(tuple(-7L, "Bill"))
        .contains(tuple(-8L, "Radagon"))
        .contains(tuple(-9L, "Ranni"))
        .contains(tuple(-10L, "Blaidd"));
  }

  @Test
  public void searchReturnsAllHorsesThatMatch() {
    var searchHorseDto = new HorseSearchDto("Dambo", null, null, null, null, 5);
    List<Horse> horses = horseDao.search(searchHorseDto);
    assertThat(horses.size()).isGreaterThanOrEqualTo(1);
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-4L, "Dambo"));
  }

  @Test
  public void getByIdOfNonExistingHorseReturnsNotFoundException() {
    assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> {
          horseDao.getById(-103);
        });
  }

  @Test
  public void createReturnsCreatedHorse() {
    var createHorseDto = new HorseCreateDto("Trulo", null, LocalDate.now(), Sex.FEMALE, null, null, null);
    var createdHorse = horseDao.create(createHorseDto);

    assertThat(createdHorse).isNotNull();
    assertThat(createdHorse).extracting("name", "sex")
        .contains("Trulo", Sex.FEMALE);
  }
}
