<template>
  <div class="login container">
    <h1 class="mx-auto" style="width: 200px;">Login</h1>
    <b-row>
      <b-col md="6" offset-md="3">
        <b-form @submit.prevent="onLogin">
          <b-form-group id="input-group-1" label="Your email" label-for="input-1">
            <b-form-input
              id="input-1"
              type="email"
              v-model="formData.email"
              required
              placeholder="Enter email"
            ></b-form-input>
          </b-form-group>
          <b-form-group id="input-group-2" label="Password" label-for="input-2">
            <b-form-input
              id="input-2"
              type="password"
              v-model="formData.password"
              required
              placeholder="Enter Password"
            ></b-form-input>
          </b-form-group>
          <b-button type="submit" variant="primary">Submit</b-button>
        </b-form>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import firebase from 'firebase'
import { mapGetters } from 'vuex'

export default {
  data () {
    return {
      formData: {
        email: '',
        password: ''
      }
    }
  },
  methods: {
    onLogin () {
      const { email, password } = this.formData
      firebase
        .auth()
        .signInWithEmailAndPassword(email, password)
        .then(data => {
          this.$router.replace({ name: 'Home' })
        })
        .catch(err => {
          this.error = err.message
        })
    }
  },
  created: function () {
    if (this.user.loggedIn) {
      this.$router.replace({ name: 'Home' })
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
