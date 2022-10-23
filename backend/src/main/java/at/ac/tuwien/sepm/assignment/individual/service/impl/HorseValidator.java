package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.fatherId() == horse.id()) {
      validationErrors.add("Horse father cannot be the horse itself");
    }

    if (horse.motherId() == horse.id()) {
      validationErrors.add("Horse mother cannot be the horse itself");
    }



    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

}
