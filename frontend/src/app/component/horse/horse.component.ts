import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseSearch} from '../../dto/horse';
import {Owner} from '../../dto/owner';
import {Router} from '@angular/router';
import {Sex} from '../../dto/sex';
import {OwnerService} from '../../service/owner.service';
import {of} from 'rxjs';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: Horse[] = [];
  horseSearch: HorseSearch = {};
  bannerError: string | null = null;

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  searchHorses() {
    console.log(this.horseSearch);
    this.service.search(this.horseSearch)
      .subscribe({
        next: data => {
          this.horses = data;
    }}
      );
  }

  public onDelete(id: number, name: string): void {
    const observable = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${name} successfully deleted.`);
        this.reloadHorses();
      },
      error: error => {
        console.error('Error deleting horse', error);
      }
    });
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  ownerName(owner: Owner | null): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

}
