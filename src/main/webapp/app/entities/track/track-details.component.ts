import { Component, Vue, Inject } from 'vue-property-decorator';

import { ITrack } from '@/shared/model/track.model';
import TrackService from './track.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class TrackDetails extends Vue {
  @Inject('trackService') private trackService: () => TrackService;
  @Inject('alertService') private alertService: () => AlertService;

  public track: ITrack = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.trackId) {
        vm.retrieveTrack(to.params.trackId);
      }
    });
  }

  public retrieveTrack(trackId) {
    this.trackService()
      .find(trackId)
      .then(res => {
        this.track = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
