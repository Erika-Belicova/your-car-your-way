import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ConversationService } from '../../../../core/services/conversation.service';
import { SupportConversationResponseDTO } from '../../../../core/interfaces/support-conversation/support-conversation-response-dto';
import { ConversationStatus } from '../../../../core/enumerations/conversation-status';

@Component({
  selector: 'app-agent-conversation-list',
  imports: [DatePipe],
  templateUrl: './agent-conversation-list.html',
  styleUrl: './agent-conversation-list.scss',
})
export class AgentConversationList implements OnInit, OnDestroy {

  conversations: SupportConversationResponseDTO[] = [];
  isLoading = true;
  errorMessage = '';
  openDropdownId: number | null = null;
  ConversationStatus = ConversationStatus;
  private pollingInterval: number | null = null;

  constructor(
    private conversationService: ConversationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadConversations();
    // refresh conversations every 30 seconds
    this.pollingInterval = setInterval(() => this.loadConversations(), 30000);
  }

  ngOnDestroy(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  // manual refresh
  refresh(): void {
    this.loadConversations();
  }

  // fetch all support conversations from the API
  private loadConversations(): void {
    this.conversationService.getAllConversations().subscribe({
      next: conversations => {
        this.isLoading = false;
        this.conversations = conversations;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load conversations. Please try again.';
        this.cdr.detectChanges();
      }
    });
  }

  // filter conversations by status sorted by most recently updated
  private getByStatus(status: ConversationStatus): SupportConversationResponseDTO[] {
    return this.conversations
      .filter(c => c.status === status)
      // the most recently updated conversation is on top of each category
      .sort((a, b) => new Date(b.updated_at).getTime() - new Date(a.updated_at).getTime());
  }

  get activeConversations(): SupportConversationResponseDTO[] {
    return this.getByStatus(ConversationStatus.ACTIVE);
  }

  get waitingConversations(): SupportConversationResponseDTO[] {
    return this.getByStatus(ConversationStatus.WAITING);
  }

  get openConversations(): SupportConversationResponseDTO[] {
    return this.getByStatus(ConversationStatus.OPEN);
  }

  get closedConversations(): SupportConversationResponseDTO[] {
    return this.getByStatus(ConversationStatus.CLOSED);
  }

  // navigate to conversation detail
  openConversation(id: number): void {
    this.router.navigate(['/agent-access/support-conversations', id]);
  }

  // toggle status dropdown for a conversation
  toggleDropdown(id: number, event: Event): void {
    event.stopPropagation();
    this.openDropdownId = this.openDropdownId === id ? null : id;
  }

  // update conversation status and reload conversations
  updateStatus(id: number, status: ConversationStatus, event: Event): void {
    event.stopPropagation();
    this.conversationService.updateConversationStatus(id, status).subscribe({
      next: () => {
        this.openDropdownId = null;
        this.loadConversations();
      },
      error: () => {
        this.errorMessage = 'Failed to update status. Please try again.';
        this.cdr.detectChanges();
      }
    });
  }

  // get available status options excluding current and PENDING_RESPONSE
  getStatusOptions(current: ConversationStatus): ConversationStatus[] {
    return Object.values(ConversationStatus).filter(
      s => s !== current && s !== ConversationStatus.PENDING_RESPONSE
    );
  }

}