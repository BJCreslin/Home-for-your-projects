import dayjs from 'dayjs';
import { MessageStatus } from 'app/shared/model/enumerations/message-status.model';

export interface IMessage {
  id?: number;
  author?: string | null;
  recepient?: string | null;
  text?: string | null;
  status?: MessageStatus | null;
  created?: string | null;
  edited?: string | null;
}

export const defaultValue: Readonly<IMessage> = {};
