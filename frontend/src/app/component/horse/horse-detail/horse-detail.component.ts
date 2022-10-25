import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router} from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import {of, Observable} from 'rxjs';
import {Horse} from '../../../dto/horse';
import {Owner} from '../../../dto/owner';
import {Sex} from '../../../dto/sex';
import {HorseService} from '../../../service/horse.service';


@Component({
  selector: 'app-horse-detail',
  templateUrl: './horse-detail.component.html',
  styleUrls: ['./horse-detail.component.scss']
})
export class HorseDetailComponent implements OnInit {

  id = -1;
  horse: Horse = {
    name: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };

  constructor(
    private service: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.route.params.subscribe(params => {
      this.id = params.id;
      this.service.getById(this.id).subscribe(data => {
        this.horse = data;
      });
    });
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public formatHorseName(horse: Horse | null | undefined): string {
    return (horse == null)
      ? ''
      : `${horse.name}`;
  }

  public formatHorseId(horse: Horse | null | undefined): string {
    return (horse == null)
      ? ''
      : `${horse.id}`;
  }

  public onEdit(): void {
    this.router.navigate([`/horses/edit/${this.id}`]);
  }

  public onDelete(): void {
    const observable = this.service.delete(this.id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        this.router.navigate(['/horses']);
      },
      error: error => {
        this.notification.error(error.message, error.errors);
      }
    });
  }

}
