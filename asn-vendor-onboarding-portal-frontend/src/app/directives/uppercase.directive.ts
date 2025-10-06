import { Directive, HostListener, ElementRef, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[appUppercase]',
  standalone: true  // ✅ Important in Angular 17 standalone apps
})
export class UppercaseDirective {
  private el = inject(ElementRef);
  private control = inject(NgControl);

  @HostListener('input', ['$event']) onInput(event: Event) {
    const start = this.el.nativeElement.selectionStart;
    const end = this.el.nativeElement.selectionEnd;

    const upper = this.el.nativeElement.value.toUpperCase();
    this.el.nativeElement.value = upper;

    // ✅ Sync with form control
    this.control.control?.setValue(upper, { emitEvent: false });

    // ✅ Restore cursor position
    this.el.nativeElement.setSelectionRange(start, end);
  }
}