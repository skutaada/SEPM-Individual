package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao horseDao;
  private final OwnerDao ownerDao;
  @Autowired
  public HorseValidator(HorseDao horseDao, OwnerDao ownerDao) {
    this.horseDao = horseDao;
    this.ownerDao = ownerDao;
  }

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.name() == null || horse.name().isBlank()){
      validationErrors.add("Horses name cannot be empty");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.fatherId() != null) {
      try {
        var father = horseDao.getById(horse.fatherId());
        if (father.getFatherId() == horse.id()) {
          validationErrors.add("Cannot set horses son as father");
        }
      } catch (NotFoundException e) {
        validationErrors.add("Horses father must exist");
      }
    }

    if (horse.motherId() != null) {
      try {
         var mother = horseDao.getById(horse.motherId());
         if (mother.getMotherId() == horse.id()) {
           validationErrors.add("Cannot set horses daughter as mother");
         }
      } catch (NotFoundException e) {
        validationErrors.add("Horses mother must exist");
      }
    }

    if (horse.fatherId() == horse.id()) {
      validationErrors.add("Horse father cannot be the horse itself");
    }

    if (horse.motherId() == horse.id()) {
      validationErrors.add("Horse mother cannot be the horse itself");
    }

    if (horse.ownerId() != null) {
      try {
        ownerDao.getById(horse.ownerId());
      } catch (NotFoundException e) {
        validationErrors.add("Horses owner must exist");
      }
    }

    if (horse.sex() != Sex.FEMALE && horse.sex() != Sex.MALE) {
      validationErrors.add("Horses sex must be either male or female");
    }

    if (LocalDate.now().isBefore(horse.dateOfBirth())) {
      validationErrors.add("Horses date of birth cannot be in the future");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.name() == null || horse.name().isBlank()){
      validationErrors.add("Horses name cannot be empty");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.fatherId() != null) {
      try {
        horseDao.getById(horse.fatherId());
      } catch (NotFoundException e) {
        validationErrors.add("Horses father must exist");
      }
    }

    if (horse.motherId() != null) {
      try {
        horseDao.getById(horse.motherId());
      } catch (NotFoundException e) {
        validationErrors.add("Horses mother must exist");
      }
    }

    if (horse.ownerId() != null) {
      try {
        ownerDao.getById(horse.ownerId());
      } catch (NotFoundException e) {
        validationErrors.add("Horses owner must exist");
      }
    }

    if (horse.sex() != Sex.FEMALE && horse.sex() != Sex.MALE) {
      validationErrors.add("Horses sex must either be male or female");
    }

    if (LocalDate.now().isBefore(horse.dateOfBirth())) {
      validationErrors.add("Horses birth date cannnot be in the future");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

}
