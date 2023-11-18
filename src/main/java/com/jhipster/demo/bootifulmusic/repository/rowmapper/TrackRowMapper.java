package com.jhipster.demo.bootifulmusic.repository.rowmapper;

import com.jhipster.demo.bootifulmusic.domain.Track;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Track}, with proper type conversions.
 */
@Service
public class TrackRowMapper implements BiFunction<Row, String, Track> {

    private final ColumnConverter converter;

    public TrackRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Track} stored in the database.
     */
    @Override
    public Track apply(Row row, String prefix) {
        Track entity = new Track();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAlbumId(converter.fromRow(row, prefix + "_album_id", Long.class));
        return entity;
    }
}
