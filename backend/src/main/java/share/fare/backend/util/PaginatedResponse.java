package share.fare.backend.util;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A generic class representing a paginated response.
 *
 * @param <T> the type of the content in the paginated response
 */
@Data
public class PaginatedResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public PaginatedResponse() {
    }

    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }
}