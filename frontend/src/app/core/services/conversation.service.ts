import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SupportConversationRequestDTO } from '../interfaces/support-conversation-request-dto';
import { SupportConversationResponseDTO } from '../interfaces/support-conversation-response-dto';
import { SupportConversationDetailDTO } from '../interfaces/support-conversation-detail-dto';
import { ConversationStatus } from '../interfaces/conversation-status';

@Injectable({
  providedIn: 'root',
})
export class ConversationService {

  constructor(private http: HttpClient) {}

  // create a new support conversation with an initial message
  createConversation(
    request: SupportConversationRequestDTO
  ): Observable<SupportConversationResponseDTO> {
    return this.http.post<SupportConversationResponseDTO>(
      `${environment.apiUrl}/support-conversations`, request);
  }

  // get all conversations of the authenticated user
  getUserConversations(): Observable<SupportConversationResponseDTO[]> {
    return this.http.get<SupportConversationResponseDTO[]>(
      `${environment.apiUrl}/support-conversations`);
  }

  // get all conversations for the support agent
  getAllConversations(): Observable<SupportConversationResponseDTO[]> {
    return this.http.get<SupportConversationResponseDTO[]>(
      `${environment.apiUrl}/support-conversations/all`);
  }

  // get a conversation by id with full message history
  getConversationById(id: number): Observable<SupportConversationDetailDTO> {
    return this.http.get<SupportConversationDetailDTO>(
      `${environment.apiUrl}/support-conversations/${id}`);
  }

  // support agent can update conversation status manually
  updateConversationStatus(id: number, status: ConversationStatus
  ): Observable<SupportConversationResponseDTO> {
    return this.http.patch<SupportConversationResponseDTO>(
      `${environment.apiUrl}/support-conversations/${id}/status?status=${status}`,
      {}
    );
  }

}
