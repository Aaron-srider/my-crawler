<template lang="">
  <div style="margin: 50px 40px">
    <div>
      <el-button @click="select_dir_dialog.open()">scan</el-button>
    </div>
    <!-- select dir to scan -->
    <el-dialog-view :model="select_dir_dialog" width="50%" :with_commit="false">
      <template #default="scope">
        <div class="mgb10">
          <el-button @click="select_dir_back()">back</el-button>
        </div>
        <div>
          <span>{{ cur_path }}</span>
        </div>
        <!-- select file panel -->
        <div style="overflow: scroll; height: 300px">
          <el-table-view :model="select_dir_cur_dir_file_list">
            <template slot-scope="scope">
              <!-- pick btn -->
              <div v-if="scope.col.prop == undefined">
                <span class="underline pointer" @click="select_dir_pick(scope.row['id'])">选择</span>
              </div>

              <!-- dir name -->
              <div v-else-if="scope.col.prop === 'name'">
                <span
                  class="underline pointer"
                  @click="select_dir_into_dir(scope.row[scope.col.prop])"
                >
                  {{ scope.row[scope.col.prop] }}</span>
              </div>

            </template>
          </el-table-view>
        </div>
        <!-- scope.model 获取 dialog 的oop对象 -->
        <!-- scope.data 获取 dialog 的oop对象中的data部分 -->
        <!-- ...... -->
      </template>
    </el-dialog-view>
  </div>
</template>
<script>
import readableDisplay from '@/views/common/readable-display.vue'
import ElDialogView from '@/views/oop_components/ElDialogView.vue'
import ElTableView from '@/views/oop_components/ElTableView.vue'
import { OopElDialogModel, OopElTableModel } from '@/lib/index'
import * as file_api from '@/api/file.js'
import * as config_api from '@/api/config.js'
import { Message } from 'element-ui'
let vue
export default {
  components: {
    ElDialogView,
    ElTableView
  },
  data() {
    return {
      picked_dir_path: '',
      cur_path: '/',
      select_dir_cur_dir_file_list: new OopElTableModel('权限列表', {
        cols: [
          {
            prop: 'name',
            label: 'Name'
          },
          {
            prop: undefined,
            label: '操作'
          }
        ],

        // /////////////// 必须方法 /////////////////

        // 拉取数据操作写在这里，调用后列表自动进入loading状态
        do_fetch_data(data) {
          this.data = [{
            name: 'test',
            id: 1
          },
          {
            name: 'work',
            id: 2
          },
          {
            name: 'music',
            id: 3
          }]

          this.fetch_over()
        }
      }),
      select_dir_dialog: new OopElDialogModel('修改xx对话框', {

        // /////////////// 必须方法 /////////////////

        // 必须有该方法，创建和关闭对话框时被调用
        reset_data() {
          this.data = {
            counter: 0
          }
        },
        // 必须有该方法，打开对话框前被调用
        // data参数在open被调用时被传递
        before_open(data) {
          // do some extra work
          vue.select_dir_cur_dir_file_list.fetch_data()
          this.do_open()
        },

        // /////////////// 可选方法 /////////////////

        // 默认返回true
        enable_commit() {
          if (true) {
            return true
          }
          return false
        },

        // 点击 提交按钮 后执行
        do_commit() {
          // request_net_work();
          this.close()
        }
      }),
      /**
         * relative path to root( root is configured on the backend, it's transparent to frontend)
         */
      path: this.$route.query.path == null ? '/' : this.$route.query.path,
      /**
         * file list
         */
      filelist: [
        // {
        //   name: 'hello.txt',
        //   length: 324343,
        //   type: 1
        // },
        // {
        //   name: 'dir',
        //   length: 6827384682,
        //   type: 0
        // }
      ]
    }
  },
  watch: {

  },

  created() {
    vue = this
    this.fetch_file_list()
  },
  methods: {
    select_dir_pick(id) {
      const cur_path_dirs = vue.select_dir_cur_dir_file_list.data
      const pick_dir = cur_path_dirs.find((dir) => { return dir.id === id })
      const name = pick_dir.name
      const cur_path = this.cur_path
      console.log(name)
      let dir_path = cur_path
      if (cur_path === '/') {
        dir_path = dir_path + name
      } else {
        dir_path = dir_path + '/' + name
      }
      console.log(dir_path)
      vue.picked_dir_path = dir_path
    },
    select_dir_into_dir(name) {
      const cur_path = this.cur_path
      let next_path = cur_path
      if (cur_path === '/') {
        next_path = next_path + name
      } else {
        next_path = next_path + '/' + name
      }
      console.log(next_path)
    },
    select_dir_back() {
      if (this.cur_path === '/') {
        return
      }

      const lastSplash = this.cur_path.lastIndexOf('/')
      if (lastSplash === -1) {
        throw new Error('path error')
      }

      let parent_path = this.cur_path.substring(0, lastSplash)

      if (parent_path === '') {
        parent_path = '/'
      }

      console.log(parent_path)
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
    },
    /**
       * go to parent path, equals to "cd ../"
       */
    goback() {
      // already root, do nothing
      if (this.path === '/') {
        return
      }

      const lastSplash = this.path.lastIndexOf('/')
      if (lastSplash === -1) {
        throw new Error('path error')
      }

      let parent_path = this.path.substring(0, lastSplash)

      if (parent_path === '') {
        parent_path = '/'
      }

      console.log(parent_path)

      this.$router.push({ path: '/filelist', query: { path: parent_path }})
    },
    do_upload_file() {
      const upload_elem = document.getElementById('upload_file')

      const files = upload_elem.files
      const file = files[0]

      // check file size
      // fetch file size limit first
      config_api.getConfigValue('max-upload-size').then(resp => {
        const limit_size = resp.data
        const filesize = file.size
        // file size exceeds the limit
        if (filesize > limit_size * 1024 * 1024) {
          upload_elem.value = ''
          Message.warning('Exceed upload limit: ' + limit_size + 'M')
          return
        }

        // do upload
        const form = new FormData()
        form.append('file-body', file)
        form.append(
          'file-info',
          new Blob(
            [
              JSON.stringify({
                path: this.path
              })
            ],
            {
              type: 'application/json'
            }
          )
        )

        file_api.upload(form).then((resp) => {
          console.log(resp)
        }).then((resp) => {
          Message.success('upload successfully')
          file_api.getFileList({
            path: this.path
          }).then(resp => {
            this.filelist = resp.data
          })
        }).catch(resp => {
          const code = resp.code
          if (code === 'FILE_ACCESS_DENIED') {
            Message.error('FILE_ACCESS_DENIED')
          }
        })

        upload_elem.value = ''
      }).catch((resp) => {
        if (resp.code === 'CONFIG_FILE_NOT_FOUND') {
          this.config_file_not_found_prompt = true
        }
      })
    },
    // open upload file dialog
    open_upload_file() {
      document.getElementById('upload_file').click()
    },
    download(name) {
      const location = `${process.env.VUE_APP_BASE_API}file?path=` + this.concat_path(this.path, name)
      console.log('download location: ' + location)
      window.location.href = location
    },
    concat_path(src, part) {
      if (src.charAt(src.length - 1) === '/') {
        return src + part
      } else {
        return src + '/' + part
      }
    }
  }
}
</script>
<style lang="scss">
  @import '~@/styles/common-style.scss';
  .underline {
   &:hover{
    text-decoration: underline;
   }
  }

  .el-table--medium td{
    padding: 0
  }

</style>

