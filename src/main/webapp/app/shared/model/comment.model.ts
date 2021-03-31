import dayjs from 'dayjs';
import { ITask } from 'app/shared/model/task.model';
import { CommentStatus } from 'app/shared/model/enumerations/comment-status.model';

export interface IComment {
  id?: number;
  author?: string | null;
  text?: string | null;
  status?: CommentStatus | null;
  created?: string | null;
  edited?: string | null;
  task?: ITask | null;
}

export const defaultValue: Readonly<IComment> = {};
