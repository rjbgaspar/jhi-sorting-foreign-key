<template>
  <div>
    <b-dropdown-item to="/artist">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.artist')">Artist</span>
    </b-dropdown-item>
    <b-dropdown-item to="/genre">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.genre')">Genre</span>
    </b-dropdown-item>
    <b-dropdown-item to="/track">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.track')">Track</span>
    </b-dropdown-item>
    <b-dropdown-item to="/album">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.album')">Album</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
