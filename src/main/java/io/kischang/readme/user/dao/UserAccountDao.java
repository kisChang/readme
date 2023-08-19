package io.kischang.readme.user.dao;

import io.kischang.readme.base.config.KisFenixJpaRepository;
import io.kischang.readme.user.model.UserAccount;

public interface UserAccountDao extends KisFenixJpaRepository<UserAccount, Long> {

    UserAccount findByUsername(String username);

    UserAccount findByOpenid(String openId);

}