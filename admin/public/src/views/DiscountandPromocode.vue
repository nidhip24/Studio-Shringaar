<template>
  <div class="dandp container">
    <b-row>
      <b-row class="mx-auto">
        <h4>Discount and Promocode</h4>
      </b-row>
      <b-col sm="12">
        <b-row>
          <b-col sm="3">
            <!-- discount modal button -->
            <b-row>
              <b-card style="width: 100%" header="Discount Content">
                <b-button v-b-modal.dicount-modal @click="addCategory">Add Discount</b-button>
              </b-card>
            </b-row>
            <!-- promocode modal button -->
            <b-row>
              <b-card style="margin-top: 10px; width: 100%" header="Promocode">
                <b-button v-b-modal.promocode-modal>Add Promocode</b-button>
              </b-card>
            </b-row>
          </b-col>
          <b-col sm="8">
            <!-- All Discount Listed here -->
            <b-row style="margin-left: 10px;">
              <b-card header="Discounts">
                <b-table :items="discountList" :fields="discountfields" striped hover responsive="sm">
                  <!-- Remove item -->
                  <template v-slot:cell(remove)="data">
                    <div class="remove-item" @click="removeDiscount(data.index)">
                      &times;
                    </div>
                  </template>
                </b-table>
              </b-card>
            </b-row>
            <!-- All Promocode Listed here -->
            <b-row style="margin-left: 10px;">
              <b-card header="Promocodes"  style="margin-top: 10px;">
                <b-table :items="promocodeList" :fields="promofields" striped hover responsive="sm">
                  <!-- Remove item -->
                  <template v-slot:cell(remove)="data">
                    <div class="remove-item" @click="removePromocode(data.index)">
                      &times;
                    </div>
                  </template>
                </b-table>
              </b-card>
            </b-row>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
    <b-modal id="dicount-modal" size="lg" title="Discount Modal" hide-footer>
      <b-form @submit.prevent="addDiscount">
        <!-- discount -->
        <b-col sm="12">
          <b-form-group id="input-group-1121" label="Discount Name" label-for='dname'>
            <b-form-input
            id='dname'
            v-model="discountform.name"
            required
            type="text"
            placeholder="Eg. DISCOUNT NAME"
            ></b-form-input>
          </b-form-group>
          <b-form-group id="input-group-1" label="Discount" label-for='discount'>
            <b-form-input
            id='discount'
            v-model="discountform.discount"
            required
            type="number"
            placeholder="Eg. 10 or 20"
            ></b-form-input>
          </b-form-group>
          <label for="startDate">Select Starting date</label>
          <b-form-datepicker
            id="startDate"
            today-button
            reset-button
            close-button
            v-model="discountform.startDate"
            locale="en"
          ></b-form-datepicker>
          <label for="datepicker-buttons">Select Ending date</label>
          <b-form-datepicker
            id="datepicker-buttons"
            today-button
            reset-button
            close-button
            v-model="discountform.endDate"
            locale="en"
          ></b-form-datepicker>
          <b-form-group id="input-group-2" label="Max Discount" label-for='maxDiscount'>
            <b-form-input
            type="number"
            id='maxDiscount'
            v-model="discountform.maxdiscount"
            required
            placeholder="Eg. 200 or 300"
            ></b-form-input>
          </b-form-group>
          <b-form-select v-model="selected" :options="localOptions"></b-form-select>
          <!-- button -->
          <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
        </b-col>
      </b-form>
    </b-modal>
    <b-modal id="promocode-modal" size="lg" title="Promocode Modal" hide-footer>
      <b-form @submit.prevent="addPromocode">
        <!-- promocode -->
        <b-col sm="12">
          <b-form-group id="input-group-01" label="Promocode Name" label-for='name'>
            <b-form-input
            id='name'
            v-model="promoform.name"
            required
            type="text"
            placeholder="Eg. DIWALI2020"
            ></b-form-input>
          </b-form-group>
          <b-form-group id="input-group-11" label="Discount" label-for='discount2'>
            <b-form-input
            id='discount2'
            v-model="promoform.discount"
            required
            type="number"
            placeholder="Eg. 10 or 20"
            ></b-form-input>
          </b-form-group>
          <label for="startDate2">Select Starting date</label>
          <b-form-datepicker
            id="startDate2"
            today-button
            reset-button
            close-button
            v-model="promoform.startDate"
            locale="en"
          ></b-form-datepicker>
          <label for="datepicker-buttons2">Select Ending date</label>
          <b-form-datepicker
            id="datepicker-buttons2"
            today-button
            reset-button
            close-button
            v-model="promoform.endDate"
            locale="en"
          ></b-form-datepicker>
          <b-form-group id="input-group-22" label="Max Discount" label-for='maxDiscount2'>
            <b-form-input
            type="number"
            id='maxDiscount2'
            v-model="promoform.maxdiscount"
            required
            placeholder="Eg. 200 or 300"
            ></b-form-input>
          </b-form-group>
          <!-- button -->
          <b-button type="submit" variant="primary" style="margin-top: 10px">Submit</b-button>
        </b-col>
      </b-form>
    </b-modal>
  </div>
</template>
<script>
import { mapActions, mapState } from 'vuex'

export default {
  data () {
    return {
      selected: null,
      localOptions: [],
      discountform: {
        name: '',
        discount: null,
        startDate: '',
        endDate: '',
        maxdiscount: 0
      },
      promoform: {
        name: '',
        discount: null,
        startDate: '',
        endDate: '',
        maxdiscount: 0
      },
      filter: 'all',
      discountfields: [
        {
          key: 'remove',
          label: 'Delete'
        },
        {
          key: 'name',
          sortable: true
        },
        {
          key: 'cat',
          sortable: true
        },
        {
          key: 'discount',
          sortable: true
        },
        {
          key: 'maxDiscount',
          sortable: true
        },
        {
          key: 'startDate',
          sortable: true
        },
        {
          key: 'endDate',
          sortable: true
        }
      ],
      promofields: [
        {
          key: 'remove',
          label: 'Delete'
        },
        {
          key: 'name',
          sortable: true
        },
        {
          key: 'discount',
          sortable: true
        },
        {
          key: 'maxDiscount',
          sortable: true
        },
        {
          key: 'startDate',
          sortable: true
        },
        {
          key: 'endDate',
          sortable: true
        }
      ]
    }
  },
  created: function () {
    this.initPromocode()
    this.initDiscount()
  },
  computed: {
    ...mapState([
      'options',
      'discountList',
      'promocodeList'
    ])
  },
  methods: {
    ...mapActions([
      'getCategory',
      'addPromocodee',
      'addDiscountt',
      'initPromocode',
      'initDiscount',
      'deletePromocode',
      'deleteDiscount'
    ]),
    addCategory () {
      this.localOptions = []
      this.localOptions.push({ value: 'all', text: 'All' })
      for (let i = 0; i < this.options.length; i++) {
        if (this.options[i].value !== null) {
          this.localOptions.push(this.options[i])
        }
      }
    },
    addDiscount () {
      const { name, discount, startDate, endDate, maxdiscount } = this.discountform
      if (name === '' || discount === null || startDate === '' || endDate === '' || maxdiscount === 0 || this.selected === null) {
        alert('One of the field is empty')
        return
      }
      const cat = this.selected
      const payload = {
        name,
        discount,
        startDate,
        endDate,
        maxdiscount,
        cat
      }
      this.addDiscountt(payload)
    },
    addPromocode () {
      const { name, discount, startDate, endDate, maxdiscount } = this.promoform
      if (discount === null || startDate === '' || endDate === '' || maxdiscount === 0 || name === '') {
        alert('One of the field is empty')
        return
      }
      const payload = {
        name,
        discount,
        startDate,
        endDate,
        maxdiscount
      }
      this.addPromocodee(payload)
    },
    removePromocode (index) {
      const id = this.promocodeList[index].id
      // alert(id)
      this.deletePromocode({
        id: id
      })
    },
    removeDiscount (index) {
      const id = this.discountList[index].id
      // alert(id)
      this.deleteDiscount({
        id: id
      })
    }
  }
}
</script>
