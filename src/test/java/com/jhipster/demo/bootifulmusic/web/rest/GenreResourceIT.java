package com.jhipster.demo.bootifulmusic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jhipster.demo.bootifulmusic.IntegrationTest;
import com.jhipster.demo.bootifulmusic.domain.Genre;
import com.jhipster.demo.bootifulmusic.repository.EntityManager;
import com.jhipster.demo.bootifulmusic.repository.GenreRepository;
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
 * Integration tests for the {@link GenreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class GenreResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/genres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Genre genre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genre createEntity(EntityManager em) {
        Genre genre = new Genre().name(DEFAULT_NAME);
        return genre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genre createUpdatedEntity(EntityManager em) {
        Genre genre = new Genre().name(UPDATED_NAME);
        return genre;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Genre.class).block();
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
        genre = createEntity(em);
    }

    @Test
    void createGenre() throws Exception {
        int databaseSizeBeforeCreate = genreRepository.findAll().collectList().block().size();
        // Create the Genre
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeCreate + 1);
        Genre testGenre = genreList.get(genreList.size() - 1);
        assertThat(testGenre.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createGenreWithExistingId() throws Exception {
        // Create the Genre with an existing ID
        genre.setId(1L);

        int databaseSizeBeforeCreate = genreRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = genreRepository.findAll().collectList().block().size();
        // set the field null
        genre.setName(null);

        // Create the Genre, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllGenres() {
        // Initialize the database
        genreRepository.save(genre).block();

        // Get all the genreList
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
            .value(hasItem(genre.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getGenre() {
        // Initialize the database
        genreRepository.save(genre).block();

        // Get the genre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, genre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(genre.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingGenre() {
        // Get the genre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingGenre() throws Exception {
        // Initialize the database
        genreRepository.save(genre).block();

        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();

        // Update the genre
        Genre updatedGenre = genreRepository.findById(genre.getId()).block();
        updatedGenre.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedGenre.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedGenre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
        Genre testGenre = genreList.get(genreList.size() - 1);
        assertThat(testGenre.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingGenre() throws Exception {
        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();
        genre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, genre.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchGenre() throws Exception {
        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();
        genre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamGenre() throws Exception {
        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();
        genre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateGenreWithPatch() throws Exception {
        // Initialize the database
        genreRepository.save(genre).block();

        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();

        // Update the genre using partial update
        Genre partialUpdatedGenre = new Genre();
        partialUpdatedGenre.setId(genre.getId());

        partialUpdatedGenre.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGenre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedGenre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
        Genre testGenre = genreList.get(genreList.size() - 1);
        assertThat(testGenre.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateGenreWithPatch() throws Exception {
        // Initialize the database
        genreRepository.save(genre).block();

        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();

        // Update the genre using partial update
        Genre partialUpdatedGenre = new Genre();
        partialUpdatedGenre.setId(genre.getId());

        partialUpdatedGenre.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGenre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedGenre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
        Genre testGenre = genreList.get(genreList.size() - 1);
        assertThat(testGenre.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingGenre() throws Exception {
        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();
        genre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, genre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchGenre() throws Exception {
        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();
        genre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamGenre() throws Exception {
        int databaseSizeBeforeUpdate = genreRepository.findAll().collectList().block().size();
        genre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(genre))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Genre in the database
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteGenre() {
        // Initialize the database
        genreRepository.save(genre).block();

        int databaseSizeBeforeDelete = genreRepository.findAll().collectList().block().size();

        // Delete the genre
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, genre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Genre> genreList = genreRepository.findAll().collectList().block();
        assertThat(genreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
