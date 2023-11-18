package com.jhipster.demo.bootifulmusic.repository;

import com.jhipster.demo.bootifulmusic.domain.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Album entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlbumRepository extends ReactiveCrudRepository<Album, Long>, AlbumRepositoryInternal {
    Flux<Album> findAllBy(Pageable pageable);

    @Override
    Mono<Album> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Album> findAllWithEagerRelationships();

    @Override
    Flux<Album> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM album entity WHERE entity.artist_id = :id")
    Flux<Album> findByArtist(Long id);

    @Query("SELECT * FROM album entity WHERE entity.artist_id IS NULL")
    Flux<Album> findAllWhereArtistIsNull();

    @Query("SELECT * FROM album entity WHERE entity.genre_id = :id")
    Flux<Album> findByGenre(Long id);

    @Query("SELECT * FROM album entity WHERE entity.genre_id IS NULL")
    Flux<Album> findAllWhereGenreIsNull();

    @Override
    <S extends Album> Mono<S> save(S entity);

    @Override
    Flux<Album> findAll();

    @Override
    Mono<Album> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AlbumRepositoryInternal {
    <S extends Album> Mono<S> save(S entity);

    Flux<Album> findAllBy(Pageable pageable);

    Flux<Album> findAll();

    Mono<Album> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Album> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Album> findOneWithEagerRelationships(Long id);

    Flux<Album> findAllWithEagerRelationships();

    Flux<Album> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
