import dayjs from 'dayjs';
import { ITask } from 'app/shared/model/task.model';
import { ProjectStatus } from 'app/shared/model/enumerations/project-status.model';

export interface IProject {
  id?: number;
  projectUrl?: string | null;
  description?: string | null;
  projectName?: string;
  comment?: string | null;
  status?: ProjectStatus | null;
  created?: string | null;
  edited?: string | null;
  tasks?: ITask[] | null;
}

export const defaultValue: Readonly<IProject> = {};
