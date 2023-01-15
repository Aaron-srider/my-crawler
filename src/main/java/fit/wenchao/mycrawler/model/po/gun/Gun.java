package fit.wenchao.mycrawler.model.po.gun;

import fit.wenchao.mycrawler.utils.tableGen.TableGenFieldLength;
import fit.wenchao.mycrawler.utils.tableGen.TableGenFieldName;
import fit.wenchao.mycrawler.utils.tableGen.TableGenPrimary;
import fit.wenchao.mycrawler.utils.tableGen.TableGenTableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableGenTableName("gun")
public class Gun {
    String itemSortId;
    String itemName;
    @TableGenFieldLength(200)
    @TableGenFieldName("item_id")
    @TableGenPrimary
    String itemID;
    String itemMuzzleVelocity;
    String itemTypesOfFire;
    String itemKeyword;
    String itemFireRate;
    String itemMagazinesCapacity;
    String itemCaliber;
    String itemCaliberDesc;
    String itemHorizontalRecoil;
    String itemVerticalRecoil;
    String itemAccuracy;
    String itemEffectiveDistance;
    String itemAdsMoa;
    String itemEngonomics;
    String itemSizeX;
    String itemSizeY;
    String itemWeight;
    String itemDesc;
    String itemBullets;
    String itemGetWay;
    @TableGenFieldLength(100)
    @TableGenFieldName("npc_id")
    String npcID;
    String itemGetCondition;
}
