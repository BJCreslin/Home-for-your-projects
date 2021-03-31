import dayjs from 'dayjs';
import { UserStatus } from 'app/shared/model/enumerations/user-status.model';

export interface IUserInfo {
  id?: number;
  email?: string;
  gitHubId?: string | null;
  name?: string;
  hours?: number | null;
  status?: UserStatus | null;
  birthday?: string | null;
  comment?: string | null;
  created?: string | null;
  edited?: string | null;
}

export const defaultValue: Readonly<IUserInfo> = {};
