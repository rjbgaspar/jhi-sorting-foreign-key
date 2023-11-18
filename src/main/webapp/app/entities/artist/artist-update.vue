<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="bootifulmusicApp.artist.home.createOrEditLabel"
          data-cy="ArtistCreateUpdateHeading"
          v-text="$t('bootifulmusicApp.artist.home.createOrEditLabel')"
        >
          Create or edit a Artist
        </h2>
        <div>
          <div class="form-group" v-if="artist.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="artist.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('bootifulmusicApp.artist.name')" for="artist-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="artist-name"
              data-cy="name"
              :class="{ valid: !$v.artist.name.$invalid, invalid: $v.artist.name.$invalid }"
              v-model="$v.artist.name.$model"
              required
            />
            <div v-if="$v.artist.name.$anyDirty && $v.artist.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.artist.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
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
            :disabled="$v.artist.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./artist-update.component.ts"></script>
