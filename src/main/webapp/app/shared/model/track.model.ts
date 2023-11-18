import { IAlbum } from '@/shared/model/album.model';

export interface ITrack {
  id?: number;
  name?: string;
  album?: IAlbum | null;
}

export class Track implements ITrack {
  constructor(public id?: number, public name?: string, public album?: IAlbum | null) {}
}
