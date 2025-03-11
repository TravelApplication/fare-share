package share.fare.backend.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaginatedResponseTest {
    @Test
    void testNoArgsConstructor() {
        PaginatedResponse<String> response = new PaginatedResponse<>();
        assertNotNull(response);
        assertNull(response.getContent());
        assertEquals(0, response.getCurrentPage());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getTotalElements());
    }

    @Test
    void testPageConstructor() {
        List<String> sampleContent = Arrays.asList("Item1", "Item2", "Item3");
        Page<String> mockPage = Mockito.mock(Page.class);

        Mockito.when(mockPage.getContent()).thenReturn(sampleContent);
        Mockito.when(mockPage.getNumber()).thenReturn(1);
        Mockito.when(mockPage.getTotalPages()).thenReturn(5);
        Mockito.when(mockPage.getTotalElements()).thenReturn(15L);

        PaginatedResponse<String> response = new PaginatedResponse<>(mockPage);

        assertNotNull(response);
        assertEquals(sampleContent, response.getContent());
        assertEquals(1, response.getCurrentPage());
        assertEquals(5, response.getTotalPages());
        assertEquals(15L, response.getTotalElements());
    }

}