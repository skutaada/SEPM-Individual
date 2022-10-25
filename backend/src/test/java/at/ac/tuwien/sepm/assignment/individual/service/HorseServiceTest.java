package at.ac.tuwien.sepm.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(10);
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE))
        .contains(tuple(-2L, Sex.MALE))
        .contains(tuple(-3L, Sex.MALE))
        .contains(tuple(-4L, Sex.MALE))
        .contains(tuple(-5L, Sex.FEMALE))
        .contains(tuple(-6L, Sex.FEMALE))
        .contains(tuple(-7L, Sex.MALE))
        .contains(tuple(-8L, Sex.MALE))
        .contains(tuple(-9L, Sex.FEMALE))
        .contains(tuple(-10L, Sex.MALE));
  }

  @Test
  public void getByIdReturnsSelectedHorse() throws Exception {
    HorseDetailDto horse = horseService.getById(-2);
    assertThat(horse).isNotNull();
    assertThat(horse).extracting("id", "name")
        .contains(-2L, "Bucky");
  }

  @Test
  public void getByIdOfNonExistingHorseThrowsNotFoundException() {
    assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> {
          var horse = horseService.getById(-103);
        });
  }

  @Test
  public void updateReturnsUpdateHorse() throws Exception {
    var horse = new HorseDetailDto(-1L, "Coolio", "The famous one!", LocalDate.now(), Sex.FEMALE, null, null, null);
    var updatedHorse = horseService.update(horse);
    assertThat(updatedHorse).isNotNull();
    assertThat(updatedHorse).extracting("id", "name")
        .contains(-1L, "Coolio");
  }


}
