import { ConversationStatus } from '../../enumerations/conversation-status';

export interface SupportConversationResponseDTO {
  id: number;
  chatSessionId: string;
  subject: string;
  status: ConversationStatus;
  created_at: string;
  updated_at: string;
}