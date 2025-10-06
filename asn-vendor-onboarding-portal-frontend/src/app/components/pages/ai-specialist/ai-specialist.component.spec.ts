import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AiSpecialistComponent } from './ai-specialist.component';

describe('AiSpecialistComponent', () => {
  let component: AiSpecialistComponent;
  let fixture: ComponentFixture<AiSpecialistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AiSpecialistComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AiSpecialistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
