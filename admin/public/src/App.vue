<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">Home <span v-if="!user.loggedIn"></span></router-link> |
      <router-link to="/addproduct" v-if="user.loggedIn">Add product |</router-link>
      <router-link to="/register" v-if="!user.loggedIn">Register |</router-link>
      <router-link to="/login" v-if="!user.loggedIn"> Login |</router-link>
      <router-link to="/makeadmin" v-if="user.loggedIn"> Make Admin |</router-link>
      <router-link to="/contentmanage" v-if="user.loggedIn"> Content Management |</router-link>
      <router-link to="/dandp" v-if="user.loggedIn">Discount & Promocode |</router-link>
      <router-link to="/order" v-if="user.loggedIn"> Orders |</router-link>
      <router-link to="/sendmail" v-if="user.loggedIn"> Send Mail</router-link> |
      <span id="logout-s" @click="logout" v-if="user.loggedIn"> Logout |</span>
    </div>
    <router-view/>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import firebase from 'firebase'

export default {
  methods: {
    ...mapActions([
      'logoutUser'
    ]),
    logout () {
      alert('logout')
      firebase
        .auth()
        .signOut()
        .then(() => {
          this.$router.replace({
            name: 'Home'
          })
        }).then(() => {
          this.logoutUser()
        }).catch(err => {
          alert(err)
        })
    }
  },
  computed: {
    // map `this.user` to `this.$store.getters.user`
    ...mapGetters({
      user: 'user'
    })
  }
}
</script>

<style lang="scss">
#logout-s{
  cursor: pointer;
  color: #2c3e50;
  font-weight: 600;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

#nav {
  padding: 30px;
  text-align: center;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
