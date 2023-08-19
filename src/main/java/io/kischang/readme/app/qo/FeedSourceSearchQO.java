package io.kischang.readme.app.qo;

import io.kischang.readme.base.bean.PageQO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FeedSourceSearchQO extends PageQO {

    private String name;

}
