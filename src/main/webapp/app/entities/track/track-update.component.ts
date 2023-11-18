import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import AlbumService from '@/entities/album/album.service';
import { IAlbum } from '@/shared/model/album.model';

import { ITrack, Track } from '@/shared/model/track.model';
import TrackService from './track.service';

const validations: any = {
  track: {
    name: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class TrackUpdate extends Vue {
  @Inject('trackService') private trackService: () => TrackService;
  @Inject('alertService') private alertService: () => AlertService;

  public track: ITrack = new Track();

  @Inject('albumService') private albumService: () => AlbumService;

  public albums: IAlbum[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.trackId) {
        vm.retrieveTrack(to.params.trackId);
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
    if (this.track.id) {
      this.trackService()
        .update(this.track)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.track.updated', { param: param.id });
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
      this.trackService()
        .create(this.track)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.track.created', { param: param.id });
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

  public retrieveTrack(trackId): void {
    this.trackService()
      .find(trackId)
      .then(res => {
        this.track = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.albumService()
      .retrieve()
      .then(res => {
        this.albums = res.data;
      });
  }
}
