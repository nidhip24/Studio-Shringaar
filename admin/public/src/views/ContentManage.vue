<template>
  <div class="contentmanagemnet-container container">
    <b-row>
      <b-col sm="12">
        <b-row>
          <!-- Home & category modal button -->
          <b-col sm="3">
            <!-- Home modal button -->
            <b-row>
              <b-card style="width: 100%"
              header="Home Content">
                <b-button v-b-modal.modal-1>Add layout for Home</b-button>
              </b-card>
            </b-row>
            <!-- category modal button -->
            <b-row>
              <b-card style="margin-top: 10px; width: 100%"
              header="Category">
                <b-button v-b-modal.category-modal>Add Category</b-button>
              </b-card>
            </b-row>
          </b-col>
          <!-- content of home & category -->
          <b-col sm="8" style="margin-left: 10px">
            <!-- Home content -->
            <b-row>
              <b-card
              header="Content">
                <b-table :fields="fields" responsive striped hover :items="homeList">
                  <!-- delete -->
                  <template v-slot:cell(remove)="data">
                    <div class="remove-item" @click="remove(data.index)">
                      &times;
                    </div>
                  </template>
                  <!-- edit -->
                  <template v-slot:cell(edit)="data">
                    <div class="edit-item" @click="editHome(data.index)">
                      <b-icon-pencil></b-icon-pencil>
                    </div>
                  </template>
                  //for code
                  <template v-slot:cell(code)="data">
                    <p v-if="data.item.code === 0">Product Carousel</p>
                    <p v-if="data.item.code === 1">Three Product Layout</p>
                    <p v-if="data.item.code === 2">Slider</p>
                    <p v-if="data.item.code === 3">Poster</p>
                  </template>
                  //for cat
                  <template v-slot:cell(cat)="data">
                    {{ data.item.cat }}
                  </template>
                  //for image 1
                  <template v-slot:cell(img)="data">
                      <img :src="data.item.img" width="100" height="100" v-if="data.item.img !== undefined" />
                  </template>
                  //for image 2
                  <template v-slot:cell(img2)="data">
                      <img :src="data.item.img2" width="100" height="100" v-if="data.item.img2 !== undefined" />
                  </template>
                  //for image 3
                  <template v-slot:cell(img3)="data">
                      <img :src="data.item.img3" width="100" height="100" v-if="data.item.img3 !== undefined" />
                  </template>
                  //for image 4
                  <template v-slot:cell(img4)="data">
                      <img :src="data.item.img4" width="100" height="100" v-if="data.item.img4 !== undefined" />
                  </template>
                </b-table>
              </b-card>
            </b-row>
            <!-- Category list -->
            <b-row style="margin-top: 10px;">
              <b-card
              header="Category list">
                <b-table :fields="fields2" responsive striped hover :items="categoryList">
                  <template v-slot:cell(remove)="data">
                    <div class="remove-item" @click="removeCategory(data.index)">
                      &times;
                    </div>
                  </template>
                  //for cat
                  <template v-slot:cell(cat)="data">
                    {{ data.item }}
                  </template>
                </b-table>
              </b-card>
            </b-row>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
    <!-- Home modal -->
    <b-modal id="modal-1" size="lg" title="Add Content for Home" hide-footer>
      <b-row>
        <!-- Home side list -->
        <b-col sm="3">
          <b-list-group>
            <b-list-group-item button @click="addContent(0)" >Product Carousel</b-list-group-item>
            <b-list-group-item button @click="addContent(1)">Three Product Layout</b-list-group-item>
            <b-list-group-item button @click="addContent(2)">Slider</b-list-group-item>
            <b-list-group-item button @click="addContent(3)">Poster</b-list-group-item>
          </b-list-group>
        </b-col>
        <!-- Form Content -->
         <b-col sm="9">
           <!-- For zero -->
           <div v-if="zero">
             <b-form @submit.prevent="addFormZero">
               <b-row>
                 <!-- total -->
                 <b-col sm="12">
                  <b-form-group id="input-group-1" label="Enter Total:" label-for="total">
                    <b-form-input
                    id="total"
                    v-model="formdatazero.total"
                    required
                    placeholder="Total number of product to be displayed"
                    ></b-form-input>
                  </b-form-group>
                 </b-col>
                <!-- Category -->
                <b-col sm="12">
                  <b-form-select v-model="selected" :options="options"></b-form-select>
                </b-col>
               </b-row>
              <!-- button -->
              <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
            </b-form>
           </div>
           <!-- For one -->
           <div v-if="one">
             <b-form @submit.prevent="addFormOne">
               <b-row>
                <!-- Category -->
                <b-col sm="12">
                  <b-form-select v-model="selectedOne" :options="options"></b-form-select>
                </b-col>
               </b-row>
              <!-- button -->
              <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
            </b-form>
           </div>
           <!-- For two -->
           <div v-if="two">
             <b-form @submit.prevent="addFormTwo">
                <b-row>
                  <!-- image 1  -->
                  <b-col md="6">
                    <b-form-group id="input-group-31" label="Slider Image 1" label-for="img1">
                      <b-form-file
                        id="img1"
                        v-model="formdatatwo.img"
                        :state="Boolean(formdatatwo.img)"
                        accept="image/*"
                        placeholder="Choose file or drop it here..."
                        drop-placeholder="Drop file here..." />
                    </b-form-group>
                  </b-col>
                  <!-- image 2  -->
                  <b-col md="6">
                    <b-form-group id="input-group-32" label="Slider Image 2" label-for="img2">
                      <b-form-file
                        id="img2"
                        v-model="formdatatwo.img2"
                        :state="Boolean(formdatatwo.img2)"
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
                    <b-form-group id="input-group-33" label="Slider Image 3" label-for="img3">
                      <b-form-file
                        id="img3"
                        v-model="formdatatwo.img3"
                        :state="Boolean(formdatatwo.img3)"
                        accept="image/*"
                        placeholder="Choose file or drop it here..."
                        drop-placeholder="Drop file here..." />
                    </b-form-group>
                  </b-col>
                  <!-- image 4  -->
                  <b-col md="6">
                    <b-form-group id="input-group-34" label="Slider Image 4" label-for="img4">
                      <b-form-file
                        id="img4"
                        v-model="formdatatwo.img4"
                        :state="Boolean(formdatatwo.img4)"
                        accept="image/*"
                        placeholder="Choose file or drop it here..."
                        drop-placeholder="Drop file here..." />
                    </b-form-group>
                  </b-col>
                </b-row>
                <!-- button -->
                <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
             </b-form>
           </div>
           <!-- For three -->
           <div v-if="three">
             <b-form @submit.prevent="addFormThree">
               <b-row>
                  <!-- image 1  -->
                  <b-col md="6">
                    <b-form-group id="input-group-31" label="Poster image" label-for="img1">
                      <b-form-file
                        id="img1"
                        v-model="formdatathree.img"
                        :state="Boolean(formdatathree.img)"
                        accept="image/*"
                        placeholder="Choose file or drop it here..."
                        drop-placeholder="Drop file here..." />
                    </b-form-group>
                  </b-col>
               </b-row>
               <!-- button -->
              <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
             </b-form>
           </div>
         </b-col>
      </b-row>
    </b-modal>
    <!-- Category modal -->
    <b-modal id="category-modal" size="sm" title="Add Category" hide-footer>
      <b-row>
        <b-col>
          <div v-if="zero">
             <b-form @submit.prevent="addFormCategory">
               <b-row>
                 <!-- total -->
                 <b-col sm="12">
                  <b-form-group id="input-group-1" label="Enter Category Name:" label-for='category'>
                    <b-form-input
                    id='category'
                    v-model="formdatacategory.category"
                    required
                    placeholder="dress or kurta..."
                    ></b-form-input>
                  </b-form-group>
                 </b-col>
                 <!-- image 1  -->
                <b-col>
                  <b-form-group id="input-group-31" label="Slider Image 1" label-for="image">
                    <b-form-file
                      id="image"
                      v-model="formdatacategory.image"
                      accept="image/*"
                      placeholder="Choose file or drop it here..."
                      drop-placeholder="Drop file here..." />
                  </b-form-group>
                </b-col>
               </b-row>
              <!-- button -->
              <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
            </b-form>
           </div>
        </b-col>
      </b-row>
    </b-modal>

    <!-- Edit Home modal -->
    <b-modal id="edit-home-modal" ref="edit-home-modal" size="sm" title="Edit Content for Home" hide-footer>
      <b-row>
        <!-- Form Content -->
         <b-col>
           <!-- For zero -->
           <div v-if="editModal == 0">
             <b-form @submit.prevent="editFormZero">
               <b-row>
                 <!-- total -->
                 <b-col sm="12">
                  <b-form-group id="input-group-1" label="Enter Total:" label-for="total">
                    <b-form-input
                    id="total"
                    v-model="formdatazero.total"
                    required
                    type="number"
                    placeholder="Total number of product to be displayed"
                    ></b-form-input>
                  </b-form-group>
                 </b-col>
                <!-- Category -->
                <b-col sm="12">
                  <b-form-select v-model="selected" :options="options"></b-form-select>
                </b-col>
               </b-row>
              <!-- button -->
              <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
            </b-form>
           </div>
           <!-- For one -->
           <div v-if="editModal == 1">
             <b-form @submit.prevent="editFormOne">
                <b-row>
                  <!-- Category -->
                  <b-col sm="12">
                    <b-form-select v-model="selectedOne" :options="options"></b-form-select>
                  </b-col>
                </b-row>
                <!-- button -->
                <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
              </b-form>
           </div>
           <!-- For two -->
           <div v-if="editModal == 2">
             <b-form @submit.prevent="editFormTwo">
                <!-- image 1  -->
                <b-col>
                  <b-form-group id="input-group-31" label="Slider Image 1" label-for="img1">
                    <b-form-file
                      id="img1"
                      v-model="formdatatwo.img"
                      :state="Boolean(formdatatwo.img)"
                      accept="image/*"
                      placeholder="Choose file or drop it here..."
                      drop-placeholder="Drop file here..." />
                  </b-form-group>
                </b-col>
                <!-- image 2  -->
                <b-col>
                  <b-form-group id="input-group-32" label="Slider Image 2" label-for="img2">
                    <b-form-file
                      id="img2"
                      v-model="formdatatwo.img2"
                      :state="Boolean(formdatatwo.img2)"
                      accept="image/*"
                      placeholder="Choose file or drop it here..."
                      drop-placeholder="Drop file here..." />
                  </b-form-group>
                </b-col>
                <!-- image 3  -->
                <b-col>
                  <b-form-group id="input-group-33" label="Slider Image 3" label-for="img3">
                    <b-form-file
                      id="img3"
                      v-model="formdatatwo.img3"
                      :state="Boolean(formdatatwo.img3)"
                      accept="image/*"
                      placeholder="Choose file or drop it here..."
                      drop-placeholder="Drop file here..." />
                  </b-form-group>
                </b-col>
                <!-- image 4  -->
                <b-col>
                  <b-form-group id="input-group-34" label="Slider Image 4" label-for="img4">
                    <b-form-file
                      id="img4"
                      v-model="formdatatwo.img4"
                      :state="Boolean(formdatatwo.img4)"
                      accept="image/*"
                      placeholder="Choose file or drop it here..."
                      drop-placeholder="Drop file here..." />
                  </b-form-group>
                </b-col>
                <!-- button -->
                <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
             </b-form>
           </div>
           <!-- For three -->
           <div v-if="editModal == 3">
             <b-form @submit.prevent="editFormThree">
                <!-- image 1  -->
                <b-col>
                  <b-form-group id="input-group-31" label="Poster image" label-for="img1">
                    <b-form-file
                      id="img1"
                      v-model="formdatathree.img"
                      :state="Boolean(formdatathree.img)"
                      accept="image/*"
                      placeholder="Choose file or drop it here..."
                      drop-placeholder="Drop file here..." />
                  </b-form-group>
                </b-col>
                <!-- button -->
                <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
             </b-form>
           </div>
         </b-col>
      </b-row>
    </b-modal>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'

export default {
  data () {
    return {
      editModal: null,
      docID: null,
      selected: null,
      selectedOne: null,
      zero: true,
      one: false,
      two: false,
      three: false,
      formdatazero: {
        total: 10,
        category: ''
      },
      formdatatwo: {
        img: null,
        img2: null,
        img3: null,
        img4: null
      },
      formdatathree: {
        img: null
      },
      formdatacategory: {
        category: '',
        image: null
      },
      fields2: [
        { key: 'remove', label: 'Delete' },
        { key: 'cat', label: 'Category' }
      ],
      fields: [
        { key: 'remove', label: 'Delete' },
        { key: 'edit', label: 'Edit' },
        { key: 'code', label: 'Type' },
        { key: 'cat', label: 'Category' },
        { key: 'total', label: 'total' },
        { key: 'img', label: 'Image1' },
        { key: 'img2', label: 'Image2' },
        { key: 'img3', label: 'Image3' },
        { key: 'img4', label: 'Image4' }
      ]
    }
  },
  created: function () {
    this.getCategory()
    this.resetHome()
    this.inithome()
  },
  computed: {
    ...mapState([
      'homeList',
      'options',
      'categoryList',
      'categoryListWithLink'
    ])
  },
  methods: {
    ...mapActions([
      'getCategory',
      'updateCategory',
      'updateCategoryList',
      'addToHome',
      'inithome',
      'deleteHome',
      'resetHome',
      'updateHome'
    ]),
    remove (index) {
      // alert(index)
      this.deleteHome(this.homeList[index])
    },
    editHome (index) {
      this.editModal = this.homeList[index].code
      this.docID = this.homeList[index].id
      this.$refs['edit-home-modal'].show()
    },
    removeCategory (index) {
      // alert(index)
      this.categoryList.splice(index, 1)
      this.categoryListWithLink.splice(index, 1)
      this.updateCategoryList(this.categoryListWithLink)
    },
    // ADD HOME
    addFormZero () {
      if (!this.selected) {
        alert('Please select category')
        // return
      }
      const { total } = this.formdatazero
      const cat = this.selected
      const code = 0
      const payload = {
        total,
        cat,
        code
      }
      this.addToHome(payload)

      this.formdatazero = {
        total: 10,
        category: ''
      }

      this.selected = null
    },
    addFormOne () {
      if (!this.selectedOne) {
        alert('Please select category')
        return
      }
      const cat = this.selectedOne
      const code = 1
      const payload = {
        cat,
        code
      }
      this.addToHome(payload)

      this.selectedOne = null
    },
    addFormTwo () {
      const { img, img2, img3, img4 } = this.formdatatwo
      const code = 2
      const payload = {
        img,
        img2,
        img3,
        img4,
        code
      }
      this.addToHome(payload)
      this.formdatatwo = {
        img: null,
        img2: null,
        img3: null,
        img4: null
      }
    },
    addFormThree () {
      const { img } = this.formdatathree
      const code = 3
      const payload = {
        img,
        code
      }
      this.addToHome(payload)
      this.formdatathree = {
        img: null
      }
    },
    // END ADD HOME
    // EDIT
    editFormZero () {
      if (!this.selected) {
        alert('Please select category')
        // return
      }
      let { total } = this.formdatazero
      total = Number(total)
      const cat = this.selected
      const code = 0
      const docID = this.docID
      const payload = {
        total,
        cat,
        code,
        docID
      }
      this.updateHome(payload)

      this.formdatazero = {
        total: 10,
        category: ''
      }

      this.selected = null
    },
    editFormOne () {
      if (!this.selectedOne) {
        alert('Please select category')
        return
      }
      const cat = this.selectedOne
      const code = 1
      const docID = this.docID
      const payload = {
        cat,
        code,
        docID
      }
      this.updateHome(payload)

      this.selectedOne = null
    },
    editFormTwo () {
      const { img, img2, img3, img4 } = this.formdatatwo
      const code = 2
      const docID = this.docID
      const payload = {
        img,
        img2,
        img3,
        img4,
        code,
        docID
      }
      this.updateHome(payload)
      this.formdatatwo = {
        img: null,
        img2: null,
        img3: null,
        img4: null
      }
    },
    editFormThree () {
      const { img } = this.formdatathree
      const code = 3
      const docID = this.docID
      const payload = {
        img,
        code,
        docID
      }
      this.updateHome(payload)
      this.formdatathree = {
        img: null
      }
    },
    // END EDIT
    addFormCategory () {
      const { category, image } = this.formdatacategory
      if (category === '' || image === null) {
        alert('field is empty or image is not selected')
        return
      }
      // this.categoryList.push(category)
      const str = this.rndStr(10)
      const payload = {
        c: category.toLowerCase(),
        image: image,
        str: str
      }
      this.updateCategory(payload)
    },
    rndStr (len) {
      let text = ' '
      const chars = 'abcdefghijklmnopqrstuvwxyz0123456789QWERTYUIOPASDFGHJKLZXCVBNNM'

      for (let i = 0; i < len; i++) {
        text += chars.charAt(Math.floor(Math.random() * chars.length))
      }

      return text
    },
    addContent (index) {
      if (index === 0) {
        this.zero = true
        this.one = false
        this.two = false
        this.three = false
      } else if (index === 1) {
        this.zero = false
        this.one = true
        this.two = false
        this.three = false
      } else if (index === 2) {
        this.zero = false
        this.one = false
        this.two = true
        this.three = false
      } else {
        this.zero = false
        this.one = false
        this.two = false
        this.three = true
      }
    }
  }
}
</script>
<style lang="scss">
.remove-item{
  font-size: 24px;
  cursor: pointer;
  &:hover {
    color: black;
  }
}
.edit-item{
  font-size: 24px;
  margin-top: 15px;
  cursor: pointer;
  &:hover {
    color: black;
  }
}
</style>
