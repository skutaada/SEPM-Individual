package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


@Component
public class OwnerValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForCreate(OwnerCreateDto owner) throws ValidationException {
    LOG.trace("validateForCreate({})", owner);
    List<String> validationErrors = new ArrayList<>();

    if (owner.firstName() == null || owner.firstName().isBlank()) {
      validationErrors.add("Owners first name cannot be empty");
      if (owner.firstName().length() > 255) {
        validationErrors.add("Owners first name too long: Longer than 255 characters");
      }
    }

    if (owner.lastName() == null || owner.lastName().isBlank()) {
      validationErrors.add("Owners last name cannot be empty");
      if (owner.lastName().length() > 255) {
        validationErrors.add("Owners last name too long: Longer than 255 characters");
      }
    }

    if (owner.email() != null) {
      if (owner.email().isBlank()) {
        validationErrors.add("Owner email is given but blank");
      }
      if (owner.email().length() > 255) {
        validationErrors.add("Owner email too long: longer than 255 characters");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of owner for create failed", validationErrors);
    }
  }
}
