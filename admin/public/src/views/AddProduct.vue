/* eslint-disable */
<template>
  <div class="add-product container">
    <b-row>
      <b-col md="6" offset-md="3">
        <h1>Add product</h1>
        <b-form @submit.prevent="addProductForm">
          <!-- name -->
          <b-form-group id="input-group-1" label="Product Name:" label-for="name">
            <b-form-input
            id="name"
            v-model="formdata.name"
            required
            placeholder="Enter name"
            ></b-form-input>
          </b-form-group>
          <!-- price and design no -->
          <b-row>
            <!-- price  -->
            <b-col md="6">
              <b-form-group id="input-group-2" label="Product Price:" label-for="price">
                <b-form-input
                id="price"
                v-model="formdata.price"
                required
                placeholder="Enter price"
                ></b-form-input>
              </b-form-group>
            </b-col>
            <b-col md="6">
              <b-form-group id="input-group-211" label="Design number" label-for="design">
                <b-form-input
                id="design"
                v-model="formdata.design"
                required
                placeholder="Enter design number"
                ></b-form-input>
              </b-form-group>
            </b-col>
          </b-row>
          <!-- Size -->
          <b-form-group id="input-group-00" label="Sizes" label-for="size">
            <b-form-input
            id="size"
            v-model="formdata.size"
            required
            placeholder="Eg 38/40/42/44"
            ></b-form-input>
          </b-form-group>
          <!--  product description -->
          <b-form-textarea id="input-group-3" label="Product Description:"
            placeholder="Enter something..."
            rows="3"
            max-rows="6"
            v-model="formdata.descc"
            required
          ></b-form-textarea>
          <!-- Image 1 and 2 -->
          <b-row>
            <!-- image 1  -->
            <b-col md="6">
              <b-form-group id="input-group-31" label="Image 1" label-for="img1">
                <b-form-file
                  id="img1"
                  v-model="formdata.img"
                  :state="Boolean(formdata.img)"
                  accept="image/*"
                  placeholder="Choose file or drop it here..."
                  drop-placeholder="Drop file here..." />
              </b-form-group>
            </b-col>
            <!-- image 2  -->
            <b-col md="6">
              <b-form-group id="input-group-32" label="Image 2" label-for="img2">
                <b-form-file
                  id="img2"
                  v-model="formdata.img2"
                  :state="Boolean(formdata.img2)"
                  accept="image/*"
                  placeholder="Choose file or drop it here..."
                  drop-placeholder="Drop file here..." />
              </b-form-group>
            </b-col>
          </b-row>
          <!-- Image 3 and 4 -->
          <b-row>
            <!-- image 3  -->
            <b-col md="6">
              <b-form-group id="input-group-33" label="Image 3" label-for="img3">
                <b-form-file
                  id="img3"
                  v-model="formdata.img3"
                  :state="Boolean(formdata.img3)"
                  accept="image/*"
                  placeholder="Choose file or drop it here..."
                  drop-placeholder="Drop file here..." />
              </b-form-group>
            </b-col>
            <!-- image 4  -->
            <b-col md="6">
              <b-form-group id="input-group-34" label="Image 4" label-for="img4">
                <b-form-file
                  id="img4"
                  v-model="formdata.img4"
                  :state="Boolean(formdata.img4)"
                  accept="image/*"
                  placeholder="Choose file or drop it here..."
                  drop-placeholder="Drop file here..." />
              </b-form-group>
            </b-col>
          </b-row>
          <b-form-select v-model="selected" :options="categoryList"></b-form-select>
          <div class="mt-3">Selected: <strong>{{ selected }}</strong></div>
          <b-button type="submit" variant="primary">Submit</b-button>
        </b-form>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import { mapActions, mapGetters, mapState } from 'vuex'

export default {
  data () {
    return {
      selected: null,
      file: null,
      formdata: {
        name: '',
        price: '',
        descc: '',
        design: '',
        size: '',
        img: null,
        img2: null,
        img3: null,
        img4: null
      }
    }
  },
  methods: {
    ...mapActions([
      'addProduct',
      'getCategory'
    ]),
    addProductForm () {
      // alert(JSON.stringify(this.formdata))
      const { name, price, descc, design, size, img, img2, img3, img4 } = this.formdata
      const cat = this.selected
      // alert(img)
      if (!img) {
        alert('select all images')
        return
      }
      if (!img2) {
        alert('Please take note only one photo is selected')
      } else if (!img3) {
        alert('Please take note only two photo is selected')
      } else {
        alert('Please take note only three photo is selected')
      }
      if (!this.selected) {
        alert('Please select category')
        return
      }
      // const img = this.file
      // const img = ''
      const desc = descc.replace(/(?:\r\n|\r|\n)/g, '<br>')
      // alert(temp)
      const payload = {
        name,
        price,
        desc,
        design,
        size,
        img,
        img2,
        img3,
        img4,
        cat
      }
      this.addProduct(payload)
      // clearing form data
      this.formdata = {
        name: '',
        price: '',
        descc: '',
        img: null,
        img2: null,
        img3: null,
        img4: null
      }
      this.selected = null
      // this.file = null
    }
  },
  computed: {
    ...mapGetters({
      user: 'user'
    }),
    ...mapState([
      'categoryList'
    ])
  },
  created: function () {
    this.getCategory()
    if (!this.user.loggedIn) {
      this.$router.replace({ name: 'Home' })
    }
  }
}
</script>
