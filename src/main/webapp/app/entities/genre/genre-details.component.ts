import { Component, Vue, Inject } from 'vue-property-decorator';

import { IGenre } from '@/shared/model/genre.model';
import GenreService from './genre.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class GenreDetails extends Vue {
  @Inject('genreService') private genreService: () => GenreService;
  @Inject('alertService') private alertService: () => AlertService;

  public genre: IGenre = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.genreId) {
        vm.retrieveGenre(to.params.genreId);
      }
    });
  }

  public retrieveGenre(genreId) {
    this.genreService()
      .find(genreId)
      .then(res => {
        this.genre = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
