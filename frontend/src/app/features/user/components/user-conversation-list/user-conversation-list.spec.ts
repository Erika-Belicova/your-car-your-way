import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserConversationList } from './user-conversation-list';

describe('UserConversationList', () => {
  let component: UserConversationList;
  let fixture: ComponentFixture<UserConversationList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserConversationList],
    }).compileComponents();

    fixture = TestBed.createComponent(UserConversationList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
