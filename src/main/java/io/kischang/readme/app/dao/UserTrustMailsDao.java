package io.kischang.readme.app.dao;

import io.kischang.readme.app.model.UserTrustMails;
import io.kischang.readme.base.config.KisFenixJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserTrustMailsDao extends KisFenixJpaRepository<UserTrustMails, Long> {

    @Query("SELECT a from UserTrustMails as a where a.userId=?1")
    Page<UserTrustMails> findByUser(long userId, Pageable pageable);

    @Query("SELECT a.mail FROM UserTrustMails AS a WHERE a.userId=?1")
    Set<String> findMailsByUserId(long userId);

}
