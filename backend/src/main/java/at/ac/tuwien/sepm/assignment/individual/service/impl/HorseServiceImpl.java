package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      LOG.error("Horse, that is already persisted, refers to non-existing owner", e);
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(
            horse,
            ownerMap,
            fatherMapForSingleId(horse.getFatherId()),
            motherMapForSingleId(horse.getMotherId())));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.getOwnerId()),
        fatherMapForSingleId(updatedHorse.getFatherId()),
        motherMapForSingleId(updatedHorse.getMotherId())
        );
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()),
        fatherMapForSingleId(horse.getFatherId()),
        motherMapForSingleId(horse.getMotherId())
    );
  }

  @Override
  public HorseDetailDto create(HorseCreateDto newHorse) throws ValidationException, ConflictException {
    LOG.trace("create({})", newHorse);
    validator.validateForCreate(newHorse);
    var createdHorse = dao.create(newHorse);
    return mapper.entityToDetailDto(
        createdHorse,
        ownerMapForSingleId(createdHorse.getOwnerId()),
        fatherMapForSingleId(createdHorse.getFatherId()),
        motherMapForSingleId(createdHorse.getMotherId())
    );
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    dao.delete(id);
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var horses = dao.search(searchParameters);
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      LOG.error("Horse, that is already persisted, refers to non-existing owner", e);
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(
            horse,
            ownerMap,
            fatherMapForSingleId(horse.getFatherId()),
            motherMapForSingleId(horse.getMotherId())
        ));

  }


  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      LOG.error("Owner %d referenced by horse not found".formatted(ownerId));
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

  private Map<Long, HorseDetailDto> fatherMapForSingleId(Long fatherId) {
    try {
      return  fatherId == null
          ? null
          : Collections.singletonMap(fatherId, getById(fatherId));
    } catch (NotFoundException e) {
      LOG.error("Father %d referenced by horse not found".formatted(fatherId));
      throw new FatalException("Father %d referenced by horse not found".formatted(fatherId));
    }
  }

  private Map<Long, HorseDetailDto> motherMapForSingleId(Long motherId) {
    try {
      return motherId == null
          ? null
          : Collections.singletonMap(motherId, getById(motherId));
    } catch (NotFoundException e) {
      LOG.error("Mother %d referenced by horse not found".formatted(motherId));
      throw new FatalException("Mother %d referenced by horse not found".formatted(motherId));
    }
  }

}
