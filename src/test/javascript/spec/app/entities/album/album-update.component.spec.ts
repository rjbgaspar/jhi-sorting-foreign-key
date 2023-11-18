/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import AlbumUpdateComponent from '@/entities/album/album-update.vue';
import AlbumClass from '@/entities/album/album-update.component';
import AlbumService from '@/entities/album/album.service';

import ArtistService from '@/entities/artist/artist.service';

import GenreService from '@/entities/genre/genre.service';

import TrackService from '@/entities/track/track.service';
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
  describe('Album Management Update Component', () => {
    let wrapper: Wrapper<AlbumClass>;
    let comp: AlbumClass;
    let albumServiceStub: SinonStubbedInstance<AlbumService>;

    beforeEach(() => {
      albumServiceStub = sinon.createStubInstance<AlbumService>(AlbumService);

      wrapper = shallowMount<AlbumClass>(AlbumUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          albumService: () => albumServiceStub,
          alertService: () => new AlertService(),

          artistService: () =>
            sinon.createStubInstance<ArtistService>(ArtistService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          genreService: () =>
            sinon.createStubInstance<GenreService>(GenreService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          trackService: () =>
            sinon.createStubInstance<TrackService>(TrackService, {
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
        comp.album = entity;
        albumServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(albumServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.album = entity;
        albumServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(albumServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundAlbum = { id: 123 };
        albumServiceStub.find.resolves(foundAlbum);
        albumServiceStub.retrieve.resolves([foundAlbum]);

        // WHEN
        comp.beforeRouteEnter({ params: { albumId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.album).toBe(foundAlbum);
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
