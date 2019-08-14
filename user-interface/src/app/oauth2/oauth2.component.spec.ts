import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Oauth2Component } from './oauth2.component';

describe('Oauth2Component', () => {
  let component: Oauth2Component;
  let fixture: ComponentFixture<Oauth2Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Oauth2Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Oauth2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
