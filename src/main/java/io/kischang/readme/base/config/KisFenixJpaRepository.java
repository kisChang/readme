package io.kischang.readme.base.config;

import com.blinkfox.fenix.specification.FenixJpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface KisFenixJpaRepository<T, ID> extends JpaRepository<T, ID>, FenixJpaSpecificationExecutor<T> {
}
