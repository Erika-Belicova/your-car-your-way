import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject } from 'rxjs';
import { TokenService } from './token.service';
import { ChatMessageRequestDTO } from '../interfaces/chat/chat-message-request-dto';
import { ChatMessageResponseDTO } from '../interfaces/chat/chat-message-response-dto';
import { ChatNotificationDTO } from '../interfaces/chat/chat-notification-dto';
import { ChatStatusUpdateDTO } from '../interfaces/chat/chat-status-update-dto';

@Injectable({
  providedIn: 'root',
})
export class ChatService {

  private client: Client | null = null;
  private subscription: StompSubscription | null = null;

  // subjects for broadcasting received messages and notifications
  private messageSubject = new Subject<ChatMessageResponseDTO>();
  private notificationSubject = new Subject<ChatNotificationDTO>();

  // observables for components to subscribe to
  messages$ = this.messageSubject.asObservable();
  notifications$ = this.notificationSubject.asObservable();

  constructor(private tokenService: TokenService) {}

  // connect to WebSocket and subscribe to the conversation topic
  connect(chatSessionId: string): void {
    const token = this.tokenService.getAccessToken();

    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        // pass JWT token in STOMP CONNECT frame for authentication
        Authorization: `Bearer ${token}`
      },
      onConnect: () => {
        // subscribe to the conversation topic on successful connection
        this.subscription = this.client!.subscribe(
          `/topic/chat/${chatSessionId}`,
          (message: IMessage) => this.handleMessage(message)
        );
      }
    });

    this.client.activate();
  }

  // handle incoming WebSocket messages and route to correct subject
  private handleMessage(message: IMessage): void {
    const body = JSON.parse(message.body);

    if (body.senderType) {
      // message has senderType - it is a chat message
      this.messageSubject.next(body as ChatMessageResponseDTO);
    } else {
      // no senderType - it is a notification
      this.notificationSubject.next(body as ChatNotificationDTO);
    }
  }

  // send a chat message via WebSocket
  sendMessage(request: ChatMessageRequestDTO): void {
    if (this.client?.connected) {
      this.client.publish({
        destination: '/app/chat',
        body: JSON.stringify(request)
      });
    }
  }

  // send a status update via WebSocket
  sendStatusUpdate(update: ChatStatusUpdateDTO): void {
    if (this.client?.connected) {
      this.client.publish({
        destination: '/app/chat/status',
        body: JSON.stringify(update)
      });
    }
  }

  // disconnect from WebSocket and clean up
  disconnect(): void {
    this.subscription?.unsubscribe();
    this.client?.deactivate();
    this.client = null;
    this.subscription = null;
  }

}