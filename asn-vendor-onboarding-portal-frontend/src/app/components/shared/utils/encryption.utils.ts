import * as CryptoJS from 'crypto-js';
import { ENV_VARIABLES } from 'src/app/constants/env-variables';

export class EncryptionUtils {
  static encryptPayload(payload: any): string {
    return CryptoJS.AES.encrypt(
      JSON.stringify(payload).trim(),
      ENV_VARIABLES.SECRET_KEY.trim()
    ).toString();
  }
}
