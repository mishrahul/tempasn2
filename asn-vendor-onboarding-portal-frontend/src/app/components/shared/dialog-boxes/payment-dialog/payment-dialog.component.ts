import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-payment-dialog',
  standalone: true,
  imports: [
    FormsModule,
    MatDialogModule,   // dialog container
    MatButtonModule,   // buttons
    MatRadioModule     // ✅ radio buttons
  ],
  templateUrl: './payment-dialog.component.html',
  styleUrl: './payment-dialog.component.scss'
})
export class PaymentDialogComponent {
  selectedMethod: string | null = null;
  totalAmount: number = 20060;

  constructor(
    private dialogRef: MatDialogRef<PaymentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public notificationService: NotificationService
  ) {}

  selectPaymentMethod(method: string) {
    this.selectedMethod = method;
  }

  closePaymentModal(value?: boolean) {
    this.dialogRef.close(value);
  }

  processPayment() {
    if (!this.selectedMethod) {
      console.warn('⚠️ Please select a payment method first.');
      this.notificationService.error('⚠️ Please select a payment method first.')
      return;
    }

    if (this.selectedMethod) {
      this.dialogRef.close({ method: this.selectedMethod, amount: 20060 });
      this.notificationService.success(`✅ Processing payment with ${this.selectedMethod}, amount: ₹${this.totalAmount}`)
      console.log(`✅ Processing payment with ${this.selectedMethod}, amount: ₹${this.totalAmount}`);
      this.closePaymentModal(true)
    }

  }
  
}
