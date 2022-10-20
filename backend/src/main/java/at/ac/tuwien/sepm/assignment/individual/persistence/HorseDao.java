package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();

  /**
   * Search for horses matching the criteria in {@code searchParameters}
   * <p>
   *   The returned stream of horses never contains more than {@code searchParameters.maxAmount} elements,
   *   even if the there would be more matches in the persistent data store.
   * </p>
   *
   * @param searchParameters object containing the search parameters to match
   * @return a stream containing horses matching the criteria in {@code searchParameters}
   */
  List<Horse> search(HorseSearchDto searchParameters);


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */


  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Create a new horse in the persistent data store.
   *
   * @param newHorse the data to create a new horse
   * @return the newly created horse
   */
  Horse create(HorseCreateDto newHorse);

  /**
   * Delete a horse by its ID from the persistent storage.
   *
   * @param id the id of the horse to delete
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent storage.
   */
  void delete(long id) throws NotFoundException;
}
