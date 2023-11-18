/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import ArtistUpdateComponent from '@/entities/artist/artist-update.vue';
import ArtistClass from '@/entities/artist/artist-update.component';
import ArtistService from '@/entities/artist/artist.service';

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
  describe('Artist Management Update Component', () => {
    let wrapper: Wrapper<ArtistClass>;
    let comp: ArtistClass;
    let artistServiceStub: SinonStubbedInstance<ArtistService>;

    beforeEach(() => {
      artistServiceStub = sinon.createStubInstance<ArtistService>(ArtistService);

      wrapper = shallowMount<ArtistClass>(ArtistUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          artistService: () => artistServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.artist = entity;
        artistServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(artistServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.artist = entity;
        artistServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(artistServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundArtist = { id: 123 };
        artistServiceStub.find.resolves(foundArtist);
        artistServiceStub.retrieve.resolves([foundArtist]);

        // WHEN
        comp.beforeRouteEnter({ params: { artistId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.artist).toBe(foundArtist);
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
