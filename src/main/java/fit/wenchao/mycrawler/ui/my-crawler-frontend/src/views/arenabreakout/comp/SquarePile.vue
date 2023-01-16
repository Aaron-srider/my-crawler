<!--  -->
<template>
  <div class="flex row">
    <div v-for="col of x" :key="'col-' + col" class="flex column" :style="`width: ${width}px; background-color: red; border-right : 1px solid`">
      <div v-for="row of y" :key="'row-'+ row" :style="`height: ${height}px; background-color: red; border-bottom : 1px solid`" />
    </div>
  </div>

</template>

<script>

export default {
  components: {},
  props: {

    x: {
      type: Number,
      require: true
    },
    y: {
      type: Number,
      require: true
    },
    width: {
      type: Number,
      require: true
    },
    height: {
      type: Number,
      require: true
    }
  },
  data() {
    return {

    }
  },
  computed: {},

  created() {

  },

  methods: {

  }
}
</script>
<style lang="scss" scoped>
@import '~@/styles/common-style.scss';

</style>
