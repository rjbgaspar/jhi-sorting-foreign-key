import { Component, Vue, Inject } from 'vue-property-decorator';

import { IAlbum } from '@/shared/model/album.model';
import AlbumService from './album.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class AlbumDetails extends Vue {
  @Inject('albumService') private albumService: () => AlbumService;
  @Inject('alertService') private alertService: () => AlertService;

  public album: IAlbum = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.albumId) {
        vm.retrieveAlbum(to.params.albumId);
      }
    });
  }

  public retrieveAlbum(albumId) {
    this.albumService()
      .find(albumId)
      .then(res => {
        this.album = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
