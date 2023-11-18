<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="track">
        <h2 class="jh-entity-heading" data-cy="trackDetailsHeading">
          <span v-text="$t('bootifulmusicApp.track.detail.title')">Track</span> {{ track.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('bootifulmusicApp.track.name')">Name</span>
          </dt>
          <dd>
            <span>{{ track.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('bootifulmusicApp.track.album')">Album</span>
          </dt>
          <dd>
            <div v-if="track.album">
              <router-link :to="{ name: 'AlbumView', params: { albumId: track.album.id } }">{{ track.album.name }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="track.id" :to="{ name: 'TrackEdit', params: { trackId: track.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./track-details.component.ts"></script>
