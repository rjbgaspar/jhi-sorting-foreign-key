package com.jhipster.demo.bootifulmusic.web.rest;

import com.jhipster.demo.bootifulmusic.domain.Track;
import com.jhipster.demo.bootifulmusic.repository.TrackRepository;
import com.jhipster.demo.bootifulmusic.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.jhipster.demo.bootifulmusic.domain.Track}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TrackResource {

    private final Logger log = LoggerFactory.getLogger(TrackResource.class);

    private static final String ENTITY_NAME = "track";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrackRepository trackRepository;

    public TrackResource(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    /**
     * {@code POST  /tracks} : Create a new track.
     *
     * @param track the track to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new track, or with status {@code 400 (Bad Request)} if the track has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tracks")
    public Mono<ResponseEntity<Track>> createTrack(@Valid @RequestBody Track track) throws URISyntaxException {
        log.debug("REST request to save Track : {}", track);
        if (track.getId() != null) {
            throw new BadRequestAlertException("A new track cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return trackRepository
            .save(track)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/tracks/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tracks/:id} : Updates an existing track.
     *
     * @param id the id of the track to save.
     * @param track the track to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated track,
     * or with status {@code 400 (Bad Request)} if the track is not valid,
     * or with status {@code 500 (Internal Server Error)} if the track couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tracks/{id}")
    public Mono<ResponseEntity<Track>> updateTrack(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Track track
    ) throws URISyntaxException {
        log.debug("REST request to update Track : {}, {}", id, track);
        if (track.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, track.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trackRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return trackRepository
                    .save(track)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tracks/:id} : Partial updates given fields of an existing track, field will ignore if it is null
     *
     * @param id the id of the track to save.
     * @param track the track to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated track,
     * or with status {@code 400 (Bad Request)} if the track is not valid,
     * or with status {@code 404 (Not Found)} if the track is not found,
     * or with status {@code 500 (Internal Server Error)} if the track couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tracks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Track>> partialUpdateTrack(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Track track
    ) throws URISyntaxException {
        log.debug("REST request to partial update Track partially : {}, {}", id, track);
        if (track.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, track.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return trackRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Track> result = trackRepository
                    .findById(track.getId())
                    .map(existingTrack -> {
                        if (track.getName() != null) {
                            existingTrack.setName(track.getName());
                        }

                        return existingTrack;
                    })
                    .flatMap(trackRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /tracks} : get all the tracks.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tracks in body.
     */
    @GetMapping("/tracks")
    public Mono<ResponseEntity<List<Track>>> getAllTracks(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Tracks");
        return trackRepository
            .count()
            .zipWith(trackRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /tracks/:id} : get the "id" track.
     *
     * @param id the id of the track to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the track, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tracks/{id}")
    public Mono<ResponseEntity<Track>> getTrack(@PathVariable Long id) {
        log.debug("REST request to get Track : {}", id);
        Mono<Track> track = trackRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(track);
    }

    /**
     * {@code DELETE  /tracks/:id} : delete the "id" track.
     *
     * @param id the id of the track to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tracks/{id}")
    public Mono<ResponseEntity<Void>> deleteTrack(@PathVariable Long id) {
        log.debug("REST request to delete Track : {}", id);
        return trackRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
