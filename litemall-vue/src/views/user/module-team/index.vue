<template>
	<div class="user_team">
		<van-cell-group>
			<van-cell title="我的团队(50)">
				<div>
					<van-icon name="invitation" />
					<router-link to="/user/invitation">邀请会员</router-link>
				</div>
			</van-cell>
		</van-cell-group>

		<van-cell-group>
			<van-cell>
				<div slot="title" class="user_member">
					<div class="user_avatar float-l">
						<img src="../../../assets/images/avatar_default.png" alt="头像">
					</div>
					<div class="user_info">
						<div>张三</div>
						<div>一级会员</div>
					</div>
				</div>
			</van-cell>
			<van-cell>
				<div slot="title" class="user_member">
					<div class="user_avatar float-l">
						<img src="../../../assets/images/avatar_default.png" alt="头像">
					</div>
					<div class="user_info">
						<div>张三</div>
						<div>一级会员</div>
					</div>
				</div>
			</van-cell>
			<van-cell>
				<div slot="title" class="user_member">
					<div class="user_avatar float-l">
						<img src="../../../assets/images/avatar_default.png" alt="头像">
					</div>
					<div class="user_info">
						<div>张三</div>
						<div>一级会员</div>
					</div>
				</div>
			</van-cell>
		</van-cell-group>

	</div>
</template>

<script>
export default {};
</script>

<style lang="scss" scoped>
.user_team {
  background-color: #fff;
}
.user_member {
  .user_avatar {
    height: 55px;
    width: 55px;
    border-radius: 50%;
    overflow: hidden;
    img {
      max-width: 100%;
      max-height: 100%;
    }
  }

  .user_info {
    margin-left: 70px;
  }
}
</style>
