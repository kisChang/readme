package io.kischang.readme.app.dao;

import io.kischang.readme.app.model.UserFeeds;
import io.kischang.readme.base.config.KisFenixJpaRepository;

import java.util.List;

public interface UserFeedsDao extends KisFenixJpaRepository<UserFeeds, Long> {

    List<UserFeeds> findAllByFeedId(long feedId);

    UserFeeds findByFeedIdAndUserId(long feedId, long userId);

    void deleteByFeedIdAndUserId(long feedId, long userId);

}