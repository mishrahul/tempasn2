import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  title: string;
  message: string;
  buttons: { text: string, value: any, color?: string }[]; 
}

@Component({
  selector: 'app-app-dialog',
  standalone: true,
  imports: [MatButtonModule, CommonModule],
  templateUrl: './app-dialog.component.html',
  styleUrl: './app-dialog.component.scss'
})
export class AppDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<AppDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  onClick(value: any): void {
    this.dialogRef.close(value);
  }
}

