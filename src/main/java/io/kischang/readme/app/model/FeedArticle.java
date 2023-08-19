package io.kischang.readme.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 文章真实内容ID
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kis_feed_article")
public class FeedArticle {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.blinkfox.fenix.id.SnowflakeIdGenerator")
    private Long articleId;

    private Long feedId;
    // 0 rss  1 email  2----
    @Column(columnDefinition = "INT default 0")
    private Integer sourceType;

    // 文章防止重复ID
    private String hashKey;
    private String uri;

    // 具体内容
    private String source;
    private String title;

    // 消息体需要处理图片数据
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    // 发布时间
    private String publishedDate;

}
