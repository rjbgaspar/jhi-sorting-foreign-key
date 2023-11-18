import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import ArtistService from './artist/artist.service';
import GenreService from './genre/genre.service';
import TrackService from './track/track.service';
import AlbumService from './album/album.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('artistService') private artistService = () => new ArtistService();
  @Provide('genreService') private genreService = () => new GenreService();
  @Provide('trackService') private trackService = () => new TrackService();
  @Provide('albumService') private albumService = () => new AlbumService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
