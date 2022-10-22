import {Owner} from './owner';
import {Sex} from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  owner?: Owner;
  father?: Horse;
  mother?: Horse;
}


export interface HorseSearch {
  name?: string;
  description?: string;
  dateOfBirth?: Date;
  sex?: Sex;
  owner?: string;
}
