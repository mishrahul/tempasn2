export interface OEM {
  id: string;
  logoBackground: string;
  oemCode: string;
  fullName: string;
  features: string[];
  isComingSoon?: boolean;
  noAccess?: boolean;
}
export interface OemResponseBody {
  oems: OEM[];
  totalCount: number;
  message: string;
}
export interface SelectedOEM {
  id: string;
  fullName: string;
  oemCode: string;
  logoBackground: string;
}