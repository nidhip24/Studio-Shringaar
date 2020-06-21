<template>
  <div class="home container">
    <b-row>
      <b-col sm="12">
        <b-row>
          <b-col sm="5">
            <h1>Home</h1>
          </b-col>
          <b-col sm="1">
            <b-icon icon="arrow-repeat" style="color: #7952b3;" font-scale="2.5" @click="refresh"></b-icon>
          </b-col>
          <b-col sm="4">
            <b-form-input v-model="search_text" placeholder="Search..."></b-form-input>
          </b-col>
          <b-col sm="2">
            <b-form-select v-model="search_selected" :options="search_option"></b-form-select>
          </b-col>
        </b-row>
        <b-table :fields="fields" responsive striped hover :items="homeFilter">
          <template v-slot:cell(remove)="data">
            <div class="remove-item" @click="remove(data.index)">
              &times;
            </div>
          </template>
          //for name
          <template v-slot:cell(nameEdit)="data">
            <div v-if="!data.item.editing" @dblclick="editProduct(data.index)">
              {{ data.item.name }}
            </div>
            <input  v-if="data.item.editing" type="text" v-model="data.item.name" @blur="doneEdit(data.index)" @keyup.enter="doneEdit(data.index)" @keyup.esc="doneEdit(data.index)" >
          </template>
          <!-- category -->
          <template v-slot:cell(cat)="data">
              {{ data.item.cat }}
          </template>
          <!-- for size -->
          <template v-slot:cell(sizeEdit)="data">
            <div v-if="!data.item.editing" @dblclick="editProduct(data.index)">
              {{ data.item.size }}
            </div>
            <input  v-if="data.item.editing" type="text" v-model="data.item.size" @blur="doneEdit(data.index)" @keyup.enter="doneEdit(data.index)" @keyup.esc="doneEdit(data.index)" >
          </template>
          <!-- for design -->
          <template v-slot:cell(designEdit)="data">
            <div v-if="!data.item.editing" @dblclick="editProduct(data.index)">
              {{ data.item.design }}
            </div>
            <input  v-if="data.item.editing" type="text" v-model="data.item.design" @blur="doneEdit(data.index)" @keyup.enter="doneEdit(data.index)" @keyup.esc="doneEdit(data.index)" >
          </template>
          <!-- for price -->
          <template v-slot:cell(priceEdit)="data">
            <div v-if="!data.item.editing" @dblclick="editProduct(data.index)">
              {{ data.item.price }}
            </div>
            <input v-if="data.item.editing" type="text" v-model="data.item.price" @blur="doneEdit(data.index)" @keyup.enter="doneEdit(data.index)" @keyup.esc="doneEdit(data.index)" >
          </template>
          <!-- for image 1 -->
          <template v-slot:cell(imgEdit)="data">
              <img :src="data.item.image" width="100" height="100" />
          </template>
          <!-- for image 2 -->
          <template v-slot:cell(imgTwoEdit)="data">
              <img :src="data.item.imagetwo" width="100" height="100" />
          </template>
          <!-- for image 3 -->
          <template v-slot:cell(imgThreeEdit)="data">
              <img :src="data.item.imagethree" width="100" height="100" />
          </template>
          <!-- for image 3 -->
          <template v-slot:cell(imgFourEdit)="data">
              <img :src="data.item.imagefour" width="100" height="100" />
          </template>
          <!-- for desc -->
          <template v-slot:cell(descEdit)="data">
            <div v-if="!data.item.editing" @dblclick="editProduct(data.index)">
              {{ data.item.desc }}
            </div>
            <input  v-if="data.item.editing" type="text" v-model="data.item.desc" @blur="doneEdit(data.index)" @keyup.enter="doneEdit(data.index)" @keyup.esc="doneEdit(data.index)" >
          </template>
        </b-table>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  data () {
    return {
      beforeCache: '',
      search_option: [
        { value: 'name', text: 'By Name' },
        { value: 'design', text: 'By Design' }
      ],
      search_selected: 'name',
      search_text: '',
      fields: [
        { key: 'remove', label: 'Delete product' },
        { key: 'nameEdit', label: 'Product Name', sortable: true },
        { key: 'status', label: 'Status', sortable: true },
        { key: 'cat', label: 'Category', sortable: true },
        { key: 'priceEdit', label: 'Price', sortable: true },
        { key: 'sizeEdit', label: 'Size' },
        { key: 'designEdit', label: 'Design', sortable: true },
        { key: 'imgEdit', label: 'Image name' },
        { key: 'imgTwoEdit', label: 'Image 2 name' },
        { key: 'imgThreeEdit', label: 'Image 3 name' },
        { key: 'imgFourEdit', label: 'Image 4 name' },
        { key: 'descEdit', label: 'Description' }
      ]
    }
  },
  directive: {
    focus: {
      inserted: function (el) {
        // Focus the element
        el.focus()
      }
    }
  },
  methods: {
    ...mapActions([
      'updateProduct',
      'initProduct',
      'resetList',
      'deleteProduct'
    ]),
    refresh () {
      // refresh
      // alert('refresh')
      this.resetList()
      for (const val of this.options) {
        if (val.value !== null) {
          // console.log('in home' + val.value)
          this.initProduct(val.value)
        }
      }
    },
    remove (index) {
      // alert(index)
      this.deleteProduct(this.productList[index])
    },
    editProduct (index) {
      // alert('in' + index)
      this.beforeCache = this.productList[index].name
      this.productList[index].editing = true
    },
    doneEdit (index) {
      if (this.productList[index].name === '' || this.productList[index].editing === false) {
        this.productList[index].name = this.beforeCache
        this.productList[index].editing = false
      } else {
        // console.log(this.productList[index])
        // alert('here' + this.productList[index])
        if (this.productList[index].imagetwo === undefined) {
          this.productList[index].imagetwo = null
        }
        if (this.productList[index].imagethree === undefined) {
          this.productList[index].imagethree = null
        }
        if (this.productList[index].imagefour === undefined) {
          this.productList[index].imagefour = null
        }
        if (this.productList[index].status === undefined) {
          this.productList[index].status = 'live'
        }
        if (!Array.isArray(this.productList[index].size)) {
          this.productList[index].size = this.productList[index].size.split(',')
        }
        this.productList[index].editing = false
        this.updateProduct(this.productList[index])
      }
    }
  },
  computed: {
    ...mapState([
      'productList',
      'options'
    ]),
    homeFilter () {
      // return this.productList
      if (this.search_text === '') {
        return this.productList
      } else {
        if (this.search_selected === 'name') {
          return this.productList.filter(p => p.name.toLowerCase().includes(this.search_text.toLowerCase()))
        } else {
          return this.productList.filter(p => p.design.toLowerCase().includes(this.search_text.toLowerCase()))
        }
      }
    }
  },
  created: function () {
    const v = this
    setTimeout(function () {
      // alert('welcome')
      v.resetList()
      for (const val of v.options) {
        if (val.value !== null) {
          // console.log('in home' + val.value)
          v.initProduct(val.value)
        }
      }
    }, 2000)
  }
}
</script>
<style lang="scss">
.remove-item{
  font-size: 24px;
  cursor: pointer;
  margin: 14px;
  &:hover {
    color: black;
  }
}
</style>
