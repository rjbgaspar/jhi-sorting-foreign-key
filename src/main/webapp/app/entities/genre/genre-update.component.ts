import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import { IGenre, Genre } from '@/shared/model/genre.model';
import GenreService from './genre.service';

const validations: any = {
  genre: {
    name: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class GenreUpdate extends Vue {
  @Inject('genreService') private genreService: () => GenreService;
  @Inject('alertService') private alertService: () => AlertService;

  public genre: IGenre = new Genre();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.genreId) {
        vm.retrieveGenre(to.params.genreId);
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
    if (this.genre.id) {
      this.genreService()
        .update(this.genre)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.genre.updated', { param: param.id });
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
      this.genreService()
        .create(this.genre)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('bootifulmusicApp.genre.created', { param: param.id });
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

  public retrieveGenre(genreId): void {
    this.genreService()
      .find(genreId)
      .then(res => {
        this.genre = res;
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
