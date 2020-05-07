import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewSlaModalComponent } from './view-sla-modal.component';

describe('ViewSlaModalComponent', () => {
  let component: ViewSlaModalComponent;
  let fixture: ComponentFixture<DeleteSlaModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ViewSlaModalComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewSlaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
