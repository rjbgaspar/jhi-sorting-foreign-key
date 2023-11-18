<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="album">
        <h2 class="jh-entity-heading" data-cy="albumDetailsHeading">
          <span v-text="$t('bootifulmusicApp.album.detail.title')">Album</span> {{ album.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('bootifulmusicApp.album.name')">Name</span>
          </dt>
          <dd>
            <span>{{ album.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('bootifulmusicApp.album.artist')">Artist</span>
          </dt>
          <dd>
            <div v-if="album.artist">
              <router-link :to="{ name: 'ArtistView', params: { artistId: album.artist.id } }">{{ album.artist.name }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="$t('bootifulmusicApp.album.genre')">Genre</span>
          </dt>
          <dd>
            <div v-if="album.genre">
              <router-link :to="{ name: 'GenreView', params: { genreId: album.genre.id } }">{{ album.genre.name }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="album.id" :to="{ name: 'AlbumEdit', params: { albumId: album.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./album-details.component.ts"></script>
