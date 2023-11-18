package com.jhipster.demo.bootifulmusic.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.jhipster.demo.bootifulmusic.domain.Album;
import com.jhipster.demo.bootifulmusic.repository.rowmapper.AlbumRowMapper;
import com.jhipster.demo.bootifulmusic.repository.rowmapper.ArtistRowMapper;
import com.jhipster.demo.bootifulmusic.repository.rowmapper.GenreRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Album entity.
 */
@SuppressWarnings("unused")
class AlbumRepositoryInternalImpl extends SimpleR2dbcRepository<Album, Long> implements AlbumRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ArtistRowMapper artistMapper;
    private final GenreRowMapper genreMapper;
    private final AlbumRowMapper albumMapper;

    private static final Table entityTable = Table.aliased("album", EntityManager.ENTITY_ALIAS);
    private static final Table artistTable = Table.aliased("artist", "artist");
    private static final Table genreTable = Table.aliased("genre", "genre");

    public AlbumRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ArtistRowMapper artistMapper,
        GenreRowMapper genreMapper,
        AlbumRowMapper albumMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Album.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.artistMapper = artistMapper;
        this.genreMapper = genreMapper;
        this.albumMapper = albumMapper;
    }

    @Override
    public Flux<Album> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Album> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AlbumSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ArtistSqlHelper.getColumns(artistTable, "artist"));
        columns.addAll(GenreSqlHelper.getColumns(genreTable, "genre"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(artistTable)
            .on(Column.create("artist_id", entityTable))
            .equals(Column.create("id", artistTable))
            .leftOuterJoin(genreTable)
            .on(Column.create("genre_id", entityTable))
            .equals(Column.create("id", genreTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Album.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Album> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Album> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Album> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Album> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Album> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Album process(Row row, RowMetadata metadata) {
        Album entity = albumMapper.apply(row, "e");
        entity.setArtist(artistMapper.apply(row, "artist"));
        entity.setGenre(genreMapper.apply(row, "genre"));
        return entity;
    }

    @Override
    public <S extends Album> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
