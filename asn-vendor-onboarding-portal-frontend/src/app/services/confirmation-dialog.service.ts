import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../components/shared/dialog-boxes/confirmation-dialog/confirmation-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class ConfirmationDialogService {

  constructor(private dialog: MatDialog) {}

  confirm(data: ConfirmationDialogData): Observable<boolean> {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '450px',
      disableClose: true,
      data: data
    });

    return dialogRef.afterClosed();
  }

  // Convenience methods
  confirmDelete(itemName: string = 'item'): Observable<boolean> {
    return this.confirm({
      title: 'Delete Confirmation',
      message: `Are you sure you want to delete this ${itemName}? This action cannot be undone.`,
      confirmText: 'Delete',
      cancelText: 'Cancel',
      type: 'danger',
      icon: '🗑️'
    });
  }

  confirmAction(title: string, message: string, type: 'warning' | 'danger' | 'info' | 'success' = 'info'): Observable<boolean> {
    return this.confirm({
      title,
      message,
      type,
      confirmText: 'Confirm',
      cancelText: 'Cancel'
    });
  }
}