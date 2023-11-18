<template>
  <div>
    <h2 id="page-heading" data-cy="ArtistHeading">
      <span v-text="$t('bootifulmusicApp.artist.home.title')" id="artist-heading">Artists</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('bootifulmusicApp.artist.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'ArtistCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-artist"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('bootifulmusicApp.artist.home.createLabel')"> Create a new Artist </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && artists && artists.length === 0">
      <span v-text="$t('bootifulmusicApp.artist.home.notFound')">No artists found</span>
    </div>
    <div class="table-responsive" v-if="artists && artists.length > 0">
      <table class="table table-striped" aria-describedby="artists">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="$t('bootifulmusicApp.artist.name')">Name</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="artist in artists" :key="artist.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ArtistView', params: { artistId: artist.id } }">{{ artist.id }}</router-link>
            </td>
            <td>{{ artist.name }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ArtistView', params: { artistId: artist.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ArtistEdit', params: { artistId: artist.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(artist)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="bootifulmusicApp.artist.delete.question" data-cy="artistDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-artist-heading" v-text="$t('bootifulmusicApp.artist.delete.question', { id: removeId })">
          Are you sure you want to delete this Artist?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-artist"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeArtist()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="artists && artists.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./artist.component.ts"></script>
