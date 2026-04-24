import { Component, OnInit, OnDestroy, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { ConversationService } from '../../../../core/services/conversation.service';
import { ChatService } from '../../../../core/services/chat.service';
import { SupportConversationDetailDTO } from '../../../../core/interfaces/support-conversation/support-conversation-detail-dto';
import { ChatMessageResponseDTO } from '../../../../core/interfaces/chat/chat-message-response-dto';
import { ConversationStatus } from '../../../../core/enumerations/conversation-status';
import { ChatNotificationDTO } from '../../../../core/interfaces/chat/chat-notification-dto';

@Component({
  selector: 'app-user-conversation-detail',
  imports: [FormsModule, DatePipe],
  templateUrl: './user-conversation-detail.html',
  styleUrl: './user-conversation-detail.scss',
})
export class UserConversationDetail implements OnInit, OnDestroy {

  @ViewChild('chatWindow') chatWindow!: ElementRef;

  conversation: SupportConversationDetailDTO | null = null;
  chatItems: Array<ChatMessageResponseDTO | { type: 'notification', text: string }> = [];
  newMessage = '';
  isLoading = true;
  errorMessage = '';
  ConversationStatus = ConversationStatus;

  private messageSubscription: Subscription | null = null;
  private notificationSubscription: Subscription | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private conversationService: ConversationService,
    private chatService: ChatService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadConversation(id);
  }

  private loadConversation(id: number): void {
    this.conversationService.getConversationById(id).subscribe({
      next: conversation => {
        this.conversation = conversation;
        this.isLoading = false;

        // map message history to chat items for display
        this.chatItems = this.mapMessagesToChatItems(conversation);

        // connect to WebSocket for real-time updates
        this.connectToWebSocket(conversation.chatSessionId);

        // scroll to the bottom of the chat window
        this.scrollToBottom();
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'This support conversation could not be found or is no longer available.';
        this.cdr.detectChanges();
      }
    });
  }

  // map message history from REST response to chat items
  private mapMessagesToChatItems(conversation: SupportConversationDetailDTO
  ): Array<ChatMessageResponseDTO | { type: 'notification', text: string }> {

    const messages: Array<ChatMessageResponseDTO | { type: 'notification', text: string }> =
      conversation.messages.map(m => ({
        chatSessionId: conversation.chatSessionId,
        content: m.content,
        senderType: m.senderType,
        sentAt: m.sentAt
      }));

    // add waiting notification after initial message for open conversations
    if (conversation.status === ConversationStatus.OPEN) {
      messages.push({
        type: 'notification',
        text: 'Waiting for a support agent to connect to the conversation.'
      });
    }

    return messages;
  }

  private connectToWebSocket(chatSessionId: string): void {
    // connect to WebSocket and subscribe to messages and notifications
    this.chatService.connect(chatSessionId);
    this.messageSubscription = this.chatService.messages$.subscribe(
      message => this.handleIncomingMessage(message)
    );
    this.notificationSubscription = this.chatService.notifications$.subscribe(
      notification => this.handleIncomingNotification(notification)
    );
  }

  // handle incoming chat message and append to chat window
  private handleIncomingMessage(message: ChatMessageResponseDTO): void {
    this.chatItems.push(message);
    this.scrollToBottom();
    this.cdr.detectChanges();
  }

  // handle incoming notification and update conversation status if changed
  private handleIncomingNotification(notification: ChatNotificationDTO): void {
    // add notification message to chat window if present
    if (notification.notificationMessage) {
      this.chatItems.push({ type: 'notification', text: notification.notificationMessage });
    }
    // update conversation status if notification contains a status change
    if (notification.status && this.conversation) {
      this.conversation.status = notification.status;
    }
    // update status updated timestamp if notification contains it
    if (notification.updatedAt && this.conversation) {
      this.conversation.updated_at = notification.updatedAt;
    }
    this.scrollToBottom();
    this.cdr.detectChanges();
  }

  // type guard to distinguish messages from notifications
  isMessage(item: ChatMessageResponseDTO | { type: 'notification', text: string }
  ): item is ChatMessageResponseDTO {
    return 'senderType' in item;
  }

  // send a message via WebSocket
  sendMessage(): void {
    if (!this.newMessage.trim() || !this.conversation) return;
    this.chatService.sendMessage({
      chatSessionId: this.conversation.chatSessionId,
      content: this.newMessage.trim()
    });
    this.newMessage = '';
    this.scrollToBottom();
  }

  // scroll chat window to the bottom
  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.chatWindow) {
        this.chatWindow.nativeElement.scrollTop = 
          this.chatWindow.nativeElement.scrollHeight;
      }
    }, 50);
  }

  // check if conversation is active
  isActive(): boolean {
    return this.conversation?.status === ConversationStatus.ACTIVE;
  }

  // navigate back to conversation list
  goBack(): void {
    this.router.navigate(['/support-conversations']);
  }

  ngOnDestroy(): void {
    // disconnect WebSocket and unsubscribe from message and notification streams
    this.messageSubscription?.unsubscribe();
    this.notificationSubscription?.unsubscribe();
    this.chatService.disconnect(); // prevent memory leaks and orphaned connections
  }

}