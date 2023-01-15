package fit.wenchao.mycrawler.service.impl;

import fit.wenchao.mycrawler.dao.repo.GunAttrTranslationDao;
import fit.wenchao.mycrawler.service.GunAttrTranslationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * GunAttrTranslation 服务类
 * </p>
 *
 * @author wc
 * @since 2023-01-15
 */
@Service
@Slf4j
public class GunAttrTranslationServiceImpl implements GunAttrTranslationService{

    @Autowired
    GunAttrTranslationDao gunAttrTranslationDao;

}


