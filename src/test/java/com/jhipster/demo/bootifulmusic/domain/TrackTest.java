package com.jhipster.demo.bootifulmusic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jhipster.demo.bootifulmusic.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TrackTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Track.class);
        Track track1 = new Track();
        track1.setId(1L);
        Track track2 = new Track();
        track2.setId(track1.getId());
        assertThat(track1).isEqualTo(track2);
        track2.setId(2L);
        assertThat(track1).isNotEqualTo(track2);
        track1.setId(null);
        assertThat(track1).isNotEqualTo(track2);
    }
}
