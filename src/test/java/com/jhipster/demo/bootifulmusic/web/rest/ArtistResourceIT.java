package com.jhipster.demo.bootifulmusic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jhipster.demo.bootifulmusic.IntegrationTest;
import com.jhipster.demo.bootifulmusic.domain.Artist;
import com.jhipster.demo.bootifulmusic.repository.ArtistRepository;
import com.jhipster.demo.bootifulmusic.repository.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ArtistResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ArtistResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/artists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Artist artist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createEntity(EntityManager em) {
        Artist artist = new Artist().name(DEFAULT_NAME);
        return artist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createUpdatedEntity(EntityManager em) {
        Artist artist = new Artist().name(UPDATED_NAME);
        return artist;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Artist.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        artist = createEntity(em);
    }

    @Test
    void createArtist() throws Exception {
        int databaseSizeBeforeCreate = artistRepository.findAll().collectList().block().size();
        // Create the Artist
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeCreate + 1);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createArtistWithExistingId() throws Exception {
        // Create the Artist with an existing ID
        artist.setId(1L);

        int databaseSizeBeforeCreate = artistRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setName(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllArtists() {
        // Initialize the database
        artistRepository.save(artist).block();

        // Get all the artistList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(artist.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getArtist() {
        // Initialize the database
        artistRepository.save(artist).block();

        // Get the artist
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(artist.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingArtist() {
        // Get the artist
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingArtist() throws Exception {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();

        // Update the artist
        Artist updatedArtist = artistRepository.findById(artist.getId()).block();
        updatedArtist.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedArtist.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedArtist))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateArtistWithPatch() throws Exception {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();

        // Update the artist using partial update
        Artist partialUpdatedArtist = new Artist();
        partialUpdatedArtist.setId(artist.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArtist.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArtist))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void fullUpdateArtistWithPatch() throws Exception {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();

        // Update the artist using partial update
        Artist partialUpdatedArtist = new Artist();
        partialUpdatedArtist.setId(artist.getId());

        partialUpdatedArtist.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArtist.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArtist))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArtist() {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeDelete = artistRepository.findAll().collectList().block().size();

        // Delete the artist
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
