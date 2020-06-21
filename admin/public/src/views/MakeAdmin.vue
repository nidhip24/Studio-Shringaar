<template>
  <div class="make-admin container">
    <b-row>
      <b-col md="6" offset-md="3">
        <h1>Make admins</h1>
        <b-form @submit.prevent="addAdmin">
          <!-- email -->
          <b-form-group id="input-group-1" label="Enter email:" label-for="email">
            <b-form-input
            id="email"
            v-model="formdata.email"
            required
            placeholder="Enter Email ID"
            ></b-form-input>
          </b-form-group>
          <!-- button -->
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
      formdata: {
        email: ''
      }
    }
  },
  methods: {
    addAdmin () {
      const { email } = this.formdata
      firebase.functions().httpsCallable('addAdminRole')({ email: email }).then(result => {
        if (result.message !== undefined) {
          alert(result.data.message)
        } else {
          // console.log(result)
          alert(result.data.error)
        }
      })
    }
  },
  created: function () {
    if (!this.user.data.admin) {
      this.$router.replace({ name: 'Home' })
    }
  },
  computed: {
    ...mapGetters({
      user: 'user'
    })
  }
}
</script>
