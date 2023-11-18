/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import TrackUpdateComponent from '@/entities/track/track-update.vue';
import TrackClass from '@/entities/track/track-update.component';
import TrackService from '@/entities/track/track.service';

import AlbumService from '@/entities/album/album.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Track Management Update Component', () => {
    let wrapper: Wrapper<TrackClass>;
    let comp: TrackClass;
    let trackServiceStub: SinonStubbedInstance<TrackService>;

    beforeEach(() => {
      trackServiceStub = sinon.createStubInstance<TrackService>(TrackService);

      wrapper = shallowMount<TrackClass>(TrackUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          trackService: () => trackServiceStub,
          alertService: () => new AlertService(),

          albumService: () =>
            sinon.createStubInstance<AlbumService>(AlbumService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.track = entity;
        trackServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trackServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.track = entity;
        trackServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trackServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundTrack = { id: 123 };
        trackServiceStub.find.resolves(foundTrack);
        trackServiceStub.retrieve.resolves([foundTrack]);

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
