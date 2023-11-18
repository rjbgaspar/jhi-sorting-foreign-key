import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Artist = () => import('@/entities/artist/artist.vue');
const ArtistUpdate = () => import('@/entities/artist/artist-update.vue');
const ArtistDetails = () => import('@/entities/artist/artist-details.vue');

const Genre = () => import('@/entities/genre/genre.vue');
const GenreUpdate = () => import('@/entities/genre/genre-update.vue');
const GenreDetails = () => import('@/entities/genre/genre-details.vue');

const Track = () => import('@/entities/track/track.vue');
const TrackUpdate = () => import('@/entities/track/track-update.vue');
const TrackDetails = () => import('@/entities/track/track-details.vue');

const Album = () => import('@/entities/album/album.vue');
const AlbumUpdate = () => import('@/entities/album/album-update.vue');
const AlbumDetails = () => import('@/entities/album/album-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'artist',
      name: 'Artist',
      component: Artist,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'artist/new',
      name: 'ArtistCreate',
      component: ArtistUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'artist/:artistId/edit',
      name: 'ArtistEdit',
      component: ArtistUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'artist/:artistId/view',
      name: 'ArtistView',
      component: ArtistDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'genre',
      name: 'Genre',
      component: Genre,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'genre/new',
      name: 'GenreCreate',
      component: GenreUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'genre/:genreId/edit',
      name: 'GenreEdit',
      component: GenreUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'genre/:genreId/view',
      name: 'GenreView',
      component: GenreDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'track',
      name: 'Track',
      component: Track,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'track/new',
      name: 'TrackCreate',
      component: TrackUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'track/:trackId/edit',
      name: 'TrackEdit',
      component: TrackUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'track/:trackId/view',
      name: 'TrackView',
      component: TrackDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'album',
      name: 'Album',
      component: Album,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'album/new',
      name: 'AlbumCreate',
      component: AlbumUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'album/:albumId/edit',
      name: 'AlbumEdit',
      component: AlbumUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'album/:albumId/view',
      name: 'AlbumView',
      component: AlbumDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
