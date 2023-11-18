import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import { IArtist, Artist } from '@/shared/model/artist.model';
import ArtistService from './artist.service';

const validations: any = {
  artist: {
    name: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class ArtistUpdate extends Vue {
  @Inject('artistService') private artistService: () => ArtistService;
  @Inject('alertService') private alertService: () => AlertService;

  public artist: IArtist = new Artist();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.artistId) {
        vm.retrieveArtist(to.params.artistId);
      }
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
    if (this.artist.id) {
      this.artistService()
        .update(this.artist)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.artist.updated', { param: param.id });
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
      this.artistService()
        .create(this.artist)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.artist.created', { param: param.id });
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

  public retrieveArtist(artistId): void {
    this.artistService()
      .find(artistId)
      .then(res => {
        this.artist = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
