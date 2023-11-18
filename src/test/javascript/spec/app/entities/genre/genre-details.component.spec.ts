/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import GenreDetailComponent from '@/entities/genre/genre-details.vue';
import GenreClass from '@/entities/genre/genre-details.component';
import GenreService from '@/entities/genre/genre.service';
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
  describe('Genre Management Detail Component', () => {
    let wrapper: Wrapper<GenreClass>;
    let comp: GenreClass;
    let genreServiceStub: SinonStubbedInstance<GenreService>;

    beforeEach(() => {
      genreServiceStub = sinon.createStubInstance<GenreService>(GenreService);

      wrapper = shallowMount<GenreClass>(GenreDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { genreService: () => genreServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundGenre = { id: 123 };
        genreServiceStub.find.resolves(foundGenre);

        // WHEN
        comp.retrieveGenre(123);
        await comp.$nextTick();

        // THEN
        expect(comp.genre).toBe(foundGenre);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundGenre = { id: 123 };
        genreServiceStub.find.resolves(foundGenre);

        // WHEN
        comp.beforeRouteEnter({ params: { genreId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.genre).toBe(foundGenre);
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
