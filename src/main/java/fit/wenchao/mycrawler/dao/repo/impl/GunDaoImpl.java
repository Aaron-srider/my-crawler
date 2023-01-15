package fit.wenchao.mycrawler.dao.repo.impl;

import fit.wenchao.mycrawler.dao.po.GunPO;
import fit.wenchao.mycrawler.dao.mapper.GunMapper;
import fit.wenchao.mycrawler.dao.repo.GunDao;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Repository;

/**
 * <p>
 *  dao实现类
 * </p>
 *
 * @author wc
 * @since 2023-01-15
 */
@Repository
public class GunDaoImpl extends ServiceImpl<GunMapper, GunPO> implements GunDao {

}
