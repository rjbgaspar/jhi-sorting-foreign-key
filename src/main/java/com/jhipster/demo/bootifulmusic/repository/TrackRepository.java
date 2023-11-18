package com.jhipster.demo.bootifulmusic.repository;

import com.jhipster.demo.bootifulmusic.domain.Track;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Track entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrackRepository extends ReactiveCrudRepository<Track, Long>, TrackRepositoryInternal {
    Flux<Track> findAllBy(Pageable pageable);

    @Override
    Mono<Track> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Track> findAllWithEagerRelationships();

    @Override
    Flux<Track> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM track entity WHERE entity.album_id = :id")
    Flux<Track> findByAlbum(Long id);

    @Query("SELECT * FROM track entity WHERE entity.album_id IS NULL")
    Flux<Track> findAllWhereAlbumIsNull();

    @Override
    <S extends Track> Mono<S> save(S entity);

    @Override
    Flux<Track> findAll();

    @Override
    Mono<Track> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TrackRepositoryInternal {
    <S extends Track> Mono<S> save(S entity);

    Flux<Track> findAllBy(Pageable pageable);

    Flux<Track> findAll();

    Mono<Track> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Track> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Track> findOneWithEagerRelationships(Long id);

    Flux<Track> findAllWithEagerRelationships();

    Flux<Track> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
