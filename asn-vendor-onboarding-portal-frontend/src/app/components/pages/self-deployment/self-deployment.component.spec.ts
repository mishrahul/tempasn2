import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelfDeploymentComponent } from './self-deployment.component';

describe('SelfDeploymentComponent', () => {
  let component: SelfDeploymentComponent;
  let fixture: ComponentFixture<SelfDeploymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelfDeploymentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelfDeploymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
