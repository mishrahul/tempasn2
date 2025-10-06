import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelfDeploymentFormComponent } from './self-deployment-form.component';

describe('SelfDeploymentFormComponent', () => {
  let component: SelfDeploymentFormComponent;
  let fixture: ComponentFixture<SelfDeploymentFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelfDeploymentFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelfDeploymentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
