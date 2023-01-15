package fit.wenchao.mycrawler.service.impl;

import fit.wenchao.mycrawler.dao.repo.GunSortTranslationDao;
import fit.wenchao.mycrawler.service.GunSortTranslationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * GunSortTranslation 服务类
 * </p>
 *
 * @author wc
 * @since 2023-01-15
 */
@Service
@Slf4j
public class GunSortTranslationServiceImpl implements GunSortTranslationService{

    @Autowired
    GunSortTranslationDao gunSortTranslationDao;

}


