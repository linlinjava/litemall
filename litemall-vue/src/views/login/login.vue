<template>
	<div class="login">
		<login-header />
		<login-request />
		<login-footer />
	</div>
</template>

<script>
import loginHeader from './login-header';
import loginRequest from './login-request';
import loginFooter from './login-footer';

export default {
  components: {
    [loginHeader.name]: loginHeader,
    [loginRequest.name]: loginRequest,
    [loginFooter.name]: loginFooter
  }
};
</script>


<style lang="scss" scoped>
.login {
  position: relative;
  background-color: #fff;
}
</style>
