import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseSearch} from '../dto/horse';
import {Sex} from '../dto/sex';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }

  /**
   * Get a specific horse by id
   *
   * @param id the id of a horse to get
   * @returns an Observable for the found horse
   */
  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(baseUri + `/${id}`);
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Edit a horse in the system.
   *
   * @param horse the data for the horse to be edited
   * @return an Observable for the edited horse
   */
  edit(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(
      baseUri + `/${horse.id}`,
      horse
    );
  }

  /**
   * Delete a horse in the system.
   *
   * @param id the id of the horse to be deleted
   */
  delete(id: number): Observable<Horse> {
    return this.http.delete<Horse>(
      baseUri + `/${id}`
    );
  }

  searchByName(name: string, sex: Sex, limitTo: number): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('sex', sex);

    return this.http.get<Horse[]>(baseUri, {params});

  }

  search(horseSearch: HorseSearch): Observable<Horse[]> {
    let params = new HttpParams();
    console.log(horseSearch);
    if (horseSearch.name) {
      params = params.set('name', horseSearch.name);
    }
    if (horseSearch.description) {
      params = params.set('description', horseSearch.description);
    }
    if (horseSearch.dateOfBirth) {
      params = params.set('bornBefore', horseSearch.dateOfBirth.toString());
    }
    if (horseSearch.sex) {
      params = params.set('sex', horseSearch.sex);
    }
    if (horseSearch.owner) {
      params = params.set('owner', horseSearch.owner);
    }
    return this.http.get<Horse[]>(baseUri, {params});
  }
}
