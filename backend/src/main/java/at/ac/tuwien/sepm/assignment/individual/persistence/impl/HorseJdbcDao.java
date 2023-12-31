package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String TABLE_NAME_OWNER = "owner";
  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , father_id = ?"
      + "  , mother_id = ?"
      + " WHERE id = ?";
  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME
      + " (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
  private static final String SQL_SELECT_SEARCH = "SELECT * FROM " + TABLE_NAME + " LEFT JOIN " + TABLE_NAME_OWNER + " ON horse.owner_id=owner.id WHERE TRUE";
  private static final String SQL_SELECT_SEARCH_NAME = " AND UPPER(name) LIKE UPPER('%'||COALESCE(?,'')||'%')";
  private static final String SQL_SELECT_SEARCH_DESCRIPTION = " AND UPPER(description) LIKE UPPER('%'||COALESCE(?,'')||'%')";
  private static final String SQL_SELECT_SEARCH_DATE_OF_BIRTH = " AND date_of_birth <= ?";
  private static final String SQL_SELECT_SEARCH_SEX = " AND sex LIKE COALESCE(?,'%')";
  private static final String SQL_SELECT_SEARCH_OWNER_NAME = " AND UPPER(first_name||' '||last_name) like UPPER('%'||COALESCE(?, '')||'%')";
  private static final String SQL_SELECT_SEARCH_LIMIT_CLAUSE = " LIMIT ?";
  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public List<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    var params = new ArrayList<>();
    if (searchParameters.name() != null) {
      query += SQL_SELECT_SEARCH_NAME;
      params.add(searchParameters.name());
    }
    if (searchParameters.description() != null) {
      query += SQL_SELECT_SEARCH_DESCRIPTION;
      params.add(searchParameters.description());
    }
    if (searchParameters.bornBefore() != null) {
      query += SQL_SELECT_SEARCH_DATE_OF_BIRTH;
      params.add(Date.valueOf(searchParameters.bornBefore()).toString());
    }
    if (searchParameters.sex() != null) {
      query += SQL_SELECT_SEARCH_SEX;
      params.add(searchParameters.sex().toString());
    }
    if (searchParameters.owner() != null) {
      query += SQL_SELECT_SEARCH_OWNER_NAME;
      params.add(searchParameters.owner());
    }
    LOG.info(params.toString());
    var limit = searchParameters.limit();
    if (limit != null) {
      query += SQL_SELECT_SEARCH_LIMIT_CLAUSE;
      params.add(limit);
    }
    return jdbcTemplate.query(query, this::mapRow, params.toArray());
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      LOG.error("Too many horses with ID %d found".formatted(id));
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }

  @Override
  public Horse create(HorseCreateDto newHorse) {
    LOG.trace("create({})", newHorse);

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement stmt = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, newHorse.name());
      stmt.setString(2, newHorse.description());
      stmt.setDate(3, Date.valueOf(newHorse.dateOfBirth()));
      stmt.setString(4, newHorse.sex().toString());
      stmt.setObject(5, newHorse.ownerId());
      stmt.setObject(6, newHorse.fatherId());
      stmt.setObject(7, newHorse.motherId());
      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key == null) {
      // This should never happen. If it does, something is wrong with the DB or the way the prepared statement is set up.
      LOG.error("Could not extract key for newly created owner. There is probably a programming error…");
      throw new FatalException("Could not extract key for newly created owner. There is probably a programming error…");
    }

    return new Horse()
        .setId(key.longValue())
        .setName(newHorse.name())
        .setDescription(newHorse.description())
        .setDateOfBirth(newHorse.dateOfBirth())
        .setSex(newHorse.sex())
        .setOwnerId(newHorse.ownerId())
        .setFatherId(newHorse.fatherId())
        .setMotherId(newHorse.motherId())
        ;
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete ({})", id);

    int deleted = jdbcTemplate.update(SQL_DELETE, id);
    if (deleted == 0) {
      throw new NotFoundException("Could not delete horse with id " + id + " because it was not found");
    }
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.fatherId(),
        horse.motherId(),
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        .setFatherId(horse.fatherId())
        .setMotherId(horse.motherId())
        ;
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        .setFatherId(result.getObject("father_id", Long.class))
        .setMotherId(result.getObject("mother_id", Long.class))
        ;
  }
}
