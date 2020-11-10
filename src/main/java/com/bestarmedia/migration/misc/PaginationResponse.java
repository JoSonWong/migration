package com.bestarmedia.migration.misc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class PaginationResponse<T, R> {

    @JsonProperty(value = "data")
    private List<R> data;

    @JsonProperty(value = "current_page")
    private int currentPage;

    @JsonProperty(value = "from")
    private int from;

    @JsonProperty(value = "to")
    private int to;

    @JsonProperty(value = "last_page")
    private int lastPage;

    @JsonProperty(value = "next_page_url")
    private String nextPageUrl;

    @JsonProperty(value = "path")
    private String path;

    @JsonProperty(value = "per_page")
    private int perPage;

    @JsonProperty(value = "prev_page_url")
    private String prevPageUrl;

    @JsonProperty(value = "total")
    private long total;

    @JsonCreator
    public PaginationResponse(@JsonProperty(value = "data") List<R> data,
                              @JsonProperty(value = "current_page") int currentPage,
                              @JsonProperty(value = "from") int from,
                              @JsonProperty(value = "to") int to,
                              @JsonProperty(value = "last_page") int lastPage,
                              @JsonProperty(value = "next_page_url") String nextPageUrl,
                              @JsonProperty(value = "path") String path,
                              @JsonProperty(value = "per_page") int perPage,
                              @JsonProperty(value = "prev_page_url") String prevPageUrl,
                              @JsonProperty(value = "total") long total) {
        this.data = data;
        this.currentPage = currentPage;
        this.from = from;
        this.to = to;
        this.lastPage = lastPage;
        this.nextPageUrl = nextPageUrl;
        this.path = path;
        this.perPage = perPage;
        this.prevPageUrl = prevPageUrl;
        this.total = total;
    }

    public PaginationResponse(Page<T> page, PaginationFunction<T, R> function, int currentPage, int perPage) {
        this.setData(function.execute(page.toList()));
        this.setTotal(page.getTotalElements());
        this.setLastPage(page.getTotalPages());
        this.setCurrentPage(currentPage + 1);
        this.setPerPage(perPage);
        this.setNextPageUrl("");
        this.setPath("");
        this.setPrevPageUrl("");
        this.setFrom(currentPage * perPage + 1);
        this.setTo((currentPage + 1) * perPage);
    }

    public PaginationResponse(Page<T> page, Function<T, R> function, int currentPage, int perPage) {
        List<R> list = new ArrayList<>();
        for (T t : page) {
            list.add(function.apply(t));
        }
        this.setData(list);
        this.setTotal(page.getTotalElements());
        this.setLastPage(page.getTotalPages());
        this.setCurrentPage(currentPage + 1);
        this.setPerPage(perPage);
        this.setNextPageUrl("");
        this.setPath("");
        this.setPrevPageUrl("");
        this.setFrom(currentPage * perPage + 1);
        this.setTo((currentPage + 1) * perPage);
    }

    public PaginationResponse(Page<R> page, int currentPage, int perPage) {
        this.setData(page.getContent());
        this.setTotal(page.getTotalElements());
        this.setLastPage(page.getTotalPages());
        this.setCurrentPage(currentPage + 1);
        this.setPerPage(perPage);
        this.setNextPageUrl("");
        this.setPath("");
        this.setPrevPageUrl("");
        this.setFrom(currentPage * perPage + 1);
        this.setTo((currentPage + 1) * perPage);
    }
}
