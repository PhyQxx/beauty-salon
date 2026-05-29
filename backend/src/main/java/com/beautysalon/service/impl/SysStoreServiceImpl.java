package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beautysalon.entity.SysStore;
import com.beautysalon.mapper.SysStoreMapper;
import com.beautysalon.service.SysStoreService;
import org.springframework.stereotype.Service;

/**
 * 门店信息 服务实现类
 *
 * @author BeautySalon Team
 */
@Service
public class SysStoreServiceImpl extends ServiceImpl<SysStoreMapper, SysStore> implements SysStoreService {
}
