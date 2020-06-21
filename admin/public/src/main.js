import Vue from 'vue'

import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import App from './App.vue'
import router from './router'
import store from './store'
import firebaseApp from 'firebase'

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

Vue.config.productionTip = false

firebaseApp.auth().onAuthStateChanged(user => {
  user.getIdTokenResult().then((idTokenResult) => {
    user.admin = idTokenResult.claims.admin
    if (user.admin) {
      store.dispatch('fetchUser', user)
    } else {
      firebaseApp
        .auth()
        .signOut()
        .then(() => {
          router.replace({
            name: 'Home'
          })
        }).catch(err => {
          alert(err)
        })
    }
  }).catch((error) => {
    alert(error)
    // console.log(error)
    // console.log('err')
  })
})

store.dispatch('getCategory')

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
