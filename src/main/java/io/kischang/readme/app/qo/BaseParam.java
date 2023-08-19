package io.kischang.readme.app.qo;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

@Data
public class BaseParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long page;

    private Long limit;

    private String sort;

    private String order;

    public Pageable toPage() {
        if (page == null || limit == null) {
            return null;
        }
        return PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
    }
}
