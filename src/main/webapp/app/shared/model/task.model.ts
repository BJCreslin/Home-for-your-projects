import dayjs from 'dayjs';
import { IComment } from 'app/shared/model/comment.model';
import { IProject } from 'app/shared/model/project.model';
import { TaskAndProjectStatus } from 'app/shared/model/enumerations/task-and-project-status.model';

export interface ITask {
  id?: number;
  author?: string | null;
  implementer?: string | null;
  name?: string;
  text?: string | null;
  comment?: string | null;
  status?: TaskAndProjectStatus | null;
  created?: string | null;
  edited?: string | null;
  comments?: IComment[] | null;
  project?: IProject | null;
}

export const defaultValue: Readonly<ITask> = {};
