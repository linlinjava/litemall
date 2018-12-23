package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallResourceMapper;
import org.linlinjava.litemall.db.domain.LitemallResource.Column;
import org.linlinjava.litemall.db.domain.LitemallResourceNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ulongx
 */
@Service
public class LitemallResourceService {

//    private final Column[] result = new Column[]{Column.id, Column.routerText, Column.description, Column.pid, Column.routerUrl};

    @Resource
    private LitemallResourceMapper resourceMapper;

    public List<LitemallResourceNode> queryResorceTree(){

        return resourceMapper.listResourceTree();
    }
}
