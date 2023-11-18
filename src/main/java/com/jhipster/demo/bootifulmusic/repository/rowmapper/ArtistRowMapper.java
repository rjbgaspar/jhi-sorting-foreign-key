package com.jhipster.demo.bootifulmusic.repository.rowmapper;

import com.jhipster.demo.bootifulmusic.domain.Artist;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Artist}, with proper type conversions.
 */
@Service
public class ArtistRowMapper implements BiFunction<Row, String, Artist> {

    private final ColumnConverter converter;

    public ArtistRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Artist} stored in the database.
     */
    @Override
    public Artist apply(Row row, String prefix) {
        Artist entity = new Artist();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}
