import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {
  HorseCreateEditComponent,
  HorseCreateEditMode
} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {HorseDetailComponent} from './component/horse/horse-detail/horse-detail.component';
import {OwnerComponent} from './component/owner/owner.component';
import {OwnerCreateComponent} from './component/owner/owner-create/owner-create.component';
import {FamilyTreeComponent} from './component/horse/family-tree/family-tree.component';

const routes: Routes = [
  {path: '', redirectTo: 'horses', pathMatch: 'full'},
  {path: 'horses', children: [
    {path: '', component: HorseComponent},
    {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
    {path: 'edit/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit}},
    {path: 'detail/:id', component: HorseDetailComponent},
    {path: 'tree/:id', component: FamilyTreeComponent},
  ]},
  {path: 'owners', children: [
      {path: '', component: OwnerComponent},
      {path: 'create', component: OwnerCreateComponent}
    ]},
  {path: '**', redirectTo: 'horses'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
