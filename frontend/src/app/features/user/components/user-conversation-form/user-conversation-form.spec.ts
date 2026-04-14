import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserConversationForm } from './user-conversation-form';

describe('UserConversationForm', () => {
  let component: UserConversationForm;
  let fixture: ComponentFixture<UserConversationForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserConversationForm],
    }).compileComponents();

    fixture = TestBed.createComponent(UserConversationForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
