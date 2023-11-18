import { IArtist } from '@/shared/model/artist.model';
import { IGenre } from '@/shared/model/genre.model';
import { ITrack } from '@/shared/model/track.model';

export interface IAlbum {
  id?: number;
  name?: string;
  artist?: IArtist | null;
  genre?: IGenre | null;
  tracks?: ITrack[] | null;
}

export class Album implements IAlbum {
  constructor(
    public id?: number,
    public name?: string,
    public artist?: IArtist | null,
    public genre?: IGenre | null,
    public tracks?: ITrack[] | null
  ) {}
}
