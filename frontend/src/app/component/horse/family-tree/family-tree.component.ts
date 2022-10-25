import {Component, OnInit} from '@angular/core';
import {Horse, HorseNode} from '../../../dto/horse';
import {Sex} from '../../../dto/sex';
import {NestedTreeControl} from '@angular/cdk/tree';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {HorseService} from '../../../service/horse.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-family-tree',
  templateUrl: './family-tree.component.html',
  styleUrls: ['./family-tree.component.scss']
})
export class FamilyTreeComponent implements OnInit {
  id = -1;
  depth = 5;
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };
  treeData: HorseNode = {};
  treeControl = new NestedTreeControl<HorseNode>(node => node.parents);
  dataSource = new MatTreeNestedDataSource<HorseNode>();


  constructor(
    private service: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params.id;
    });
    this.reloadTree();
  }

  reloadTree(): void {
    this.service.getById(this.id).subscribe({
      next: data => {
        this.horse = data;
        this.treeData = this.buildHorseNodes(this.horse, this.treeData, this.depth);
        this.dataSource.data = [];
        this.dataSource.data = [this.treeData];
      },
      error: error => {
        this.notification.error('Horse with the given id was not found');
        this.router.navigate(['/horses']);
      }
    });
  }

  buildHorseNodes(horse: Horse, horseNode: HorseNode, depth: number): HorseNode {
    horseNode.id = horse.id;
    horseNode.name = horse.name;
    horseNode.dateOfBirth = horse.dateOfBirth;
    horseNode.parents = [];
    if (horse.father != null && depth > 1) {
      const fatherHorseNode: HorseNode = {};
      horseNode.parents.push(this.buildHorseNodes(horse.father, fatherHorseNode, depth - 1));
    }
    if (horse.mother != null && depth > 1) {
      const motherHorseNode: HorseNode = {};
      horseNode.parents.push(this.buildHorseNodes(horse.mother, motherHorseNode, depth - 1));
    }

    return horseNode;

  }

  hasChild = (_: number, node: HorseNode) => !!node.parents && node.parents.length > 0;

  public onDelete(id: number, name: string): void {
    const observable = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse ${name} successfully deleted.`);
        this.reloadTree();
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error(error.error.errors, error.error.message);
      }
    });
  }

}
