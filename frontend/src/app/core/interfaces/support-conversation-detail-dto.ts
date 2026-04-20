import { SupportMessageResponseDTO } from './support-message-response-dto';

export interface SupportConversationDetailDTO {
  id: number;
  chatSessionId: string;
  subject: string;
  status: string;
  messages: SupportMessageResponseDTO[];
  created_at: string;
  updated_at: string;
}