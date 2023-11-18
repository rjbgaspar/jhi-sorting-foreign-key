package com.jhipster.demo.bootifulmusic.repository.rowmapper;

import com.jhipster.demo.bootifulmusic.domain.Album;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Album}, with proper type conversions.
 */
@Service
public class AlbumRowMapper implements BiFunction<Row, String, Album> {

    private final ColumnConverter converter;

    public AlbumRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Album} stored in the database.
     */
    @Override
    public Album apply(Row row, String prefix) {
        Album entity = new Album();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setArtistId(converter.fromRow(row, prefix + "_artist_id", Long.class));
        entity.setGenreId(converter.fromRow(row, prefix + "_genre_id", Long.class));
        return entity;
    }
}
