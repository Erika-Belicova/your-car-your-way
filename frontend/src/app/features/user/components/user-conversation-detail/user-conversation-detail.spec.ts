import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserConversationDetail } from './user-conversation-detail';

describe('UserConversationDetail', () => {
  let component: UserConversationDetail;
  let fixture: ComponentFixture<UserConversationDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserConversationDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(UserConversationDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
