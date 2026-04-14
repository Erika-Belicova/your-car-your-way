import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentConversationList } from './agent-conversation-list';

describe('AgentConversationList', () => {
  let component: AgentConversationList;
  let fixture: ComponentFixture<AgentConversationList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentConversationList],
    }).compileComponents();

    fixture = TestBed.createComponent(AgentConversationList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
