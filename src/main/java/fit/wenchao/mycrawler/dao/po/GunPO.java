
package fit.wenchao.mycrawler.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 
 * </p>
 *
 * @author wc
 * @since 2023-01-15
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("`gun`")
public class GunPO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String itemSortId;

    private String itemName;

    @TableId(value = "item_id", type = IdType.INPUT)
    @TableField("item_id")
    private String itemID;

    private String itemMuzzleVelocity;

    private String itemTypesOfFire;

    private String itemKeyword;

    private String itemFireRate;

    private String itemMagazinesCapacity;

    private String itemCaliber;

    private String itemCaliberDesc;

    private String itemHorizontalRecoil;

    private String itemVerticalRecoil;

    private String itemAccuracy;

    private String itemEffectiveDistance;

    private String itemAdsMoa;

    private String itemEngonomics;

    private String itemSizeX;

    private String itemSizeY;

    private String itemWeight;

    private String itemDesc;

    private String itemBullets;

    private String itemGetWay;

    @TableField("npc_id")
    private String npcID;

    private String itemGetCondition;



}
