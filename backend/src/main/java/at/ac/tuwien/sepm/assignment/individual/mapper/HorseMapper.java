package at.ac.tuwien.sepm.assignment.individual.mapper;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public HorseMapper() {
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, OwnerDto> owners, Map<Long, HorseDetailDto> fathers, Map<Long, HorseDetailDto> mothers) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseListDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners),
        getFather(horse, fathers),
        getMother(horse, mothers)
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @return the converted {@link HorseListDto}
   */
  public HorseDetailDto entityToDetailDto(
      Horse horse,
      Map<Long, OwnerDto> owners,
      Map<Long, HorseDetailDto> fathers,
      Map<Long, HorseDetailDto> mothers) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }


    return new HorseDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners),
        getFather(horse, fathers),
        getMother(horse, mothers)
    );
  }

  private OwnerDto getOwner(Horse horse, Map<Long, OwnerDto> owners) {
    OwnerDto owner = null;
    var ownerId = horse.getOwnerId();
    if (ownerId != null) {
      if (!owners.containsKey(ownerId)) {
        throw new FatalException("Given owner map does not contain owner of this Horse (%d)".formatted(horse.getId()));
      }
      owner = owners.get(ownerId);
    }
    return owner;
  }

  private HorseDetailDto getFather(Horse horse, Map<Long, HorseDetailDto> fathers) {
    HorseDetailDto father = null;
    var fatherId = horse.getFatherId();
    if (fatherId != null) {
      if (!fathers.containsKey(fatherId)) {
        throw new FatalException("Given horses map does not contain father of this Horse (%d)".formatted(horse.getId()));
      }
      father = fathers.get(fatherId);
    }
    return father;
  }

  private HorseDetailDto getMother(Horse horse, Map<Long, HorseDetailDto> mothers) {
    HorseDetailDto mother = null;
    var motherId = horse.getMotherId();
    if (motherId != null) {
      if (!mothers.containsKey(motherId)) {
        throw new FatalException("Given horses map does not contain mother of this Horse (%d)".formatted(horse.getId()));
      }
      mother = mothers.get(motherId);
    }
    return mother;
  }

}
