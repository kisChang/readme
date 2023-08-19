package io.kischang.readme.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * 基础分页对象支持，针对原版方案
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageQO implements Serializable {

    private Integer pageNow;
    private Integer pageSize = 10;

    private String orderBy;
    private boolean desc = false;

    private Integer page;
    private Integer limit;
    private String sort;
    private String order = "desc";

    public PageQO(Integer pageNow, Integer pageSize) {
        this.pageNow = pageNow == null ? 0 : pageNow;
        this.pageSize = pageSize == null ? 10 : pageSize;
    }

    public PageRequest toPage() {
        Sort sortObj = Sort.unsorted();
        if (orderBy != null) {
            sortObj = Sort.by(
                    desc ? Sort.Order.desc(orderBy) : Sort.Order.asc(orderBy)
            );
        }
        if (sort != null) {
            sortObj = Sort.by(
                    "desc".equalsIgnoreCase(order) ? Sort.Order.desc(sort) : Sort.Order.asc(sort)
            );
        }
        if (page != null) {
            return PageRequest.of(page - 1, limit == null ? 10 : limit, sortObj);
        }

        if (pageNow != null) {
            return PageRequest.of(pageNow - 1, pageSize == null ? 10 : pageSize, sortObj);
        }
        return PageRequest.of(0, pageSize == null ? 10 : pageSize, sortObj);
    }

    public <T> T toParam(Class<T> tClass) {
        T target = BeanUtils.instantiateClass(tClass);
        BeanUtils.copyProperties(this, target);
        return target;
    }
}
