package io.kischang.readme.app.dao;

import io.kischang.readme.app.model.FeedArticle;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.vo.FeedArticleListVO;
import io.kischang.readme.base.config.KisFenixJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedArticleDao extends KisFenixJpaRepository<FeedArticle, Long> {

    @Query("SELECT new io.kischang.readme.app.vo.FeedArticleListVO(a,u.status) from FeedArticle as a LEFT join UserArticle as u ON a.articleId = u.articleId where u.userId=?1 AND u.feedId=?2 ORDER BY a.publishedDate DESC ")
    Page<FeedArticleListVO> findByUserAndFeedId(long userId, long feedId, Pageable pageable);

    @Query("SELECT new io.kischang.readme.app.vo.FeedArticleListVO(a, u.status, s.name) from FeedArticle as a L" +
            "EFT join UserArticle as u ON a.articleId = u.articleId LEFT JOIN FeedSource AS s ON a.feedId = s.feedId where u.userId=?1 ORDER BY a.publishedDate DESC ")
    Page<FeedArticleListVO> findByUser(long userId, Pageable pageable);

    FeedArticle findByFeedIdAndUri(long feedId, String uri);

    List<FeedArticle> findAllByFeedId(long feedId, Pageable pageable);


}