
export interface ServerResponseWithBody<T> {
  body: T;
  message: string;
  ok: boolean;
  responseCode: number;
}

export interface LoginViewModel {
  username: string;
  password: string;
}

export interface UserInfoViewModel {
  companyId: number;
  productId: number;
  permissions: PermissionViewModel[];
  userId: number;
  sub: string;
  jwt: string;
}

export interface PermissionViewModel {
  authority: string;
}

export interface JwtViewModel {
  jwt: string;
}

export interface IVerificationOTPRequest {
    username  : string;
    otp       : string;
    productId : number;
    jwt       : string;
}
