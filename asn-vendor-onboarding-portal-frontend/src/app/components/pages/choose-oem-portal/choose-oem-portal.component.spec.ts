import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChooseOemPortalComponent } from './choose-oem-portal.component';

describe('ChooseOemPortalComponent', () => {
  let component: ChooseOemPortalComponent;
  let fixture: ComponentFixture<ChooseOemPortalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChooseOemPortalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChooseOemPortalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
