package com.jhipster.demo.bootifulmusic.web.rest;

import com.jhipster.demo.bootifulmusic.domain.Genre;
import com.jhipster.demo.bootifulmusic.repository.GenreRepository;
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
 * REST controller for managing {@link com.jhipster.demo.bootifulmusic.domain.Genre}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GenreResource {

    private final Logger log = LoggerFactory.getLogger(GenreResource.class);

    private static final String ENTITY_NAME = "genre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenreRepository genreRepository;

    public GenreResource(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * {@code POST  /genres} : Create a new genre.
     *
     * @param genre the genre to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new genre, or with status {@code 400 (Bad Request)} if the genre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/genres")
    public Mono<ResponseEntity<Genre>> createGenre(@Valid @RequestBody Genre genre) throws URISyntaxException {
        log.debug("REST request to save Genre : {}", genre);
        if (genre.getId() != null) {
            throw new BadRequestAlertException("A new genre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return genreRepository
            .save(genre)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/genres/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /genres/:id} : Updates an existing genre.
     *
     * @param id the id of the genre to save.
     * @param genre the genre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genre,
     * or with status {@code 400 (Bad Request)} if the genre is not valid,
     * or with status {@code 500 (Internal Server Error)} if the genre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/genres/{id}")
    public Mono<ResponseEntity<Genre>> updateGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Genre genre
    ) throws URISyntaxException {
        log.debug("REST request to update Genre : {}, {}", id, genre);
        if (genre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return genreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return genreRepository
                    .save(genre)
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
     * {@code PATCH  /genres/:id} : Partial updates given fields of an existing genre, field will ignore if it is null
     *
     * @param id the id of the genre to save.
     * @param genre the genre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genre,
     * or with status {@code 400 (Bad Request)} if the genre is not valid,
     * or with status {@code 404 (Not Found)} if the genre is not found,
     * or with status {@code 500 (Internal Server Error)} if the genre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/genres/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Genre>> partialUpdateGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Genre genre
    ) throws URISyntaxException {
        log.debug("REST request to partial update Genre partially : {}, {}", id, genre);
        if (genre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return genreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Genre> result = genreRepository
                    .findById(genre.getId())
                    .map(existingGenre -> {
                        if (genre.getName() != null) {
                            existingGenre.setName(genre.getName());
                        }

                        return existingGenre;
                    })
                    .flatMap(genreRepository::save);

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
     * {@code GET  /genres} : get all the genres.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of genres in body.
     */
    @GetMapping("/genres")
    public Mono<ResponseEntity<List<Genre>>> getAllGenres(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Genres");
        return genreRepository
            .count()
            .zipWith(genreRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /genres/:id} : get the "id" genre.
     *
     * @param id the id of the genre to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the genre, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/genres/{id}")
    public Mono<ResponseEntity<Genre>> getGenre(@PathVariable Long id) {
        log.debug("REST request to get Genre : {}", id);
        Mono<Genre> genre = genreRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(genre);
    }

    /**
     * {@code DELETE  /genres/:id} : delete the "id" genre.
     *
     * @param id the id of the genre to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/genres/{id}")
    public Mono<ResponseEntity<Void>> deleteGenre(@PathVariable Long id) {
        log.debug("REST request to delete Genre : {}", id);
        return genreRepository
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
