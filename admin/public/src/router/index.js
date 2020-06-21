import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import AddProduct from '../views/AddProduct.vue'
import Register from '../views/Register.vue'
import Login from '../views/Login.vue'
import MakeAdmin from '../views/MakeAdmin.vue'
import ContentManage from '../views/ContentManage.vue'
import Order from '../views/Order.vue'
import SendMail from '../views/SendMail.vue'
import DiscountandPromocode from '../views/DiscountandPromocode.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/addproduct',
    name: 'Add Product',
    component: AddProduct
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/makeadmin',
    name: 'Make Admin',
    component: MakeAdmin
  },
  {
    path: '/contentmanage',
    name: 'Content Management',
    component: ContentManage
  },
  {
    path: '/order',
    name: 'Order',
    component: Order
  },
  {
    path: '/dandp',
    name: 'DiscountandPromocode',
    component: DiscountandPromocode
  },
  {
    path: '/sendmail',
    name: 'Send Mail',
    component: SendMail
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
