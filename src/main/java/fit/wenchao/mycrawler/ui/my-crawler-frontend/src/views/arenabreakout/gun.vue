<template lang="">
  <div class="container">

    <!-- pick gun sort -->
    <div class="">
      <el-tabs v-model="activeName" @tab-click="pick_sort">
        <el-tab-pane v-for="gun_sort of gun_sorts" :key="gun_sort.id" :sort_id="gun_sort.id" :label="gun_sort.name" :name="`gun_sort-${gun_sort.id}`" />
      </el-tabs>
    </div>

    <!-- gun list -->
    <div>
      <el-table-view :model="gun_list" :sortable="gun_list.sortable_list() " @current-change="select_one_gun">
        <template slot-scope="scope">
          <!-- pick to compare btn-->
          <div v-if="scope.col.prop == undefined">
            <el-button v-show="!picking_to_compare" size="mini" @click.stop="pick_to_compare(scope.row['itemID'])">比较</el-button>
          </div>

          <!-- gun size -->
          <div v-else-if="scope.col.prop === 'size'">
            <square-pile :x="scope.row[scope.col.prop].x" :y="scope.row[scope.col.prop].y" :width="10" :height="10" />
          </div>

          <!-- normal attr -->
          <div v-else>
            <span
              class="underline pointer"
              @click="select_dir_into_dir(scope.row[scope.col.prop])"
            >
              {{ scope.row[scope.col.prop] }}</span>
          </div>

        </template>
      </el-table-view>
    </div>

    <!-- gun comparing dialog -->
    <el-dialog-view :model="gun_compare_dialog" width="90%" :with_commit="false">
      <template #default="scope">
        <div class="flex justify-between compare-panel">
          <!-- a gun -->
          <div class="compare-item">
            <!-- gun data panel -->
            <el-card class="box-card">
              <!-- gun name -->
              <div slot="header" class="clearfix">
                <span>{{ scope.data.a_gun.itemName }}</span>
              </div>

              <!-- gun attrs -->
              <div v-for="attr of scope.data.a_gun.attrs" :key="attr" class="text item flex align-center mgb10">
                <!-- attr detail -->
                <div class="flexg2 mgr5">{{ attr['attr_detail'] }}</div>
                <div class="flexg1"> {{ attr.value }}</div>
                <div class="flexg6"><el-progress :percentage="(attr.value / attr.max_value) * 100" :color="customColors" :show-text="false" /></div>
                <!-- compare result icon -->
                <div class="flexg1">
                  <i v-if="attr.compare==='larger'" class="ali-international-icon-caret-up" style="color: green" />
                  <i v-else-if="attr.compare==='smaller'" class="ali-international-icon-caret-down" style="color: red" />
                </div>
              </div>
            </el-card>
          </div>

          <!-- b gun -->
          <div class="compare-item">
            <!-- gun data panel -->
            <el-card class="box-card">
              <!-- gun name -->
              <div slot="header" class="clearfix">
                <span>{{ scope.data.b_gun.itemName }}</span>
              </div>

              <!-- gun attrs -->
              <div v-for="attr of scope.data.b_gun.attrs" :key="attr" class="text item flex align-center mgb10">
                <!-- attr detail -->
                <div class="flexg2 mgr5">{{ attr['attr_detail'] }}</div>
                <div class="flexg1"> {{ attr.value }}</div>
                <div class="flexg6"><el-progress :percentage="(attr.value / attr.max_value) * 100" :color="customColors" :show-text="false" /></div>
                <!-- compare result icon -->
                <div class="flexg1">
                  <i v-if="attr.compare==='larger'" class="ali-international-icon-caret-up" style="color: green" />
                  <i v-else-if="attr.compare==='smaller'" class="ali-international-icon-caret-down" style="color: red" />
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </template>
    </el-dialog-view>
  </div>
</template>
<script>
import ElDialogView from '@/views/oop_components/ElDialogView.vue'
import ElTableView from '@/views/oop_components/ElTableView.vue'
import SquarePile from '@/views/arenabreakout/comp/SquarePile.vue'
import { OopElDialogModel, OopElTableModel } from '@/lib/index'
import readableDisplay from '@/views/common/readable-display.vue'
import * as file_api from '@/api/file.js'
import { Message } from 'element-ui'
import * as arenabreakoutApi from '@/api/arenabreakout.js'
import { ArrayUtils, is_mobile } from '@/utils'
let vue
export default {
  components: {
    readableDisplay,
    ElDialogView,
    SquarePile,
    ElTableView
  },
  data() {
    return {
      percentage: 20,
      customColor: '#409eff',
      customColors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 }
      ],
      /**
       * best gun attrs for each gun attr
       *
       * example of elem:
       *
       *  name: 'itemVerticalRecoil'
       *  max_value: 83
       *
       */
      gun_attrs_max: [
      ],

      /**
       * compare panel
       */
      gun_compare_dialog: new OopElDialogModel('比较', {
        compare_each_attr(a_gun_attrs, b_gun_attrs) {
          // compare each attr of two selected guns
          for (let i = 0; i < a_gun_attrs.length; i++) {
            if (a_gun_attrs[i].attr_name === 'itemWeight') {
              // comparing rule of weight is opposite, lighter, better
              if (a_gun_attrs[i].value < b_gun_attrs[i].value) {
                a_gun_attrs[i].compare = 'larger'
                b_gun_attrs[i].compare = 'smaller'
              } else if (a_gun_attrs[i].value === b_gun_attrs[i].value) {
                a_gun_attrs[i].compare = 'eq'
                b_gun_attrs[i].compare = 'eq'
              } else {
                a_gun_attrs[i].compare = 'smaller'
                b_gun_attrs[i].compare = 'larger'
              }
            } else {
              //  larger, better
              if (a_gun_attrs[i].value > b_gun_attrs[i].value) {
                a_gun_attrs[i].compare = 'larger'
                b_gun_attrs[i].compare = 'smaller'
              } else if (a_gun_attrs[i].value === b_gun_attrs[i].value) {
                a_gun_attrs[i].compare = 'eq'
                b_gun_attrs[i].compare = 'eq'
              } else {
                a_gun_attrs[i].compare = 'smaller'
                b_gun_attrs[i].compare = 'larger'
              }
            }
          }
        },
        convert_gun_2_attr_list(gun) {
          /**
           * attr list element example
           *
           * attr_name: itemVerticalRecoil
           * attr_detail: 枪械垂直后座
           * value: 47
           * max_value: 83
           */
          const gun_attrs = []
          const keys = Object.keys(gun)
          for (let i = 0; i < keys.length; i++) {
            // skip attrs that do not need to be compared
            const attr = keys[i]
            if (this.attrs_not_need_to_compare(attr)) {
              continue
            }
            const gun_attr = vue.gun_attrs.find(item => item.name === attr)
            const attr_detail = gun_attr == null ? undefined : gun_attr.detail
            if (attr_detail != null) {
              const value = gun[keys[i]]
              const max_pair = vue.gun_attrs_max.find(item => item.name === attr)
              const max_value = max_pair.max_value
              gun_attrs.push({ attr_name: attr, attr_detail, value, max_value })
            }
          }
          return gun_attrs
        },
        attrs_not_need_to_compare(attr) {
          if (attr === 'itemName' ||
            attr === 'itemMagazinesCapacity' ||
            attr === 'itemCaliber' ||
            attr === 'size' ||
            attr === 'itemTypesOfFire') {
            return true
          }
          return false
        },

        reset_data() {
          this.data = {
            a_gun_id: undefined,
            b_gun_id: undefined,
            a_gun: {},
            b_gun: {}
          }
        },
        before_open(data) {
          const { a_gun_id, b_gun_id, gun_list } = data
          this.data.a_gun_id = a_gun_id
          this.data.b_gun_id = b_gun_id

          // find two guns to compare from gunlist
          this.data.a_gun = gun_list.find(item => item.itemID === a_gun_id)
          this.data.b_gun = gun_list.find(item => item.itemID === b_gun_id)

          /**
           * attr list element example
           *
           * attr_name: itemVerticalRecoil
           * attr_detail: 枪械垂直后座
           * value: 47
           * max_value: 83
           */
          const a_gun_attrs = this.convert_gun_2_attr_list(this.data.a_gun)
          const b_gun_attrs = this.convert_gun_2_attr_list(this.data.b_gun)

          // compare each attr of two selected guns
          this.compare_each_attr(a_gun_attrs, b_gun_attrs)

          this.data.a_gun.attrs = a_gun_attrs
          this.data.b_gun.attrs = b_gun_attrs

          this.do_open()
        },

        enable_commit() {
          return true
        },

        do_commit() {
          this.close()
        },
        before_close() {
          vue.picking_to_compare = false
        }
      }),
      activeName: `gun_sort-80101`,

      /**
       * gun list
       */
      gun_list: new OopElTableModel('gun list', {
        // only these cols are sortable
        sortable_list() {
          return [
            'itemName',
            'itemVerticalRecoil',
            'itemHorizontalRecoil',
            'itemFireRate',
            'itemEffectiveDistance',
            'itemAdsMoa',
            'itemEngonomics',
            'itemAccuracy'
          ]
        },

        // dynamically passed in
        cols: [

        ],
        /**
         * type of all attrs of gun object is string, for convenience of comparing,
         * convert some attrs to int
         */
        convert_gun_attrs_to_int(gun) {
          const need_convert_int_attrs = [
            'itemVerticalRecoil',
            'itemHorizontalRecoil',
            'itemFireRate',
            'itemEffectiveDistance',
            'itemAdsMoa',
            'itemEngonomics',
            'itemAccuracy']
          for (const attr of need_convert_int_attrs) {
            gun[attr] = parseInt(gun[attr])
          }
        },
        pick_best_attrs_for_each_attr(gunlist, gun_attrs) {
          const gun_attrs_max = []
          for (const gun_attr of gun_attrs) {
            const attr_name = gun_attr.name
            const compare_method = (a, b) => a[attr_name] - b[attr_name]
            const max_gun = ArrayUtils.max(gunlist, compare_method)
            gun_attrs_max.push({
              name: attr_name,
              max_value: max_gun[attr_name]
            })
          }
          return gun_attrs_max
        },
        /**
         * if data is provided, only fetch the sort of guns indicated by data, or fetch
         * all guns.
        */
        do_fetch_data(data) {
          const params = {}

          if (data != null) {
            params.sortId = data.sort_id
          }
          arenabreakoutApi.fetch_gun_list(params)
            .then(resp => {
              this.data = resp.data
              for (const gun of this.data) {
                const sizeX = gun.itemSizeX
                const sizeY = gun.itemSizeY
                gun.size = {
                  x: parseInt(sizeX),
                  y: parseInt(sizeY)
                }

                this.convert_gun_attrs_to_int(gun)

                // pick the best gun attrs for each gun attr
                vue.gun_attrs_max = this.pick_best_attrs_for_each_attr(
                  this.data,
                  vue.gun_attrs
                )
              }
              this.fetch_over()
            })
        }
      }),
      gun_sorts: [],
      gun_attrs: [],
      /**
       * Indicate that we are now in the process of comparing. The process starts
       * when user clicks "compare" button, ends when user closes comparing dialog
       */
      picking_to_compare: false,
      /**
       * id of gun to compare
       */
      a_gun_id: undefined,
      /**
       * id of gun to compare
       */
      b_gun_id: undefined
    }
  },
  created() {
    vue = this
    Promise.all([
      arenabreakoutApi.fetch_gun_attr_list(),
      arenabreakoutApi.fetch_gun_sort_list()
    ])
      .then(resps => {
        this.gun_attrs = resps[0].data

        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemDesc' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemKeyword' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemID' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemGetWay' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemGetCondition' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemBullets' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'npcID' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemSortId' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemSizeX' })
        ArrayUtils.remove(this.gun_attrs, (item) => { return item.name === 'itemSizeY' })

        ArrayUtils.toFirst(this.gun_attrs, item => item.name === 'itemName')
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemVerticalRecoil', 1)
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemHorizontalRecoil', 2)
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemFireRate', 3)
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemEffectiveDistance', 4)
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemAdsMoa', 5)
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemEngonomics', 6)
        ArrayUtils.toIndex(this.gun_attrs, item => item.name === 'itemAccuracy', 7)

        this.gun_attrs.push({ name: 'size', detail: '大小' })
        this.gun_sorts = resps[1].data

        ArrayUtils.remove(this.gun_sorts, item => item.name === '')

        console.log(this.gun_sorts)

        const cols = []
        for (const gun_attr of this.gun_attrs) {
          cols.push({ prop: gun_attr.name,
            label: gun_attr.detail })
        }

        cols.push({
          prop: undefined,
          label: '操作'
        })
        if (is_mobile()) {
          ArrayUtils.toFirst(cols, item => item.prop === undefined)
        }
        this.gun_list.cols = cols
        this.gun_list.fetch_data({
          sort_id: '80101'
        })
      })

    // const gun_attrs = this.fetch_gun_attrs()
  },
  methods: {
    pick_to_compare(a_gun_id) {
      this.picking_to_compare = true
      this.a_gun_id = a_gun_id
    },
    select_one_gun(row) {
      if (this.picking_to_compare) {
        this.b_gun_id = row.itemID
        this.gun_compare_dialog.open({ a_gun_id: this.a_gun_id, b_gun_id: this.b_gun_id, gun_list: this.gun_list.data })
      }
    },
    pick_sort(tab, evt) {
      console.log(tab, evt)
      const elem = evt.target
      const elem_id = elem.getAttribute('id')
      console.log(elem_id)
      const sort_id = elem_id.substring(elem_id.lastIndexOf('-') + 1, elem_id.length)
      console.log(sort_id)
      this.gun_list.fetch_data({ sort_id })
    },
    fetch_gun_attrs() {
      return [{
        id: 1,
        name: 'itemVerticalRecoil',
        detail: '后座'
      }, {
        id: 2,
        name: 'itemEngonomics',
        detail: '人机工效'
      }, {
        id: 3,
        name: 'itemName',
        detail: '名称'
      }]
    },
    /**
       * fetch all files in "this.path" dir
       */
    fetch_file_list() {
      // '/' is the default value of this.path
      if (this.path == null) {
        this.path = '/'
      }

      file_api.getFileList({
        path: this.path
      }).then(resp => {
        this.filelist = resp.data
      }).catch(resp => {
        if (resp.code === 'NO_FILE') {
          Message.warning('File Not Found, please configure the correct root path')
          return
        }
      })
    }

  }
}
</script>
  <style lang="scss">
  @import '~@/styles/common-style.scss';

  @media (max-width: 750px){

    .container{
      width: 95% !important;
    }

    .compare-panel{
      flex-direction: column !important;
      align-items: center !important;
    }

    .compare-item{
      width: 90% !important;
    }
	}
  .compare-item{
    width: 49%;
  }

  .underline {
   &:hover{
    text-decoration: underline;
   }
  }

  .container{
      width: 90%;
      margin: auto;
  }

  </style>

