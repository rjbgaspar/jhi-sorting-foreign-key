package com.jhipster.demo.bootifulmusic.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.jhipster.demo.bootifulmusic.domain.Track;
import com.jhipster.demo.bootifulmusic.repository.rowmapper.AlbumRowMapper;
import com.jhipster.demo.bootifulmusic.repository.rowmapper.TrackRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Track entity.
 */
@SuppressWarnings("unused")
class TrackRepositoryInternalImpl extends SimpleR2dbcRepository<Track, Long> implements TrackRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AlbumRowMapper albumMapper;
    private final TrackRowMapper trackMapper;

    private static final Table entityTable = Table.aliased("track", EntityManager.ENTITY_ALIAS);
    private static final Table albumTable = Table.aliased("album", "album");

    public TrackRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AlbumRowMapper albumMapper,
        TrackRowMapper trackMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Track.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.albumMapper = albumMapper;
        this.trackMapper = trackMapper;
    }

    @Override
    public Flux<Track> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Track> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TrackSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AlbumSqlHelper.getColumns(albumTable, "album"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(albumTable)
            .on(Column.create("album_id", entityTable))
            .equals(Column.create("id", albumTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Track.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Track> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Track> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Track> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Track> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Track> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Track process(Row row, RowMetadata metadata) {
        Track entity = trackMapper.apply(row, "e");
        entity.setAlbum(albumMapper.apply(row, "album"));
        return entity;
    }

    @Override
    public <S extends Track> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
