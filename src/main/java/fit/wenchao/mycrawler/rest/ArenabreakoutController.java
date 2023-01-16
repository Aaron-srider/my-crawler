package fit.wenchao.mycrawler.rest;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import fit.wenchao.mycrawler.dao.po.GunAttrTranslationPO;
import fit.wenchao.mycrawler.dao.po.GunPO;
import fit.wenchao.mycrawler.dao.po.GunSortTranslationPO;
import fit.wenchao.mycrawler.dao.repo.GunAttrTranslationDao;
import fit.wenchao.mycrawler.dao.repo.GunDao;
import fit.wenchao.mycrawler.dao.repo.GunSortTranslationDao;
import fit.wenchao.mycrawler.model.JsonResult;
import fit.wenchao.mycrawler.utils.JsonUtils;
import fit.wenchao.mycrawler.utils.ResponseEntityUtils;
import fit.wenchao.mycrawler.utils.http.HttpClientRequestBuilder;
import fit.wenchao.mycrawler.utils.http.HttpClientResponseWrapper;
import org.apache.http.HttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/arenabreakout")
public class ArenabreakoutController {

    private static final String prefix = "https://aqtw.qq.com/data/data";

    @Autowired
    GunDao gunDao;

    @Autowired
    GunAttrTranslationDao gunAttrTranslationDao;

    @Autowired
    GunSortTranslationDao gunSortTranslationDao;

    @GetMapping("/sync/gun")
    public ResponseEntity<JsonResult> syncGunList() throws IOException {
        HttpClientResponseWrapper httpClientResponseWrapper =
                HttpClientRequestBuilder.getInstance()
                                        .get(prefix + "/gun.json")
                                        .send();

        HttpEntity entity = httpClientResponseWrapper.getResponse()
                                                     .getEntity();

        String json = httpClientResponseWrapper.getEntityAsString();

        List<GunPO> gunPOList = JsonUtils.getList(json, "$.gun", GunPO.class);

        List<GunAttrTranslationPO> infoPOList = new ArrayList<>();
        JSONObject infoJsonObject =
                JsonUtils.getObject(json, "$.info[0]");
        infoJsonObject.forEach((k, v) -> {
            infoPOList.add(new GunAttrTranslationPO(null, k, ((String) v)));
        });

        List<GunSortTranslationPO> sortPOList = JsonUtils.getList(json, "$.sort"
                , GunSortTranslationPO.class);

        JSONObject parse = (JSONObject) JSONObject.parse(json);

        //JSONArray guns = (JSONArray) parse.get("gun");
        //List<GunPO> gunPOList = new ArrayList<>();
        //for (Object gun : guns) {
        //
        //    JSONObject gunObject = (JSONObject) gun;
        //    GunPO gunPO = JSONObject.parseObject(gunObject.toJSONString(), GunPO.class);
        //    gunPOList.add(gunPO);
        //}


        for (GunPO gunPO : gunPOList) {
            String itemID = gunPO.getItemID();
            GunPO exists = gunDao.getById(itemID);
            if (exists != null) {
                gunDao.updateById(gunPO);
            }
            else {
                gunDao.save(gunPO);
            }
        }

        for (GunAttrTranslationPO gunAttrTranslationPO : infoPOList) {
            String name = gunAttrTranslationPO.getName();

            GunAttrTranslationPO exists = gunAttrTranslationDao.getOne(new QueryWrapper<GunAttrTranslationPO>().eq("name",
                    name), false);
            if (exists != null) {
                gunAttrTranslationDao.update(new UpdateWrapper<GunAttrTranslationPO>()
                        .set("detail", gunAttrTranslationPO.getDetail())
                        .eq("name", name));
            }
            else {
                gunAttrTranslationDao.save(gunAttrTranslationPO);
            }

        }


        for (GunSortTranslationPO gunSortTranslationPO : sortPOList) {
            String id = gunSortTranslationPO.getId();

            GunSortTranslationPO exists = gunSortTranslationDao.getById(id);
            if (exists != null) {
                gunSortTranslationDao.updateById(gunSortTranslationPO);
            }
            else {
                gunSortTranslationDao.save(gunSortTranslationPO);
            }

        }

        return ResponseEntityUtils.ok(JsonResult.ok());
    }


    @GetMapping("/gun")
    public ResponseEntity<JsonResult> getGunList(String sortId) {
        List<GunPO> list = new ArrayList<>();
        if (sortId == null || sortId.isEmpty()) {
            list = gunDao.list();
        }
        else {
            list = gunDao.list(new QueryWrapper<GunPO>().eq("item_sort_id", sortId));
        }
        return ResponseEntityUtils.ok(JsonResult.ok(list));
    }

    @GetMapping("/gun-sort")
    public ResponseEntity<JsonResult> getGunSortList() {
        return ResponseEntityUtils.ok(JsonResult.ok(gunSortTranslationDao.list()));
    }

    @GetMapping("/gun-attr")
    public ResponseEntity<JsonResult> getGunAttr() {
        return ResponseEntityUtils.ok(JsonResult.ok(gunAttrTranslationDao.list()));
    }

}
