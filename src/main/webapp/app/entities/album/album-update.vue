<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="bootifulmusicApp.album.home.createOrEditLabel"
          data-cy="AlbumCreateUpdateHeading"
          v-text="$t('bootifulmusicApp.album.home.createOrEditLabel')"
        >
          Create or edit a Album
        </h2>
        <div>
          <div class="form-group" v-if="album.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="album.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('bootifulmusicApp.album.name')" for="album-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="album-name"
              data-cy="name"
              :class="{ valid: !$v.album.name.$invalid, invalid: $v.album.name.$invalid }"
              v-model="$v.album.name.$model"
              required
            />
            <div v-if="$v.album.name.$anyDirty && $v.album.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.album.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('bootifulmusicApp.album.artist')" for="album-artist">Artist</label>
            <select class="form-control" id="album-artist" data-cy="artist" name="artist" v-model="album.artist">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="album.artist && artistOption.id === album.artist.id ? album.artist : artistOption"
                v-for="artistOption in artists"
                :key="artistOption.id"
              >
                {{ artistOption.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('bootifulmusicApp.album.genre')" for="album-genre">Genre</label>
            <select class="form-control" id="album-genre" data-cy="genre" name="genre" v-model="album.genre">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="album.genre && genreOption.id === album.genre.id ? album.genre : genreOption"
                v-for="genreOption in genres"
                :key="genreOption.id"
              >
                {{ genreOption.name }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.album.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./album-update.component.ts"></script>
