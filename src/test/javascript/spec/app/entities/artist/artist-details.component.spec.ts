/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ArtistDetailComponent from '@/entities/artist/artist-details.vue';
import ArtistClass from '@/entities/artist/artist-details.component';
import ArtistService from '@/entities/artist/artist.service';
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
  describe('Artist Management Detail Component', () => {
    let wrapper: Wrapper<ArtistClass>;
    let comp: ArtistClass;
    let artistServiceStub: SinonStubbedInstance<ArtistService>;

    beforeEach(() => {
      artistServiceStub = sinon.createStubInstance<ArtistService>(ArtistService);

      wrapper = shallowMount<ArtistClass>(ArtistDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { artistService: () => artistServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundArtist = { id: 123 };
        artistServiceStub.find.resolves(foundArtist);

        // WHEN
        comp.retrieveArtist(123);
        await comp.$nextTick();

        // THEN
        expect(comp.artist).toBe(foundArtist);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundArtist = { id: 123 };
        artistServiceStub.find.resolves(foundArtist);

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
