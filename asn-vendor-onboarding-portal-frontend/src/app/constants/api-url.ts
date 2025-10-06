export const API_URL = {
  login: {
      login: "auth/authenticate/2fa",
        checkTokenValidity:"auth/validateV2",
        loginOtpCheck: 'auth/authenticate/2fa/validate',
        signUp: 'auth/signup',
        checkVerificationCode: 'user/checkverificationcode',
        setPassword: 'auth/setpwd',
        forgotPassword: 'auth/forgotpwdmailV2',
        verifyMail: 'auth/verifymail',
        resendOtp: 'auth/authenticate/2fa/generate',
        getUserProfile: 'api/userProfile',
        getUserProfileImage: 'api/profileImage',
  }
};