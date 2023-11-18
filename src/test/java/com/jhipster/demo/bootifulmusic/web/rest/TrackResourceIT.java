package com.jhipster.demo.bootifulmusic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.jhipster.demo.bootifulmusic.IntegrationTest;
import com.jhipster.demo.bootifulmusic.domain.Track;
import com.jhipster.demo.bootifulmusic.repository.EntityManager;
import com.jhipster.demo.bootifulmusic.repository.TrackRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link TrackResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrackResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tracks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TrackRepository trackRepository;

    @Mock
    private TrackRepository trackRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Track track;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Track createEntity(EntityManager em) {
        Track track = new Track().name(DEFAULT_NAME);
        return track;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Track createUpdatedEntity(EntityManager em) {
        Track track = new Track().name(UPDATED_NAME);
        return track;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Track.class).block();
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
        track = createEntity(em);
    }

    @Test
    void createTrack() throws Exception {
        int databaseSizeBeforeCreate = trackRepository.findAll().collectList().block().size();
        // Create the Track
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeCreate + 1);
        Track testTrack = trackList.get(trackList.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createTrackWithExistingId() throws Exception {
        // Create the Track with an existing ID
        track.setId(1L);

        int databaseSizeBeforeCreate = trackRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().collectList().block().size();
        // set the field null
        track.setName(null);

        // Create the Track, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTracks() {
        // Initialize the database
        trackRepository.save(track).block();

        // Get all the trackList
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
            .value(hasItem(track.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTracksWithEagerRelationshipsIsEnabled() {
        when(trackRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(trackRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTracksWithEagerRelationshipsIsNotEnabled() {
        when(trackRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(trackRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTrack() {
        // Initialize the database
        trackRepository.save(track).block();

        // Get the track
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, track.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(track.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingTrack() {
        // Get the track
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTrack() throws Exception {
        // Initialize the database
        trackRepository.save(track).block();

        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();

        // Update the track
        Track updatedTrack = trackRepository.findById(track.getId()).block();
        updatedTrack.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTrack.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTrack))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
        Track testTrack = trackList.get(trackList.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();
        track.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, track.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();
        track.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();
        track.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTrackWithPatch() throws Exception {
        // Initialize the database
        trackRepository.save(track).block();

        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();

        // Update the track using partial update
        Track partialUpdatedTrack = new Track();
        partialUpdatedTrack.setId(track.getId());

        partialUpdatedTrack.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrack.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTrack))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
        Track testTrack = trackList.get(trackList.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateTrackWithPatch() throws Exception {
        // Initialize the database
        trackRepository.save(track).block();

        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();

        // Update the track using partial update
        Track partialUpdatedTrack = new Track();
        partialUpdatedTrack.setId(track.getId());

        partialUpdatedTrack.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTrack.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTrack))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
        Track testTrack = trackList.get(trackList.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();
        track.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, track.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();
        track.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTrack() throws Exception {
        int databaseSizeBeforeUpdate = trackRepository.findAll().collectList().block().size();
        track.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(track))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Track in the database
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTrack() {
        // Initialize the database
        trackRepository.save(track).block();

        int databaseSizeBeforeDelete = trackRepository.findAll().collectList().block().size();

        // Delete the track
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, track.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Track> trackList = trackRepository.findAll().collectList().block();
        assertThat(trackList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
