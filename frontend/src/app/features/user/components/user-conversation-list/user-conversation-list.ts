import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ConversationService } from '../../../../core/services/conversation.service';
import { SupportConversationResponseDTO } from '../../../../core/interfaces/support-conversation/support-conversation-response-dto';
import { ConversationStatus } from '../../../../core/enumerations/conversation-status';

@Component({
  selector: 'app-user-conversation-list',
  imports: [DatePipe],
  templateUrl: './user-conversation-list.html',
  styleUrl: './user-conversation-list.scss',
})
export class UserConversationList implements OnInit {

  conversations: SupportConversationResponseDTO[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private conversationService: ConversationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // fetch all conversations of the authenticated user
    this.conversationService.getUserConversations().subscribe({
      next: conversations => {
        this.isLoading = false;
        this.conversations = conversations;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load conversations. Please try again.';
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

  // check if user has no conversations at all
  hasNoConversations(): boolean {
    return this.conversations.length === 0;
  }

  // conversation lists by status
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
    this.router.navigate(['/support-conversations', id]);
  }

  // navigate to new conversation form
  goToNewConversation(): void {
    this.router.navigate(['/support-conversations/new']);
  }

  // navigate back to dashboard
  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

}