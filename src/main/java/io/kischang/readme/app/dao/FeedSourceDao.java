package io.kischang.readme.app.dao;

import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.vo.FeedSourceUserVo;
import io.kischang.readme.base.config.KisFenixJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface FeedSourceDao extends KisFenixJpaRepository<FeedSource, Long> {


    @Query("SELECT new io.kischang.readme.app.vo.FeedSourceUserVo(a, u) from FeedSource as a LEFT join UserFeeds as u ON a.feedId = u.feedId where u.userId=?1")
    Page<FeedSourceUserVo> findByUser(long userId, Pageable pageable);

    FeedSource findByUrlLike(String url);

}