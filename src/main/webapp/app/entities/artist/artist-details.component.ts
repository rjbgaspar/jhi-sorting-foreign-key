import { Component, Vue, Inject } from 'vue-property-decorator';

import { IArtist } from '@/shared/model/artist.model';
import ArtistService from './artist.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class ArtistDetails extends Vue {
  @Inject('artistService') private artistService: () => ArtistService;
  @Inject('alertService') private alertService: () => AlertService;

  public artist: IArtist = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.artistId) {
        vm.retrieveArtist(to.params.artistId);
      }
    });
  }

  public retrieveArtist(artistId) {
    this.artistService()
      .find(artistId)
      .then(res => {
        this.artist = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
