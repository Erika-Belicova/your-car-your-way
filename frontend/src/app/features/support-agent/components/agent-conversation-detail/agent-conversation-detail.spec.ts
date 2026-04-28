import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentConversationDetail } from './agent-conversation-detail';

describe('AgentConversationDetail', () => {
  let component: AgentConversationDetail;
  let fixture: ComponentFixture<AgentConversationDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentConversationDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(AgentConversationDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
