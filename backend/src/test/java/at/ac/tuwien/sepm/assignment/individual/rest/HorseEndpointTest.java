package at.ac.tuwien.sepm.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isGreaterThanOrEqualTo(10);
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name)
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
  public void creatingNewHorse() throws Exception {
    var testHorseDto = new HorseCreateDto(
        "Alice",
        "Cool",
        LocalDate.now(),
        Sex.FEMALE,
        null,
        null,
        null);

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    String json = mapper.writeValueAsString(testHorseDto);

    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsByteArray();

    HorseDetailDto horseResult = objectMapper.readerFor(HorseDetailDto.class).readValue(body);

    assertThat(horseResult).isNotNull();
    assertThat(horseResult).extracting("name", "sex")
        .contains("Alice", Sex.FEMALE);
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void gettingNonexistentHorseReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-103")
        ).andExpect(status().isNotFound());
  }
}
