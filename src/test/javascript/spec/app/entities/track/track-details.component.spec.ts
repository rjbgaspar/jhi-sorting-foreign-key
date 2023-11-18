/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import TrackDetailComponent from '@/entities/track/track-details.vue';
import TrackClass from '@/entities/track/track-details.component';
import TrackService from '@/entities/track/track.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Track Management Detail Component', () => {
    let wrapper: Wrapper<TrackClass>;
    let comp: TrackClass;
    let trackServiceStub: SinonStubbedInstance<TrackService>;

    beforeEach(() => {
      trackServiceStub = sinon.createStubInstance<TrackService>(TrackService);

      wrapper = shallowMount<TrackClass>(TrackDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { trackService: () => trackServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundTrack = { id: 123 };
        trackServiceStub.find.resolves(foundTrack);

        // WHEN
        comp.retrieveTrack(123);
        await comp.$nextTick();

        // THEN
        expect(comp.track).toBe(foundTrack);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundTrack = { id: 123 };
        trackServiceStub.find.resolves(foundTrack);

        // WHEN
        comp.beforeRouteEnter({ params: { trackId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.track).toBe(foundTrack);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
