<template>
  <div class="contentmanagemnet-container container">
      <h4>Orders</h4>
      <div>
        <b-button-group>
          <b-button variant="dark" @click="filter = 'all'">ALL</b-button>
          <b-button variant="primary" @click="filter = 'pending'">Pending</b-button>
          <b-button variant="primary" @click="filter = 'processed'">Processed</b-button>
          <b-button variant="primary" @click="filter = 'in-transit'">In-transit</b-button>
          <b-button variant="success" @click="filter = 'delivered'">Delivered</b-button>
          <b-button variant="danger" @click="filter = 'other'">Others</b-button>
        </b-button-group>
      </div>
      <b-table :items="orderListFiltered" :fields="fields" striped hover responsive="sm">
        <template v-slot:cell(show_details)="row">
          <b-button size="sm" @click="row.toggleDetails" class="mr-2">
            {{ row.detailsShowing ? 'Hide' : 'Show'}} Products
          </b-button>
        </template>
        <template v-slot:row-details="row">
          <b-card>
            <b-row style="margin: 10px" v-if="row.item.addressID !== undefined">
              <b-col sm="4">
                <h4>Address Details</h4>
                Name    :{{ row.item.name }} <br>
                Address :{{ row.item.address }} <br>
                Phone   :{{ row.item.phone }} <br>
                City    :{{ row.item.city }} <br>
                Country :{{ row.item.country }} <br>
                State   :{{ row.item.state }} <br>
              </b-col>
              <b-col sm="2">
                <h4>Order Status</h4>
                <b-form-select v-model="selected" :options="status_option"></b-form-select>
                <b-button variant="dark" style="margin: 10px" @click="updateOrderStatus(row.item.id, row.item.uid)">update</b-button>
              </b-col>
            </b-row>
            <b-row v-else style="margin: 10px">
              <b-col>
                <h5>CANNOT RETRIVE ORDER ADDRESS</h5>
              </b-col>
            </b-row>
            <b-row style="margin: 10px">
              <b-col>
                <h4>Ordered Products</h4>
              </b-col>
            </b-row>
            <b-row v-for="(item) in row.item.product_list" :key="item.id" style="margin: 10px">
              <b-col sm="3" class="text-sm-right"><b-img-lazy height="100" width="100" :src="item.img" alt="Image 8"></b-img-lazy></b-col>
              <b-col>
                <b>Product Name</b> :{{ item.name }} <br>
                <b>Category</b> :{{ item.cat }} <br>
                <b>Price</b> :{{ item.price }} <br>
                <b>Size</b> :{{ item.size }} <br>
                <b>Quantity</b> :{{ item.qty }} <br>
              </b-col>
            </b-row>

            <b-button size="sm" @click="row.toggleDetails">Hide Products</b-button>
          </b-card>
        </template>
      </b-table>
  </div>
</template>

<script>
import { mapActions, mapState } from 'vuex'

export default {
  data () {
    return {
      selected: null,
      status_option: [
        { value: null, text: 'Please select an option' },
        { value: 'processed', text: 'Processed' },
        { value: 'in-transit', text: 'In-transit' },
        { value: 'delivered', text: 'Delivered' }
      ],
      filter: 'all',
      fields: [
        {
          key: 'name',
          sortable: true
        },
        {
          key: 'orderID',
          sortable: false
        },
        {
          key: 'payment_id',
          sortable: false
        },
        {
          key: 'amount',
          sortable: true
        },
        {
          key: 'status',
          sortable: true
        },
        {
          key: 'order_status',
          sortable: true
        },
        {
          key: 'timestamp',
          sortable: true
        },
        {
          key: 'show_details',
          sortable: false
        }]
    }
  },
  created: function () {
    this.initorder()
    this.temporderList = this.orderList
  },
  computed: {
    ...mapState([
      'orderList'
    ]),
    orderListFiltered () {
      if (this.filter === 'all') {
        return this.orderList
      } else if (this.filter === 'pending') {
        return this.orderList.filter(order => order.order_status === 'pending')
      } else if (this.filter === 'processed') {
        return this.orderList.filter(order => order.order_status === 'processed')
      } else if (this.filter === 'in-transit') {
        return this.orderList.filter(order => order.order_status === 'in-transit')
      } else if (this.filter === 'delivered') {
        return this.orderList.filter(order => order.order_status === 'delivered')
      } else if (this.filter === 'other') {
        return this.orderList.filter(order => order.order_status !== 'in-transit' && order.order_status !== 'pending' && order.order_status !== 'delivered' && order.order_status !== 'processed')
      } else {
        return this.orderList
      }
    }
  },
  methods: {
    ...mapActions([
      'initorder',
      'updateorderstatus'
    ]),
    updateOrderStatus (id, uid) {
      // alert(id + '  ' + uid)
      if (this.selected !== null) {
        const payload = {
          id: id,
          uid: uid,
          stat: this.selected
        }
        this.updateorderstatus(payload)
        this.selected = null
      } else {
        alert('Please select status')
      }
    }
  }
}
</script>
