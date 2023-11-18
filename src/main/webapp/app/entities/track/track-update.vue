<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="bootifulmusicApp.track.home.createOrEditLabel"
          data-cy="TrackCreateUpdateHeading"
          v-text="$t('bootifulmusicApp.track.home.createOrEditLabel')"
        >
          Create or edit a Track
        </h2>
        <div>
          <div class="form-group" v-if="track.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="track.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('bootifulmusicApp.track.name')" for="track-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="track-name"
              data-cy="name"
              :class="{ valid: !$v.track.name.$invalid, invalid: $v.track.name.$invalid }"
              v-model="$v.track.name.$model"
              required
            />
            <div v-if="$v.track.name.$anyDirty && $v.track.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.track.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('bootifulmusicApp.track.album')" for="track-album">Album</label>
            <select class="form-control" id="track-album" data-cy="album" name="album" v-model="track.album">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="track.album && albumOption.id === track.album.id ? track.album : albumOption"
                v-for="albumOption in albums"
                :key="albumOption.id"
              >
                {{ albumOption.name }}
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
            :disabled="$v.track.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./track-update.component.ts"></script>
