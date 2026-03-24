package com.cc.common.dto;

import lombok.Data;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {

    private Long total;
    private List<T> records;
    private Long current;
    private Long size;

    public PageResult() {}

    public PageResult(Long total, List<T> records, Long current, Long size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
    }
}