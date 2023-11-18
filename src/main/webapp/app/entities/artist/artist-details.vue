<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="artist">
        <h2 class="jh-entity-heading" data-cy="artistDetailsHeading">
          <span v-text="$t('bootifulmusicApp.artist.detail.title')">Artist</span> {{ artist.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('bootifulmusicApp.artist.name')">Name</span>
          </dt>
          <dd>
            <span>{{ artist.name }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="artist.id" :to="{ name: 'ArtistEdit', params: { artistId: artist.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./artist-details.component.ts"></script>
