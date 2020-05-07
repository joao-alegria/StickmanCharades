import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScrollupComponent } from './scrollup.component';

describe('ScrollupComponent', () => {
  let component: ScrollupComponent;
  let fixture: ComponentFixture<ScrollupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScrollupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScrollupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
