package com.blood_donor.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlUtilsTest {

    @Test
    @DisplayName("escapeLikeWildcards correctly handles null and empty inputs")
    void testNullAndEmpty() {
        assertNull(SqlUtils.escapeLikeWildcards(null));
        assertEquals("", SqlUtils.escapeLikeWildcards(""));
    }

    @Test
    @DisplayName("escapeLikeWildcards preserves plain alphanumeric text")
    void testPlainText() {
        assertEquals("Delhi", SqlUtils.escapeLikeWildcards("Delhi"));
        assertEquals("Mumbai", SqlUtils.escapeLikeWildcards("Mumbai"));
    }

    @Test
    @DisplayName("escapeLikeWildcards escapes %, _, and \\")
    void testWildcardsEscaped() {
        assertEquals("100\\%", SqlUtils.escapeLikeWildcards("100%"));
        assertEquals("New\\_York", SqlUtils.escapeLikeWildcards("New_York"));
        assertEquals("Path\\\\City", SqlUtils.escapeLikeWildcards("Path\\City"));
        assertEquals("\\%\\_\\\\", SqlUtils.escapeLikeWildcards("%_\\"));
    }
}
