import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import ArtistService from '@/entities/artist/artist.service';
import { IArtist } from '@/shared/model/artist.model';

import GenreService from '@/entities/genre/genre.service';
import { IGenre } from '@/shared/model/genre.model';

import TrackService from '@/entities/track/track.service';
import { ITrack } from '@/shared/model/track.model';

import { IAlbum, Album } from '@/shared/model/album.model';
import AlbumService from './album.service';

const validations: any = {
  album: {
    name: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class AlbumUpdate extends Vue {
  @Inject('albumService') private albumService: () => AlbumService;
  @Inject('alertService') private alertService: () => AlertService;

  public album: IAlbum = new Album();

  @Inject('artistService') private artistService: () => ArtistService;

  public artists: IArtist[] = [];

  @Inject('genreService') private genreService: () => GenreService;

  public genres: IGenre[] = [];

  @Inject('trackService') private trackService: () => TrackService;

  public tracks: ITrack[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.albumId) {
        vm.retrieveAlbum(to.params.albumId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.album.id) {
      this.albumService()
        .update(this.album)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.album.updated', { param: param.id });
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.albumService()
        .create(this.album)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.album.created', { param: param.id });
          (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveAlbum(albumId): void {
    this.albumService()
      .find(albumId)
      .then(res => {
        this.album = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.artistService()
      .retrieve()
      .then(res => {
        this.artists = res.data;
      });
    this.genreService()
      .retrieve()
      .then(res => {
        this.genres = res.data;
      });
    this.trackService()
      .retrieve()
      .then(res => {
        this.tracks = res.data;
      });
  }
}
