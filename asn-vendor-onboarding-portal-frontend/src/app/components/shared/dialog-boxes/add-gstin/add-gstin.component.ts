import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { INDIAN_STATES } from 'src/app/constants/indian-states';

@Component({
  selector: 'app-add-gstin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule
  ],
  templateUrl: './add-gstin.component.html',
  styleUrl: './add-gstin.component.scss'
})
export class AddGstinComponent {
  gstinForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddGstinComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    const gstin = this.data?.gstin || '';
    const state = this.data?.state || '';
    const stateCode = this.data?.stateCode || '';
    const primary = this.data?.primary || false;
    const isEditMode = !!this.data?.isEditMode; // Check if editing existing GSTIN

    console.log('this.data::',this.data)
    const formConfig: any = {
      gstin: [gstin, [Validators.required, Validators.pattern(/^(0[1-9]|1[0-9]|2[0-9]|3[0-8])[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/)]],
      state: [{value: state, disabled: true}, Validators.required],
      stateCode: [stateCode],
      primary: [primary]
    };

    // Only add vendorCode if in edit mode
    if (isEditMode) {
      formConfig.gstin = [{value: gstin, disabled: true}, [Validators.required, Validators.pattern(/^(0[1-9]|1[0-9]|2[0-9]|3[0-8])[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/)]];
      formConfig.vendorCode = [this.data?.vendorCode || '', Validators.required];
    }
    
    this.gstinForm = this.fb.group(formConfig);

    // Subscribe to GSTIN changes to auto-populate state
    this.gstinForm.get('gstin')?.valueChanges.subscribe(gstin => {
      this.updateStateFromGSTIN(gstin);
    });
  }

  private updateStateFromGSTIN(gstin: string): void {
    if (gstin && gstin.length >= 2) {
      const stateCode = gstin.substring(0, 2);
      const state = INDIAN_STATES.find(s => s.code === stateCode);
      
      if (state) {
        this.gstinForm.get('state')?.setValue(state.name);
        this.gstinForm.get('stateCode')?.setValue(state.code);
      } else {
        this.gstinForm.get('state')?.setValue('');
        this.gstinForm.get('stateCode')?.setValue('');
      }
    } else {
      this.gstinForm.get('state')?.setValue('');
      this.gstinForm.get('stateCode')?.setValue('');
    }
  }

  onSubmit(): void {
    if (this.gstinForm.valid) {
      this.gstinForm.value.id = this.data?.id,
      this.dialogRef.close(this.gstinForm.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  get isFormValid(): boolean {
    const gstinControl = this.gstinForm.get('gstin');
    const gstinValid = gstinControl?.disabled ? !!gstinControl.value : gstinControl?.valid;
    const stateHasValue = !!this.gstinForm.get('state')?.value;
    
    // In edit mode, also check vendor code
    if (this.data?.isEditMode) {
      const vendorCodeValid = this.gstinForm.get('vendorCode')?.valid;
      return !!gstinValid && stateHasValue && !!vendorCodeValid;
    }
    
    return !!gstinValid && stateHasValue;
  }
}

