import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
    constructor(private toastr: ToastrService) {}

  success(message: string, title?: string, duration?: number) {
    // this.toastr.clear()
    this.toastr.success(message, title || 'Success',  {timeOut: duration ?? 5000});
  }

  error(message: string, title?: string, duration?: number) {
    // this.toastr.clear()
    this.toastr.error(message, title || 'Error',  {timeOut: duration ?? 50000});
  }

  warning(message: string, title?: string, duration?: number) {
    // this.toastr.clear()
    this.toastr.warning(message, title || 'Warning',  {timeOut: duration ?? 5000});
  }

  info(message: string, title?: string, duration?: number) {
    // this.toastr.clear()
    this.toastr.info(message, title || 'Info',  {timeOut: duration ?? 5000});
  }

  clear(){
    this.toastr.clear()
  }
}
