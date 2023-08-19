package io.kischang.readme.app.qo;

import lombok.Data;

@Data
public class FeedSourceParam extends BaseParam {

    private Long feedId;

    private String name;
    private String logo;
    private String url;
    private String descText;

    // 更新周期
    private String cron;

}
