package io.kischang.readme.app.dao;

import io.kischang.readme.app.model.UserArticle;
import io.kischang.readme.base.config.KisFenixJpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserArticleDao extends KisFenixJpaRepository<UserArticle, Long> {

    UserArticle findFirstByUserIdAndArticleIdAndFeedId(long userId, long aId, long fId);

}