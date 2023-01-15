package fit.wenchao.mycrawler.model.po.gun;

import fit.wenchao.mycrawler.utils.tableGen.TableGenPrimary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class GunSortTranslation {
    @TableGenPrimary
    String id;
    String name;
}
